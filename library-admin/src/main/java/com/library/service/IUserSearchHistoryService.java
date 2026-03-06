package com.library.service;

import com.library.domain.UserSearchHistory;

import java.util.List;

/**
 * 用户搜索历史Service接口
 * 
 * @author xiangziyang
 * @date 2026-03-03
 */
public interface IUserSearchHistoryService {

    /**
     * 查询用户搜索历史
     * 
     * @param userId 用户ID
     * @return 搜索历史列表
     */
    List<UserSearchHistory> selectByUserId(Long userId);

    /**
     * 查询用户热门搜索关键词
     * 
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 热门关键词列表
     */
    List<String> selectHotKeywords(Long userId, Integer limit);

    /**
     * 记录搜索历史
     * 
     * @param userId 用户ID
     * @param keyword 搜索关键词
     */
    void recordSearch(Long userId, String keyword);

    /**
     * 删除用户搜索历史
     * 
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteByUserId(Long userId);

    /**
     * 清空所有搜索历史
     * 
     * @return 影响行数
     */
    int deleteAll();
}
