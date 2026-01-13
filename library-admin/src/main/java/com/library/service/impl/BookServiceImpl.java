package com.library.service.impl;

import java.util.List;
import java.util.Set;

import com.library.common.exception.LibraryException;
import com.library.common.exception.LibraryExceptionEnum;
import com.library.common.utils.DateUtils;
import com.library.common.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.library.mapper.BookMapper;
import com.library.domain.Book;
import com.library.service.IBookService;
import com.library.framework.web.service.SysPermissionService;
import com.library.common.core.domain.model.LoginUser;

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
    @Override
    public int deleteBookByIds(Long[] ids) {
        return bookMapper.deleteBookByIds(ids);
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
