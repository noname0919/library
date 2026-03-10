package com.library.service;

import com.library.domain.Book;

import java.util.List;

/**
 * 图书推荐Service接口
 * 
 * @author xiangziyang
 * @date 2026-03-03
 */
public interface IRecommendService {

    /**
     * 随机推荐图书
     * 
     * @param limit 推荐数量
     * @return 图书列表
     */
    List<Book> randomRecommend(Integer limit);

    /**
     * 热门推荐图书（按借阅次数）
     * 
     * @param limit 推荐数量
     * @return 图书列表
     */
    List<Book> hotRecommend(Integer limit);

    /**
     * 关键词推荐图书
     * 
     * @param userId 用户ID
     * @param limit 推荐数量
     * @return 图书列表
     */
    List<Book> keywordRecommend(Long userId, Integer limit);

}
