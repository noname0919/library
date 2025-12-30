package com.library.mapper;

import java.util.List;
import com.library.domain.Book;

/**
 * 图书基本信息Mapper接口
 * 
 * @author xiangziyang
 * @date 2025-12-30
 */
public interface BookMapper 
{
    /**
     * 查询图书基本信息
     * 
     * @param id 图书基本信息主键
     * @return 图书基本信息
     */
    public Book selectBookById(Long id);

    /**
     * 查询图书基本信息列表
     * 
     * @param book 图书基本信息
     * @return 图书基本信息集合
     */
    public List<Book> selectBookList(Book book);

    /**
     * 新增图书基本信息
     * 
     * @param book 图书基本信息
     * @return 结果
     */
    public int insertBook(Book book);

    /**
     * 修改图书基本信息
     * 
     * @param book 图书基本信息
     * @return 结果
     */
    public int updateBook(Book book);

    /**
     * 删除图书基本信息
     * 
     * @param id 图书基本信息主键
     * @return 结果
     */
    public int deleteBookById(Long id);

    /**
     * 批量删除图书基本信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBookByIds(Long[] ids);
}
