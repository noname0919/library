package com.library.web.controller.common;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aliyun.oss.*;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.EnvironmentVariableCredentialsProvider;
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.OSSObject;
import lombok.SneakyThrows;
import org.apache.commons.compress.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.library.common.config.RuoYiConfig;
import com.library.common.core.domain.AjaxResult;
import com.library.common.utils.StringUtils;
import com.library.common.utils.file.FileUploadUtils;
import com.library.common.utils.file.FileUtils;
import com.library.framework.config.ServerConfig;

/**
 * 通用请求处理
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/common")
public class CommonController
{
    private static final Logger log = LoggerFactory.getLogger(CommonController.class);

    @Autowired
    private ServerConfig serverConfig;

    private static final String FILE_DELIMITER = ",";

    /**
     * 通用下载请求
     *
     * @param fileName 文件名称
     * @param delete 是否删除
     */
    @GetMapping("/download")
    public void fileDownload(String fileName, Boolean delete, HttpServletResponse response, HttpServletRequest request) {
        try {
            // 检查文件名是否合法（这里可能需要根据OSS的路径规则调整）
            if (fileName == null || fileName.isEmpty()) {
                throw new Exception("文件名称不能为空");
            }

            // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
            String endpoint = "https://oss-cn-guangzhou.aliyuncs.com";
            // 从环境变量中获取访问凭证。运行本代码示例之前，请确保已设置环境变量OSS_ACCESS_KEY_ID和OSS_ACCESS_KEY_SECRET。
            EnvironmentVariableCredentialsProvider credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();
            // 填写Bucket名称，例如examplebucket。
            String bucketName = "noname1";
            // 填写Bucket所在地域。
            String region = "cn-guangzhou";

            // 创建OSSClient实例。
            ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
            clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);
            OSS ossClient = OSSClientBuilder.create()
                    .endpoint(endpoint)
                    .credentialsProvider(credentialsProvider)
                    .clientConfiguration(clientBuilderConfiguration)
                    .region(region)
                    .build();

            try {
                // 从OSS获取文件对象
                OSSObject ossObject = ossClient.getObject(bucketName, fileName);

                // 设置响应头
                String realFileName = System.currentTimeMillis() + "_" + FileUtils.getName(fileName);
                response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
                FileUtils.setAttachmentResponseHeader(response, realFileName);

                // 将OSS文件流写入响应输出流
                IOUtils.copy(ossObject.getObjectContent(), response.getOutputStream());

                // 关闭OSS对象
                ossObject.close();

                // OSS中不支持直接删除文件（delete参数在OSS场景下无意义）
                if (delete != null && delete) {
                    log.warn("OSS不支持下载后删除操作，如需删除请调用OSS删除接口");
                }
            } catch (OSSException oe) {
                System.out.println("Caught an OSSException, which means your request made it to OSS, "
                        + "but was rejected with an error response for some reason.");
                System.out.println("Error Message:" + oe.getErrorMessage());
                System.out.println("Error Code:" + oe.getErrorCode());
                System.out.println("Request ID:" + oe.getRequestId());
                System.out.println("Host ID:" + oe.getHostId());
                log.error("从OSS下载文件失败", oe);
            } catch (ClientException ce) {
                System.out.println("Caught an ClientException, which means the client encountered "
                        + "a serious internal problem while trying to communicate with OSS, "
                        + "such as not being able to access the network.");
                System.out.println("Error Message:" + ce.getMessage());
                log.error("OSS客户端异常", ce);
            } finally {
                if (ossClient != null) {
                    ossClient.shutdown();
                }
            }
        } catch (Exception e) {
            log.error("下载文件失败", e);
        }
    }


    /**
     * 通用上传请求（单个）
     */

    @PostMapping("/upload")
    @SneakyThrows
    public AjaxResult uploadFile(MultipartFile file) {
        if (file.isEmpty()) {
            return AjaxResult.error("上传文件不能为空");
        }
        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
        String endpoint = "https://oss-cn-guangzhou.aliyuncs.com";
        // 从环境变量中获取访问凭证。运行本代码示例之前，请确保已设置环境变量OSS_ACCESS_KEY_ID和OSS_ACCESS_KEY_SECRET。
        EnvironmentVariableCredentialsProvider credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();
        // 填写Bucket名称，例如examplebucket。
        String bucketName = "noname1";
        // 生成上传到OSS的文件名，使用时间戳+原始文件名避免重复
        String originalFilename = file.getOriginalFilename();
        String objectName = "uploads/" + System.currentTimeMillis() + "_" + originalFilename;
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
            String url = "https://" + bucketName + "." + endpoint.replace("https://", "") + "/" + objectName;
            // 返回成功结果
            AjaxResult ajax = AjaxResult.success();
            ajax.put("url", url);
            ajax.put("fileName", objectName);
            ajax.put("originalFilename", originalFilename);
            return ajax;
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
            return AjaxResult.error("文件上传失败：" + oe.getErrorMessage());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
            return AjaxResult.error("文件上传失败：" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
    /**
     * 通用上传请求（多个）
     */
    @PostMapping("/uploads")
    public AjaxResult uploadFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return AjaxResult.error("上传文件不能为空");
        }

        try {
            // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
            String endpoint = "https://oss-cn-guangzhou.aliyuncs.com";
            // 从环境变量中获取访问凭证。运行本代码示例之前，请确保已设置环境变量OSS_ACCESS_KEY_ID和OSS_ACCESS_KEY_SECRET。
            EnvironmentVariableCredentialsProvider credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();
            // 填写Bucket名称，例如examplebucket。
            String bucketName = "noname1";
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
                List<String> urls = new ArrayList<String>();
                List<String> fileNames = new ArrayList<String>();
                List<String> newFileNames = new ArrayList<String>();
                List<String> originalFilenames = new ArrayList<String>();

                for (MultipartFile file : files) {
                    if (file.isEmpty()) {
                        continue; // 跳过空文件
                    }

                    // 生成上传到OSS的文件名，使用时间戳+原始文件名避免重复
                    String originalFilename = file.getOriginalFilename();
                    String objectName = "uploads/" + System.currentTimeMillis() + "_" + originalFilename;

                    // 上传文件流到OSS
                    ossClient.putObject(bucketName, objectName, file.getInputStream());

                    // 构建访问URL
                    String url = "https://" + bucketName + "." + endpoint.replace("https://", "") + "/" + objectName;

                    urls.add(url);
                    fileNames.add(objectName);
                    newFileNames.add(FileUtils.getName(objectName));
                    originalFilenames.add(file.getOriginalFilename());
                }

                AjaxResult ajax = AjaxResult.success();
                ajax.put("urls", StringUtils.join(urls, FILE_DELIMITER));
                ajax.put("fileNames", StringUtils.join(fileNames, FILE_DELIMITER));
                ajax.put("newFileNames", StringUtils.join(newFileNames, FILE_DELIMITER));
                ajax.put("originalFilenames", StringUtils.join(originalFilenames, FILE_DELIMITER));
                return ajax;
            } catch (OSSException oe) {
                System.out.println("Caught an OSSException, which means your request made it to OSS, "
                        + "but was rejected with an error response for some reason.");
                System.out.println("Error Message:" + oe.getErrorMessage());
                System.out.println("Error Code:" + oe.getErrorCode());
                System.out.println("Request ID:" + oe.getRequestId());
                System.out.println("Host ID:" + oe.getHostId());
                return AjaxResult.error("文件上传失败：" + oe.getErrorMessage());
            } catch (ClientException ce) {
                System.out.println("Caught an ClientException, which means the client encountered "
                        + "a serious internal problem while trying to communicate with OSS, "
                        + "such as not being able to access the network.");
                System.out.println("Error Message:" + ce.getMessage());
                return AjaxResult.error("文件上传失败：" + ce.getMessage());
            } finally {
                if (ossClient != null) {
                    ossClient.shutdown();
                }
            }
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }


    /**
     * 本地资源通用下载
     */
    @GetMapping("/download/resource")
    public void resourceDownload(String resource, HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        try
        {
            if (!FileUtils.checkAllowDownload(resource))
            {
                throw new Exception(StringUtils.format("资源文件({})非法，不允许下载。 ", resource));
            }
            // 本地资源路径
            String localPath = RuoYiConfig.getProfile();
            // 数据库资源地址
            String downloadPath = localPath + FileUtils.stripPrefix(resource);
            // 下载名称
            String downloadName = StringUtils.substringAfterLast(downloadPath, "/");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            FileUtils.setAttachmentResponseHeader(response, downloadName);
            FileUtils.writeBytes(downloadPath, response.getOutputStream());
        }
        catch (Exception e)
        {
            log.error("下载文件失败", e);
        }
    }
}
