package com.library.controller;

import com.library.common.core.controller.BaseController;
import com.library.common.core.domain.AjaxResult;
import com.library.service.IDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
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

    /**
     * 获取库存不足预警
     */
    @PreAuthorize("@ss.hasPermi('library:dashboard:trend:view')")
    @GetMapping("/stock-warning")
    public AjaxResult getStockWarning() {
        java.util.List<com.library.domain.Book> books = dashboardService.getStockWarning();
        return AjaxResult.success(books);
    }

    /**
     * 导出今日借阅记录
     */
    @PreAuthorize("@ss.hasPermi('library:dashboard:view')")
    @PostMapping("/export-today-borrow")
    public void exportTodayBorrow(HttpServletResponse response) {
        java.util.List<com.library.domain.BorrowRecord> records = dashboardService.getTodayBorrowRecords();
        // 转换为导出DTO，不包含状态字段
        java.util.List<com.library.domain.BorrowRecordExportDTO> exportData = new java.util.ArrayList<>();
        for (com.library.domain.BorrowRecord record : records) {
            com.library.domain.BorrowRecordExportDTO dto = new com.library.domain.BorrowRecordExportDTO();
            dto.setBookName(record.getBookName());
            dto.setIsbn(record.getIsbn());
            dto.setUserName(record.getUserName());
            dto.setNickName(record.getNickName());
            dto.setBorrowTime(com.library.common.utils.DateUtils.parseDateToStr("yyyy-MM-dd", record.getBorrowTime()));
            dto.setDueDate(com.library.common.utils.DateUtils.parseDateToStr("yyyy-MM-dd", record.getDueDate()));
            exportData.add(dto);
        }
        com.library.common.utils.poi.ExcelUtil<com.library.domain.BorrowRecordExportDTO> util = new com.library.common.utils.poi.ExcelUtil<>(com.library.domain.BorrowRecordExportDTO.class);
        util.exportExcel(response, exportData, "今日借阅记录");
    }

    /**
     * 导出今日归还记录
     */
    @PreAuthorize("@ss.hasPermi('library:dashboard:view')")
    @PostMapping("/export-today-return")
    public void exportTodayReturn(HttpServletResponse response) {
        java.util.List<com.library.domain.BorrowRecord> records = dashboardService.getTodayReturnRecords();
        // 转换为导出DTO，包含归还时间字段
        java.util.List<com.library.domain.BorrowRecordReturnExportDTO> exportData = new java.util.ArrayList<>();
        for (com.library.domain.BorrowRecord record : records) {
            com.library.domain.BorrowRecordReturnExportDTO dto = new com.library.domain.BorrowRecordReturnExportDTO();
            dto.setBookName(record.getBookName());
            dto.setIsbn(record.getIsbn());
            dto.setUserName(record.getUserName());
            dto.setNickName(record.getNickName());
            dto.setBorrowTime(com.library.common.utils.DateUtils.parseDateToStr("yyyy-MM-dd", record.getBorrowTime()));
            dto.setDueDate(com.library.common.utils.DateUtils.parseDateToStr("yyyy-MM-dd", record.getDueDate()));
            if (record.getReturnTime() != null) {
                dto.setReturnTime(com.library.common.utils.DateUtils.parseDateToStr("yyyy-MM-dd", record.getReturnTime()));
            }
            exportData.add(dto);
        }
        com.library.common.utils.poi.ExcelUtil<com.library.domain.BorrowRecordReturnExportDTO> util = new com.library.common.utils.poi.ExcelUtil<>(com.library.domain.BorrowRecordReturnExportDTO.class);
        util.exportExcel(response, exportData, "今日归还记录");
    }

    /**
     * 导出逾期未还记录
     */
    @PreAuthorize("@ss.hasPermi('library:dashboard:view')")
    @PostMapping("/export-overdue")
    public void exportOverdue(HttpServletResponse response) {
        java.util.List<com.library.domain.BorrowRecord> records = dashboardService.getOverdueRecords();
        // 转换为导出DTO，不包含归还时间字段
        java.util.List<com.library.domain.BorrowRecordExportDTO> exportData = new java.util.ArrayList<>();
        for (com.library.domain.BorrowRecord record : records) {
            com.library.domain.BorrowRecordExportDTO dto = new com.library.domain.BorrowRecordExportDTO();
            dto.setBookName(record.getBookName());
            dto.setIsbn(record.getIsbn());
            dto.setUserName(record.getUserName());
            dto.setNickName(record.getNickName());
            dto.setBorrowTime(com.library.common.utils.DateUtils.parseDateToStr("yyyy-MM-dd", record.getBorrowTime()));
            dto.setDueDate(com.library.common.utils.DateUtils.parseDateToStr("yyyy-MM-dd", record.getDueDate()));
            exportData.add(dto);
        }
        com.library.common.utils.poi.ExcelUtil<com.library.domain.BorrowRecordExportDTO> util = new com.library.common.utils.poi.ExcelUtil<>(com.library.domain.BorrowRecordExportDTO.class);
        util.exportExcel(response, exportData, "逾期未还记录");
    }

    /**
     * 导出图书分类占比
     */
    @PreAuthorize("@ss.hasPermi('library:dashboard:view')")
    @PostMapping("/export-category-stats")
    public void exportCategoryStats(HttpServletResponse response) {
        java.util.List<java.util.Map<String, Object>> stats = dashboardService.getCategoryStats();
        // 转换为具体的DTO格式进行导出
        java.util.List<com.library.domain.CategoryStatsDTO> exportData = new java.util.ArrayList<>();
        for (java.util.Map<String, Object> stat : stats) {
            com.library.domain.CategoryStatsDTO dto = new com.library.domain.CategoryStatsDTO();
            dto.setName((String) stat.get("name"));
            dto.setValue((Integer) stat.get("value"));
            exportData.add(dto);
        }
        com.library.common.utils.poi.ExcelUtil<com.library.domain.CategoryStatsDTO> util = new com.library.common.utils.poi.ExcelUtil<>(com.library.domain.CategoryStatsDTO.class);
        util.exportExcel(response, exportData, "图书分类占比");
    }
}
