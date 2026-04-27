package com.library.controller;

import com.library.common.core.controller.BaseController;
import com.library.common.core.domain.AjaxResult;
import com.library.domain.BorrowRecord;
import com.library.mapper.BorrowRecordMapper;
import com.library.task.OverdueReminderTask;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
    private final OverdueReminderTask overdueReminderTask;

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

    /**
     * 手动触发逾期提醒任务
     */
    @PostMapping("/trigger")
    public AjaxResult triggerOverdueReminder() {
        try {
            overdueReminderTask.sendOverdueReminders();
            return AjaxResult.success("逾期提醒任务执行完成，请查看日志");
        } catch (Exception e) {
            return AjaxResult.error("逾期提醒任务执行失败：" + e.getMessage());
        }
    }
}
