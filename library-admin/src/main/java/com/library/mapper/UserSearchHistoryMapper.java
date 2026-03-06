package com.library.mapper;

import com.library.domain.UserSearchHistory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户搜索历史Mapper接口
 * 
 * @author xiangziyang
 * @date 2026-03-03
 */
public interface UserSearchHistoryMapper {

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
    List<String> selectHotKeywords(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 插入或更新搜索历史
     * 
     * @param history 搜索历史
     * @return 影响行数
     */
    int insertOrUpdate(UserSearchHistory history);

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
