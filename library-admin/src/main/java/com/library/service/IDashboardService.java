package com.library.service;

import java.util.Map;

/**
 * 仪表盘Service接口
 *
 * @author xiangziyang
 * @date 2026-03-11
 */
public interface IDashboardService {

    /**
     * 获取仪表盘统计数据
     *
     * @return 统计数据
     */
    Map<String, Object> getStatistics();

    /**
     * 获取借阅趋势数据
     *
     * @param days 天数
     * @return 借阅趋势数据
     */
    java.util.List<java.util.Map<String, Object>> getBorrowTrend(int days);

    /**
     * 获取图书分类借阅统计
     *
     * @param days 天数
     * @return 分类借阅统计数据
     */
    java.util.List<java.util.Map<String, Object>> getCategoryBorrowStats(int days);

    /**
     * 获取TOP10热门图书
     *
     * @param days 天数
     * @return TOP10热门图书
     */
    java.util.List<java.util.Map<String, Object>> getTopBorrowedBooks(int days);

    /**
     * 获取TOP10活跃读者
     *
     * @param days 天数
     * @return TOP10活跃读者
     */
    java.util.List<java.util.Map<String, Object>> getTopActiveReaders(int days);

    /**
     * 获取库存不足预警
     *
     * @return 库存不足的图书列表
     */
    java.util.List<com.library.domain.Book> getStockWarning();

    /**
     * 获取今日借阅记录
     *
     * @return 今日借阅记录
     */
    java.util.List<com.library.domain.BorrowRecord> getTodayBorrowRecords();

    /**
     * 获取今日归还记录
     *
     * @return 今日归还记录
     */
    java.util.List<com.library.domain.BorrowRecord> getTodayReturnRecords();

    /**
     * 获取逾期未还记录
     *
     * @return 逾期未还记录
     */
    java.util.List<com.library.domain.BorrowRecord> getOverdueRecords();

    /**
     * 获取图书分类占比
     *
     * @return 图书分类占比
     */
    java.util.List<java.util.Map<String, Object>> getCategoryStats();
}
