package com.library.service.impl;

import com.library.domain.Book;
import com.library.mapper.BookMapper;
import com.library.service.IRecommendService;
import com.library.service.IUserSearchHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 图书推荐Service实现类
 * 
 * @author xiangziyang
 * @date 2026-03-03
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendServiceImpl implements IRecommendService {

    private final BookMapper bookMapper;
    private final IUserSearchHistoryService userSearchHistoryService;

    @Override
    public List<Book> randomRecommend(Integer limit) {
        List<Book> books = bookMapper.selectRandomBooks(limit);
        log.info("随机推荐图书，数量：{}", books.size());
        return books;
    }

    @Override
    public List<Book> hotRecommend(Integer limit) {
        List<Book> books = bookMapper.selectHotBooks(limit);
        log.info("热门推荐图书，数量：{}", books.size());
        return books;
    }

    @Override
    public List<Book> keywordRecommend(Long userId, Integer limit) {
        if (userId == null) {
            return randomRecommend(limit);
        }

        List<String> hotKeywords = userSearchHistoryService.selectHotKeywords(userId, 5);
        
        if (hotKeywords.isEmpty()) {
            return randomRecommend(limit);
        }

        List<Book> books = bookMapper.selectBooksByKeywords(hotKeywords, limit);
        log.info("关键词推荐图书，用户ID：{}，关键词：{}，推荐数量：{}", userId, hotKeywords, books.size());
        
        if (books.isEmpty()) {
            return randomRecommend(limit);
        }
        
        return books;
    }
}
