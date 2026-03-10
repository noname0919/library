package com.library.service.impl;

import com.library.mapper.BookMapper;
import com.library.service.IDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 仪表盘Service实现类
 *
 * @author xiangziyang
 * @date 2026-03-11
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements IDashboardService {

    private final BookMapper bookMapper;

    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        // 图书总量
        Long totalBooks = bookMapper.selectTotalCount();
        statistics.put("totalBooks", totalBooks);

        // 图书分类统计
        List<Map<String, Object>> categoryStats = bookMapper.selectCategoryStats();
        statistics.put("categoryStats", categoryStats);

        log.info("获取仪表盘统计数据：图书总量={}", totalBooks);

        return statistics;
    }
}
