package com.library.service.impl;

import com.library.mapper.BookMapper;
import com.library.mapper.BorrowRecordMapper;
import com.library.service.IDashboardService;
import com.library.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    private final BorrowRecordMapper borrowRecordMapper;
    private final SysUserMapper sysUserMapper;

    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // 图书总量
        Long totalBooks = bookMapper.selectTotalCount();
        statistics.put("totalBooks", totalBooks);

        // 用户总数
        Long totalUsers = sysUserMapper.countTotalUsers();
        statistics.put("totalUsers", totalUsers);

        // 今日借阅
        Integer todayBorrow = borrowRecordMapper.countTodayBorrow(today);
        statistics.put("todayBorrow", todayBorrow);

        // 今日归还
        Integer todayReturn = borrowRecordMapper.countTodayReturn(today);
        statistics.put("todayReturn", todayReturn);

        // 逾期未还
        Integer overdueCount = borrowRecordMapper.countOverdue();
        statistics.put("overdueCount", overdueCount);

        // 图书分类统计
        List<Map<String, Object>> categoryStats = bookMapper.selectCategoryStats();
        statistics.put("categoryStats", categoryStats);

        log.info("获取仪表盘统计数据：图书总量={}, 用户总数={}, 今日借阅={}, 今日归还={}, 逾期未还={}", 
                totalBooks, totalUsers, todayBorrow, todayReturn, overdueCount);

        return statistics;
    }

    @Override
    public List<Map<String, Object>> getBorrowTrend(int days) {
        // 获取数据库中的借阅数据
        List<Map<String, Object>> trend = borrowRecordMapper.selectBorrowTrend(days);
        
        // 将查询结果转换为Map，方便查找
        Map<String, Integer> dateCountMap = new HashMap<>();
        for (Map<String, Object> item : trend) {
            String date = (String) item.get("date");
            Integer count = ((Number) item.get("count")).intValue();
            dateCountMap.put(date, count);
        }
        
        // 生成完整的日期列表（包括没有借阅记录的日期）
        List<Map<String, Object>> result = new ArrayList<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        // 从今天往前推days天
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String dateStr = date.format(formatter);
            
            Map<String, Object> item = new HashMap<>();
            item.put("date", dateStr);
            item.put("count", dateCountMap.getOrDefault(dateStr, 0));
            result.add(item);
        }
        
        log.info("获取借阅趋势数据：天数={}, 数据条数={}", days, result.size());
        return result;
    }

    @Override
    public List<Map<String, Object>> getCategoryBorrowStats(int days) {
        List<Map<String, Object>> stats = borrowRecordMapper.selectCategoryBorrowStats(days);
        log.info("获取图书分类借阅统计：天数={}, 数据条数={}", days, stats.size());
        return stats;
    }

    @Override
    public List<Map<String, Object>> getTopBorrowedBooks(int days) {
        List<Map<String, Object>> books = borrowRecordMapper.selectTopBorrowedBooks(10, days);
        log.info("获取TOP10热门图书：天数={}, 数据条数={}", days, books.size());
        return books;
    }
}
