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
}
