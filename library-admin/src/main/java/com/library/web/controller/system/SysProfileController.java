package com.library.web.controller.system;

import java.util.Map;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.EnvironmentVariableCredentialsProvider;
import com.aliyun.oss.common.comm.SignVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyuncs.exceptions.ClientException;
import com.library.common.annotation.Log;
import com.library.common.core.controller.BaseController;
import com.library.common.core.domain.AjaxResult;
import com.library.common.core.domain.entity.SysUser;
import com.library.common.core.domain.model.LoginUser;
import com.library.common.enums.BusinessType;
import com.library.common.utils.DateUtils;
import com.library.common.utils.SecurityUtils;
import com.library.common.utils.StringUtils;
import com.library.common.utils.file.FileUploadUtils;
import com.library.common.utils.file.MimeTypeUtils;
import lombok.SneakyThrows;
import com.library.framework.web.service.TokenService;
import com.library.system.service.ISysUserService;

/**
 * 个人信息 业务处理
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/user/profile")
public class SysProfileController extends BaseController
{
    @Autowired
    private ISysUserService userService;

    @Autowired
    private TokenService tokenService;

    /**
     * 个人信息
     */
    @GetMapping
    public AjaxResult profile()
    {
        LoginUser loginUser = getLoginUser();
        SysUser user = loginUser.getUser();
        AjaxResult ajax = AjaxResult.success(user);
        ajax.put("roleGroup", userService.selectUserRoleGroup(loginUser.getUsername()));
        ajax.put("postGroup", userService.selectUserPostGroup(loginUser.getUsername()));
        return ajax;
    }

    /**
     * 修改用户
     */
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult updateProfile(@RequestBody SysUser user)
    {
        LoginUser loginUser = getLoginUser();
        SysUser currentUser = loginUser.getUser();
        currentUser.setNickName(user.getNickName());
        currentUser.setEmail(user.getEmail());
        currentUser.setPhonenumber(user.getPhonenumber());
        currentUser.setSex(user.getSex());
        if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(currentUser))
        {
            return error("修改用户'" + loginUser.getUsername() + "'失败，手机号码已存在");
        }
        if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(currentUser))
        {
            return error("修改用户'" + loginUser.getUsername() + "'失败，邮箱账号已存在");
        }
        if (userService.updateUserProfile(currentUser) > 0)
        {
            // 更新缓存用户信息
            tokenService.setLoginUser(loginUser);
            return success();
        }
        return error("修改个人信息异常，请联系管理员");
    }

    /**
     * 重置密码
     */
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping("/updatePwd")
    public AjaxResult updatePwd(@RequestBody Map<String, String> params)
    {
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        LoginUser loginUser = getLoginUser();
        Long userId = loginUser.getUserId();
        SysUser user = userService.selectUserById(userId);
        String password = user.getPassword();
        if (!SecurityUtils.matchesPassword(oldPassword, password))
        {
            return error("修改密码失败，旧密码错误");
        }
        if (SecurityUtils.matchesPassword(newPassword, password))
        {
            return error("新密码不能与旧密码相同");
        }
        newPassword = SecurityUtils.encryptPassword(newPassword);
        if (userService.resetUserPwd(userId, newPassword) > 0)
        {
            // 更新缓存用户密码&密码最后更新时间
            loginUser.getUser().setPwdUpdateDate(DateUtils.getNowDate());
            loginUser.getUser().setPassword(newPassword);
            tokenService.setLoginUser(loginUser);
            return success();
        }
        return error("修改密码异常，请联系管理员");
    }

    /**
     * 头像上传
     */
    @Log(title = "用户头像", businessType = BusinessType.UPDATE)
    @PostMapping("/avatar")
    @SneakyThrows
    public AjaxResult avatar(@RequestParam("avatarfile") MultipartFile file) throws Exception
    {
        if (!file.isEmpty())
        {
            // 验证文件类型和大小
            FileUploadUtils.assertAllowed(file, MimeTypeUtils.IMAGE_EXTENSION);
            // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
            String endpoint = "https://oss-cn-guangzhou.aliyuncs.com";
            // 从环境变量中获取访问凭证。运行本代码示例之前，请确保已设置环境变量OSS_ACCESS_KEY_ID和OSS_ACCESS_KEY_SECRET。
            EnvironmentVariableCredentialsProvider credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();
            // 填写Bucket名称，例如examplebucket。
            String bucketName = "noname1";
            // 生成上传到OSS的文件名，使用时间戳+原始文件名避免重复
            String originalFilename = file.getOriginalFilename();
            String objectName = "avatar/" + System.currentTimeMillis() + "_" + originalFilename;
            // 填写Bucket所在地域。
            String region = "cn-guangzhou";
            // 创建OSSClient实例。
            // 当OSSClient实例不再使用时，调用shutdown方法以释放资源。
            ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
            clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);
            OSS ossClient = OSSClientBuilder.create()
                    .endpoint(endpoint)
                    .credentialsProvider(credentialsProvider)
                    .clientConfiguration(clientBuilderConfiguration)
                    .region(region)
                    .build();
            try {
                // 上传文件流到OSS
                ossClient.putObject(bucketName, objectName, file.getInputStream());
                // 构建访问URL
                String avatarUrl = "https://" + bucketName + "." + endpoint.replace("https://", "") + "/" + objectName;
                
                LoginUser loginUser = getLoginUser();
                if (userService.updateUserAvatar(loginUser.getUserId(), avatarUrl))
                {
                    // 更新缓存用户头像
                    loginUser.getUser().setAvatar(avatarUrl);
                    tokenService.setLoginUser(loginUser);
                    
                    AjaxResult ajax = AjaxResult.success();
                    ajax.put("imgUrl", avatarUrl);
                    return ajax;
                }
            } catch (OSSException oe) {
                logger.error("OSSException occurred: {}", oe.getErrorMessage(), oe);
                return error("文件上传失败：" + oe.getErrorMessage());
            } catch (Exception e) {
                logger.error("文件上传异常：{}", e.getMessage(), e);
                return error("文件上传异常，请联系管理员");
            } finally {
                if (ossClient != null) {
                    ossClient.shutdown();
                }
            }
        }
        return error("上传图片异常，请联系管理员");
    }
}
