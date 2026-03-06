package com.library.service.impl;

import com.library.domain.UserSearchHistory;
import com.library.mapper.UserSearchHistoryMapper;
import com.library.service.IUserSearchHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户搜索历史Service实现类
 * 
 * @author xiangziyang
 * @date 2026-03-03
 */
@Service
@RequiredArgsConstructor
public class UserSearchHistoryServiceImpl implements IUserSearchHistoryService {

    private final UserSearchHistoryMapper userSearchHistoryMapper;

    @Override
    public List<UserSearchHistory> selectByUserId(Long userId) {
        return userSearchHistoryMapper.selectByUserId(userId);
    }

    @Override
    public List<String> selectHotKeywords(Long userId, Integer limit) {
        return userSearchHistoryMapper.selectHotKeywords(userId, limit);
    }

    @Override
    public void recordSearch(Long userId, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return;
        }
        
        UserSearchHistory history = new UserSearchHistory();
        history.setUserId(userId);
        history.setKeyword(keyword.trim());
        
        userSearchHistoryMapper.insertOrUpdate(history);
    }

    @Override
    public int deleteByUserId(Long userId) {
        return userSearchHistoryMapper.deleteByUserId(userId);
    }

    @Override
    public int deleteAll() {
        return userSearchHistoryMapper.deleteAll();
    }
}
