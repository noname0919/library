package com.library.mapper;

import java.util.List;
import com.library.domain.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 图书基本信息Mapper接口
 * 
 * @author xiangziyang
 * @date 2025-12-30
 */
@Mapper
public interface BookMapper 
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
     * 删除图书基本信息
     * 
     * @param id 图书基本信息主键
     * @return 结果
     */
    int deleteBookById(Long id);

    /**
     * 批量删除图书基本信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteBookByIds(Long[] ids);

    /**
     * 判断新增后是否重复
     * @param isbn
     * @return
     */
    boolean checkAddExist(String isbn);

    /**
     * 修改判断是否重复
     * @param isbn
     * @param id
     * @return
     */
    boolean checkUpdateExist(@Param("isbn") String isbn, @Param("id") Long id);

    /**
     * 根据id批量查询图书记录
     * @param ids
     * @return
     */
    List<Book> selectBookListByIds(@Param("ids")Long[] ids);

    /**
     * 随机推荐图书
     * 
     * @param limit 推荐数量
     * @return 图书列表
     */
    List<Book> selectRandomBooks(@Param("limit") Integer limit);

    /**
     * 热门推荐图书（按借阅次数）
     * 
     * @param limit 推荐数量
     * @return 图书列表
     */
    List<Book> selectHotBooks(@Param("limit") Integer limit);

    /**
     * 关键词推荐图书
     * 
     * @param keywords 关键词列表
     * @param limit 推荐数量
     * @return 图书列表
     */
    List<Book> selectBooksByKeywords(@Param("keywords") List<String> keywords, @Param("limit") Integer limit);
}
