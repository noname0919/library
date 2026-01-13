package com.library.service;

import java.util.List;
import com.library.domain.Book;

/**
 * 图书基本信息Service接口
 * 
 * @author xiangziyang
 * @date 2025-12-30
 */
public interface IBookService 
{
    /**
     * 查询图书基本信息
     * 
     * @param id 图书基本信息主键
     * @return 图书基本信息
     */
     Book selectBookById(Long id);

    /**
     * 查询图书基本信息列表
     * 
     * @param book 图书基本信息
     * @return 图书基本信息集合
     */
     List<Book> selectBookList(Book book);

    /**
     * 新增图书基本信息
     * 
     * @param book 图书基本信息
     * @return 结果
     */
     int insertBook(Book book);

    /**
     * 修改图书基本信息
     * 
     * @param book 图书基本信息
     * @return 结果
     */
    int updateBook(Book book);

    /**
     * 批量删除图书基本信息
     * 
     * @param ids 需要删除的图书基本信息主键集合
     * @return 结果
     */
     int deleteBookByIds(Long[] ids);

    /**
     * 删除图书基本信息信息
     * 
     * @param id 图书基本信息主键
     * @return 结果
     */
     int deleteBookById(Long id);
}
