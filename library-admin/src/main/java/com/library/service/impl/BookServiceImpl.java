package com.library.service.impl;

import java.util.List;
import com.library.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.library.mapper.BookMapper;
import com.library.domain.Book;
import com.library.service.IBookService;

/**
 * 图书基本信息Service业务层处理
 * 
 * @author xiangziyang
 * @date 2025-12-30
 */
@Service
public class BookServiceImpl implements IBookService 
{
    @Autowired
    private BookMapper bookMapper;

    /**
     * 查询图书基本信息
     * 
     * @param id 图书基本信息主键
     * @return 图书基本信息
     */
    @Override
    public Book selectBookById(Long id)
    {
        return bookMapper.selectBookById(id);
    }

    /**
     * 查询图书基本信息列表
     * 
     * @param book 图书基本信息
     * @return 图书基本信息
     */
    @Override
    public List<Book> selectBookList(Book book)
    {
        return bookMapper.selectBookList(book);
    }

    /**
     * 新增图书基本信息
     * 
     * @param book 图书基本信息
     * @return 结果
     */
    @Override
    public int insertBook(Book book)
    {
        book.setCreateTime(DateUtils.getNowDate());
        return bookMapper.insertBook(book);
    }

    /**
     * 修改图书基本信息
     * 
     * @param book 图书基本信息
     * @return 结果
     */
    @Override
    public int updateBook(Book book)
    {
        book.setUpdateTime(DateUtils.getNowDate());
        return bookMapper.updateBook(book);
    }

    /**
     * 批量删除图书基本信息
     * 
     * @param ids 需要删除的图书基本信息主键
     * @return 结果
     */
    @Override
    public int deleteBookByIds(Long[] ids)
    {
        return bookMapper.deleteBookByIds(ids);
    }

    /**
     * 删除图书基本信息信息
     * 
     * @param id 图书基本信息主键
     * @return 结果
     */
    @Override
    public int deleteBookById(Long id)
    {
        return bookMapper.deleteBookById(id);
    }
}
