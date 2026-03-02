package com.library.controller;

import com.library.common.core.controller.BaseController;
import com.library.common.core.domain.AjaxResult;
import com.library.domain.BorrowRecord;
import com.library.mapper.BorrowRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 提醒控制器
 *
 * @author xiangziyang
 * @date 2026-03-02
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/library/reminder")
public class ReminderController extends BaseController {

    private final BorrowRecordMapper borrowRecordMapper;

    /**
     * 获取用户借阅提醒信息
     */
    @GetMapping("/info")
    public AjaxResult getReminderInfo() {
        Long userId = getUserId();
        
        // 查询即将逾期的记录（剩余1天内）
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        Date tomorrowDate = Date.from(tomorrow.atZone(ZoneId.systemDefault()).toInstant());
        List<BorrowRecord> dueSoonList = borrowRecordMapper.selectDueSoonRecordsByUserId(userId, tomorrowDate);
        
        // 查询已逾期的记录
        List<BorrowRecord> overdueList = borrowRecordMapper.selectOverdueRecordsByUserId(userId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("dueSoonCount", dueSoonList.size());
        result.put("dueSoonList", dueSoonList);
        result.put("overdueCount", overdueList.size());
        result.put("overdueList", overdueList);
        result.put("hasReminder", !dueSoonList.isEmpty() || !overdueList.isEmpty());
        
        return AjaxResult.success(result);
    }
}
