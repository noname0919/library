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

    @Override
    public List<Book> mixedRecommend(Long userId, Integer limit) {
        List<Book> result = new ArrayList<>();
        
        int randomCount = limit / 3;
        int hotCount = limit / 3;
        int keywordCount = limit - randomCount - hotCount;
        
        List<Book> randomBooks = randomRecommend(randomCount);
        List<Book> hotBooks = hotRecommend(hotCount);
        List<Book> keywordBooks = keywordRecommend(userId, keywordCount);
        
        result.addAll(randomBooks);
        result.addAll(hotBooks);
        result.addAll(keywordBooks);
        
        Collections.shuffle(result);
        
        if (result.size() > limit) {
            result = result.subList(0, limit);
        }
        
        log.info("综合推荐图书，用户ID：{}，随机：{}，热门：{}，关键词：{}，总计：{}", 
                userId, randomCount, hotCount, keywordCount, result.size());
        
        return result;
    }
}
