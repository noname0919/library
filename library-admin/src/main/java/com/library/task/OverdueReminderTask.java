package com.library.task;

import com.library.common.core.domain.entity.SysUser;
import com.library.domain.BorrowRecord;
import com.library.mapper.BorrowRecordMapper;
import com.library.service.IMailService;
import com.library.system.service.ISysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 逾期提醒定时任务
 *
 * @author xiangziyang
 * @date 2026-03-02
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OverdueReminderTask {

    private final BorrowRecordMapper borrowRecordMapper;
    private final ISysUserService userService;
    private final IMailService mailService;

    /**
     * 每天上午9点执行逾期提醒
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void sendOverdueReminders() {
        log.info("========================================");
        log.info("开始执行逾期提醒定时任务...");

        // 1. 查询所有即将逾期的记录（剩余1天内到期）
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        Date tomorrowDate = Date.from(tomorrow.atZone(ZoneId.systemDefault()).toInstant());
        log.info("查询条件：due_date <= {} 且 >= now()", tomorrowDate);
        
        List<BorrowRecord> dueSoonRecords = borrowRecordMapper.selectDueSoonRecords(tomorrowDate);
        log.info("查询到即将逾期记录：{}条", dueSoonRecords.size());
        for (BorrowRecord record : dueSoonRecords) {
            log.info("  即将逾期 - 用户ID：{}，图书：{}，应还日期：{}", 
                    record.getUserId(), record.getBookName(), record.getDueDate());
        }

        // 2. 查询所有已逾期的记录
        List<BorrowRecord> overdueRecords = borrowRecordMapper.selectOverdueRecords();
        log.info("查询到已逾期记录：{}条", overdueRecords.size());
        for (BorrowRecord record : overdueRecords) {
            log.info("  已逾期 - 用户ID：{}，图书：{}，应还日期：{}", 
                    record.getUserId(), record.getBookName(), record.getDueDate());
        }

        // 3. 按用户分组发送邮件
        sendReminderEmails(dueSoonRecords, overdueRecords);

        log.info("逾期提醒定时任务执行完成");
        log.info("========================================");
    }

    /**
     * 发送提醒邮件
     */
    private void sendReminderEmails(List<BorrowRecord> dueSoonRecords, List<BorrowRecord> overdueRecords) {
        // 按用户ID分组即将逾期的记录
        Map<Long, List<BorrowRecord>> dueSoonMap = dueSoonRecords.stream()
                .collect(Collectors.groupingBy(BorrowRecord::getUserId));

        // 按用户ID分组逾期记录
        Map<Long, List<BorrowRecord>> overdueMap = overdueRecords.stream()
                .collect(Collectors.groupingBy(BorrowRecord::getUserId));

        // 获取所有需要通知的用户ID（合并两个map的key）
        Set<Long> allUserIds = new HashSet<>();
        allUserIds.addAll(dueSoonMap.keySet());
        allUserIds.addAll(overdueMap.keySet());

        log.info("需要发送邮件提醒的用户数量：{}", allUserIds.size());

        // 遍历每个用户发送邮件
        for (Long userId : allUserIds) {
            try {
                SysUser user = userService.selectUserById(userId);
                if (user == null) {
                    log.warn("用户不存在，userId={}", userId);
                    continue;
                }
                if (user.getEmail() == null || user.getEmail().isEmpty()) {
                    log.warn("用户邮箱为空，userId={}，userName={}", userId, user.getUserName());
                    continue;
                }

                List<BorrowRecord> dueSoonList = dueSoonMap.getOrDefault(userId, Collections.emptyList());
                List<BorrowRecord> overdueList = overdueMap.getOrDefault(userId, Collections.emptyList());

                // 发送即将逾期提醒
                if (!dueSoonList.isEmpty()) {
                    mailService.sendDueReminderEmail(user.getEmail(), dueSoonList);
                    log.info("发送即将逾期提醒邮件成功，用户：{}，邮箱：{}，图书数量：{}",
                            user.getUserName(), user.getEmail(), dueSoonList.size());
                }

                // 发送逾期提醒
                if (!overdueList.isEmpty()) {
                    mailService.sendOverdueEmail(user.getEmail(), overdueList);
                    log.info("发送逾期提醒邮件成功，用户：{}，邮箱：{}，图书数量：{}",
                            user.getUserName(), user.getEmail(), overdueList.size());
                }
            } catch (Exception e) {
                log.error("发送邮件提醒失败，userId={}，错误：{}", userId, e.getMessage());
            }
        }
    }
}
