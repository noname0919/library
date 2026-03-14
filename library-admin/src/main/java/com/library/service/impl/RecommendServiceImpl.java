package com.library.service.impl;

import com.library.common.utils.SecurityUtils;
import com.library.domain.Book;
import com.library.domain.BorrowRecord;
import com.library.mapper.BookMapper;
import com.library.service.IBorrowRecordService;
import com.library.service.IRecommendService;
import com.library.service.IUserSearchHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.stereotype.Service;
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
    private final IBorrowRecordService borrowRecordService;

    @Override
    public List<Book> randomRecommend(Integer limit) {
        List<Book> books = bookMapper.selectRandomBooks(limit);
        // 检查当前用户是否已借阅这些图书
        checkUserBorrowStatus(books);
        log.info("随机推荐图书，数量：{}", books.size());
        return books;
    }

    @Override
    public List<Book> hotRecommend(Integer limit) {
        List<Book> books = bookMapper.selectHotBooks(limit);
        // 检查当前用户是否已借阅这些图书
        checkUserBorrowStatus(books);
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
        // 检查当前用户是否已借阅这些图书
        checkUserBorrowStatus(books);
        log.info("关键词推荐图书，用户ID：{}，关键词：{}，推荐数量：{}", userId, hotKeywords, books.size());
        
        if (books.isEmpty()) {
            return randomRecommend(limit);
        }
        
        return books;
    }

    /**
     * 检查当前用户是否已借阅图书列表中的图书
     */
    private void checkUserBorrowStatus(List<Book> books) {
        try {
            var loginUser = SecurityUtils.getLoginUser();
            if (loginUser == null || loginUser.getUser() == null) {
                return;
            }
            Long currentUserId = loginUser.getUser().getUserId();
            
            for (Book book : books) {
                BorrowRecord borrowRecord = borrowRecordService.selectUserBorrowingBook(currentUserId, book.getId());
                if (borrowRecord != null) {
                    book.setIsBorrowedByCurrentUser(true);
                    book.setCurrentBorrowRecordId(borrowRecord.getId());
                } else {
                    book.setIsBorrowedByCurrentUser(false);
                    book.setCurrentBorrowRecordId(null);
                }
            }
        } catch (Exception e) {
            log.error("检查用户借阅状态失败", e);
        }
    }
}
