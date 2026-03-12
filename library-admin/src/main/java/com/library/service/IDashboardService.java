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
}
