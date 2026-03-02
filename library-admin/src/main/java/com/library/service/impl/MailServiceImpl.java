package com.library.service.impl;

import com.library.domain.BorrowRecord;
import com.library.service.IMailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 邮件服务实现类
 *
 * @author xiangziyang
 * @date 2026-03-02
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements IMailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    @Override
    public void sendDueReminderEmail(String toEmail, List<BorrowRecord> borrowRecords) {
        log.info("准备发送即将逾期提醒邮件，收件人：{}，发件人：{}，图书数量：{}", toEmail, fromEmail, borrowRecords.size());
        
        if (borrowRecords.isEmpty()) {
            log.warn("图书列表为空，不发送邮件");
            return;
        }
        
        if (fromEmail == null || fromEmail.isEmpty()) {
            log.error("发件人邮箱未配置，请在 application.yml 中配置 spring.mail.username");
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("【图书馆】图书即将到期提醒");
        message.setText(buildDueReminderContent(borrowRecords));

        try {
            mailSender.send(message);
            log.info("发送即将逾期提醒邮件成功，收件人：{}，图书数量：{}", toEmail, borrowRecords.size());
        } catch (Exception e) {
            log.error("发送即将逾期提醒邮件失败，收件人：{}，错误：{}", toEmail, e.getMessage(), e);
        }
    }

    @Override
    public void sendOverdueEmail(String toEmail, List<BorrowRecord> borrowRecords) {
        log.info("准备发送逾期提醒邮件，收件人：{}，发件人：{}，图书数量：{}", toEmail, fromEmail, borrowRecords.size());
        
        if (borrowRecords.isEmpty()) {
            log.warn("图书列表为空，不发送邮件");
            return;
        }
        
        if (fromEmail == null || fromEmail.isEmpty()) {
            log.error("发件人邮箱未配置，请在 application.yml 中配置 spring.mail.username");
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("【图书馆】图书已逾期提醒");
        message.setText(buildOverdueContent(borrowRecords));

        try {
            mailSender.send(message);
            log.info("发送逾期提醒邮件成功，收件人：{}，图书数量：{}", toEmail, borrowRecords.size());
        } catch (Exception e) {
            log.error("发送逾期提醒邮件失败，收件人：{}，错误：{}", toEmail, e.getMessage());
        }
    }

    /**
     * 构建即将逾期提醒内容
     */
    private String buildDueReminderContent(List<BorrowRecord> borrowRecords) {
        StringBuilder sb = new StringBuilder();
        sb.append("尊敬的读者，您好！\n\n");
        sb.append("您借阅的以下图书即将到期，请及时归还或续借：\n\n");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (BorrowRecord record : borrowRecords) {
            sb.append("《").append(record.getBookName()).append("》\n");
            sb.append("  应还日期：").append(sdf.format(record.getDueDate())).append("\n\n");
        }

        sb.append("温馨提示：\n");
        sb.append("1. 每本图书最多可续借3次，每次续借可延长30天\n");
        sb.append("2. 逾期图书将无法续借和借阅新书\n");
        sb.append("3. 如有疑问，请联系图书馆管理员\n\n");
        sb.append("图书馆系统");

        return sb.toString();
    }

    /**
     * 构建逾期提醒内容
     */
    private String buildOverdueContent(List<BorrowRecord> borrowRecords) {
        StringBuilder sb = new StringBuilder();
        sb.append("尊敬的读者，您好！\n\n");
        sb.append("您借阅的以下图书已逾期，请尽快归还：\n\n");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (BorrowRecord record : borrowRecords) {
            sb.append("《").append(record.getBookName()).append("》\n");
            sb.append("  应还日期：").append(sdf.format(record.getDueDate())).append("\n\n");
        }

        sb.append("重要提示：\n");
        sb.append("1. 逾期图书将无法借阅新书\n");
        sb.append("2. 逾期图书无法续借\n");
        sb.append("3. 请尽快归还逾期图书，以免影响您的借阅权限\n");
        sb.append("4. 如有疑问，请联系图书馆管理员\n\n");
        sb.append("图书馆系统");

        return sb.toString();
    }
}
