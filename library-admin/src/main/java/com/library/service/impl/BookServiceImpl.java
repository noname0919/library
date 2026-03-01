package com.library.service.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.aliyun.oss.*;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.EnvironmentVariableCredentialsProvider;
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyun.oss.model.DeleteObjectsRequest;
import com.aliyun.oss.model.DeleteObjectsResult;
import com.library.common.exception.LibraryException;
import com.library.common.exception.LibraryExceptionEnum;
import com.library.common.utils.DateUtils;
import com.library.common.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.library.mapper.BookMapper;
import com.library.domain.Book;
import com.library.service.IBookService;
import com.library.framework.web.service.SysPermissionService;
import com.library.common.core.domain.model.LoginUser;
import org.springframework.transaction.annotation.Transactional;

/**
 * 图书基本信息Service业务层处理
 *
 * @author xiangziyang
 * @date 2025-12-30
 */
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements IBookService {
    private final BookMapper bookMapper;
    private final SysPermissionService permissionService;

    /**
     * 查询图书基本信息
     *
     * @param id 图书基本信息主键
     * @return 图书基本信息
     */
    @Override
    public Book selectBookById(Long id) {
        return bookMapper.selectBookById(id);
    }


    /**
     * 查询图书基本信息列表
     *
     * @param book 图书基本信息
     * @return 图书基本信息
     */
    @Override
    public List<Book> selectBookList(Book book) {
        // 检查当前用户角色，读者只能查看上架图书
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser != null && loginUser.getUser() != null) {
            // 使用permissionService获取用户角色集合
            Set<String> roles = permissionService.getRolePermission(loginUser.getUser());
            // 检查用户是否拥有读者角色
            if (roles != null && roles.contains("reader")) {
                book.setStatus("0");
            }
        }
        return bookMapper.selectBookList(book);
    }


    /**
     * 新增图书基本信息
     *
     * @param book 图书基本信息
     * @return 结果
     */
    @Override
    public int insertBook(Book book) {
        book.setCreateTime(DateUtils.getNowDate());
        //校验新增是否重复
        boolean isExist = bookMapper.checkAddExist(book.getIsbn());
        if (isExist) {
            throw new LibraryException(LibraryExceptionEnum.BOOK_EXIST);
        }
        return bookMapper.insertBook(book);
    }

    /**
     * 修改图书基本信息
     *
     * @param book 图书基本信息
     * @return 结果
     */
    @Override
    public int updateBook(Book book) {
        book.setUpdateTime(DateUtils.getNowDate());
        //判断修改后是否重复
        boolean isExist = bookMapper.checkUpdateExist(book.getIsbn(), book.getId());
        //修改的可借数量不能超过馆藏数量
        if (book.getAvailableQuantity() > book.getTotalQuantity()) {
            throw new LibraryException(LibraryExceptionEnum.BOOK_QUANTITY_ERROR);
        }
        //馆藏数量不能小于已借数量
        if (book.getTotalQuantity() < book.getBorrowedQuantity()) {
            throw new LibraryException(LibraryExceptionEnum.BOOK_QUANTITY_ERROR);
        }
        if (isExist) {
            throw new LibraryException(LibraryExceptionEnum.BOOK_EXIST);
        }
        return bookMapper.updateBook(book);
    }

    /**
     * 批量删除图书基本信息
     *
     * @param ids 需要删除的图书基本信息主键
     * @return 结果
     */
    @Transactional
    @Override
    @SneakyThrows
    public int deleteBookByIds(Long[] ids) {
        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
        String endpoint = "https://oss-cn-guangzhou.aliyuncs.com";
        // 从环境变量中获取访问凭证。运行本代码示例之前，请确保已设置环境变量OSS_ACCESS_KEY_ID和OSS_ACCESS_KEY_SECRET。
        EnvironmentVariableCredentialsProvider credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();
        // 填写Bucket名称，例如examplebucket。
        String bucketName = "noname1";
        // 填写Bucket所在地域。以华东1（杭州）为例，Region填写为cn-hangzhou。
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

            // 查询需要删除的图书记录
            List<Book> books = bookMapper.selectBookListByIds(ids);
            List<String> keys = new ArrayList<>();

            // 提取每个图书记录的图片文件名并添加到keys列表中
            for (Book book : books) {
                if (book.getImage() != null && !book.getImage().isEmpty()) {
                    String objectName = extractObjectNameFromUrl(book.getImage());
                    if (objectName != null && !objectName.isEmpty()) {
                        keys.add(objectName);
                        System.out.println("准备删除OSS文件: " + objectName);
                    }
                }
            }

            // 批量删除OSS中的文件
            if (!keys.isEmpty()) {
                DeleteObjectsResult deleteObjectsResult = ossClient.deleteObjects(
                        new DeleteObjectsRequest(bucketName).withKeys(keys).withEncodingType("url"));
                List<String> deletedObjects = deleteObjectsResult.getDeletedObjects();
                for (String obj : deletedObjects) {
                    String decodedObj = URLDecoder.decode(obj, "UTF-8");
                    System.out.println("Deleted OSS object: " + decodedObj);
                }
            }

        // 删除数据库中的记录
        return bookMapper.deleteBookByIds(ids);
    }


    private String extractObjectNameFromUrl(String ossImageUrl) {
        // OSS URL 格式: https://bucketName.endpoint/objectName
        // 例如: https://noname1.oss-cn-guangzhou.aliyuncs.com/uploads/1772391260013_xxx.png
        // 需要提取出 objectName: uploads/1772391260013_xxx.png
        
        // 去掉协议部分 https://
        String withoutProtocol = ossImageUrl.replaceFirst("^https?://", "");
        
        // 找到第一个 / 后的内容就是 objectName
        int firstSlashIndex = withoutProtocol.indexOf("/");
        if (firstSlashIndex == -1) {
            return null;
        }
        
        // 提取 objectName (包括 uploads/ 前缀)
        String objectName = withoutProtocol.substring(firstSlashIndex + 1);
        
        // URL 解码，处理中文等特殊字符
        try {
            objectName = URLDecoder.decode(objectName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // 解码失败，使用原始值
        }
        
        return objectName;
    }

    /**
     * 删除图书基本信息信息
     *
     * @param id 图书基本信息主键
     * @return 结果
     */
    @Override
    public int deleteBookById(Long id) {
        return bookMapper.deleteBookById(id);
    }
}
