package com.library.service;

import com.library.domain.BorrowRecord;

import java.util.List;

/**
 * 邮件服务接口
 *
 * @author xiangziyang
 * @date 2026-03-02
 */
public interface IMailService {

    /**
     * 发送即将逾期提醒邮件
     *
     * @param toEmail 收件人邮箱
     * @param borrowRecords 即将逾期的借阅记录
     */
    void sendDueReminderEmail(String toEmail, List<BorrowRecord> borrowRecords);

    /**
     * 发送逾期提醒邮件
     *
     * @param toEmail 收件人邮箱
     * @param borrowRecords 逾期的借阅记录
     */
    void sendOverdueEmail(String toEmail, List<BorrowRecord> borrowRecords);
}
