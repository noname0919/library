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
}
