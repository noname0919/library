package com.library.controller;

import com.library.common.core.controller.BaseController;
import com.library.common.core.domain.AjaxResult;
import com.library.service.IDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 仪表盘Controller
 *
 * @author xiangziyang
 * @date 2026-03-11
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/library/dashboard")
public class DashboardController extends BaseController {

    private final IDashboardService dashboardService;

    /**
     * 获取仪表盘统计数据
     */
    @PreAuthorize("@ss.hasPermi('library:dashboard:view')")
    @GetMapping("/statistics")
    public AjaxResult getStatistics() {
        Map<String, Object> statistics = dashboardService.getStatistics();
        return AjaxResult.success(statistics);
    }

    /**
     * 获取借阅趋势数据
     */
    @PreAuthorize("@ss.hasPermi('library:dashboard:trend:view')")
    @GetMapping("/borrow-trend")
    public AjaxResult getBorrowTrend(@org.springframework.web.bind.annotation.RequestParam(defaultValue = "7") int days) {
        java.util.List<java.util.Map<String, Object>> trend = dashboardService.getBorrowTrend(days);
        return AjaxResult.success(trend);
    }

    /**
     * 获取图书分类借阅统计
     */
    @PreAuthorize("@ss.hasPermi('library:dashboard:trend:view')")
    @GetMapping("/category-borrow-stats")
    public AjaxResult getCategoryBorrowStats(@org.springframework.web.bind.annotation.RequestParam(defaultValue = "7") int days) {
        java.util.List<java.util.Map<String, Object>> stats = dashboardService.getCategoryBorrowStats(days);
        return AjaxResult.success(stats);
    }

    /**
     * 获取TOP10热门图书
     */
    @PreAuthorize("@ss.hasPermi('library:dashboard:trend:view')")
    @GetMapping("/top-borrowed-books")
    public AjaxResult getTopBorrowedBooks(@org.springframework.web.bind.annotation.RequestParam(defaultValue = "7") int days) {
        java.util.List<java.util.Map<String, Object>> books = dashboardService.getTopBorrowedBooks(days);
        return AjaxResult.success(books);
    }

    /**
     * 获取TOP10活跃读者
     */
    @PreAuthorize("@ss.hasPermi('library:dashboard:trend:view')")
    @GetMapping("/top-active-readers")
    public AjaxResult getTopActiveReaders(@org.springframework.web.bind.annotation.RequestParam(defaultValue = "0") int days) {
        java.util.List<java.util.Map<String, Object>> readers = dashboardService.getTopActiveReaders(days);
        return AjaxResult.success(readers);
    }
}
