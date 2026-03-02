package com.library.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import com.library.common.exception.LibraryException;
import com.library.common.exception.LibraryExceptionEnum;
import com.library.common.utils.DateUtils;
import com.library.common.utils.SecurityUtils;
import com.library.common.core.domain.model.LoginUser;
import com.library.domain.Book;
import com.library.domain.BorrowRecord;
import com.library.mapper.BookMapper;
import com.library.mapper.BorrowRecordMapper;
import com.library.service.IBookService;
import com.library.service.IBorrowRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 借阅记录Service业务层处理
 *
 * @author xiangziyang
 * @date 2026-03-02
 */
@Service
@RequiredArgsConstructor
public class BorrowRecordServiceImpl implements IBorrowRecordService {
    private final BorrowRecordMapper borrowRecordMapper;
    private final BookMapper bookMapper;

    /**
     * 查询借阅记录
     *
     * @param id 借阅记录主键
     * @return 借阅记录
     */
    @Override
    public BorrowRecord selectBorrowRecordById(Long id) {
        return borrowRecordMapper.selectBorrowRecordById(id);
    }

    /**
     * 查询借阅记录列表
     *
     * @param borrowRecord 借阅记录
     * @return 借阅记录
     */
    @Override
    public List<BorrowRecord> selectBorrowRecordList(BorrowRecord borrowRecord) {
        return borrowRecordMapper.selectBorrowRecordList(borrowRecord);
    }

    /**
     * 查询借阅记录列表（借阅中+逾期）
     *
     * @param borrowRecord 借阅记录
     * @return 借阅记录
     */
    @Override
    public List<BorrowRecord> selectBorrowingList(BorrowRecord borrowRecord) {
        List<BorrowRecord> list = borrowRecordMapper.selectBorrowingList(borrowRecord);
        // 实时计算逾期状态
        for (BorrowRecord record : list) {
            record.setStatus(record.getActualStatus());
        }
        return list;
    }

    /**
     * 新增借阅记录
     *
     * @param borrowRecord 借阅记录
     * @return 结果
     */
    @Override
    public int insertBorrowRecord(BorrowRecord borrowRecord) {
        return borrowRecordMapper.insertBorrowRecord(borrowRecord);
    }

    /**
     * 修改借阅记录
     *
     * @param borrowRecord 借阅记录
     * @return 结果
     */
    @Override
    public int updateBorrowRecord(BorrowRecord borrowRecord) {
        return borrowRecordMapper.updateBorrowRecord(borrowRecord);
    }

    /**
     * 批量删除借阅记录
     *
     * @param ids 需要删除的借阅记录主键集合
     * @return 结果
     */
    @Override
    public int deleteBorrowRecordByIds(Long[] ids) {
        return borrowRecordMapper.deleteBorrowRecordByIds(ids);
    }

    /**
     * 删除借阅记录
     *
     * @param id 借阅记录主键
     * @return 结果
     */
    @Override
    public int deleteBorrowRecordById(Long id) {
        return borrowRecordMapper.deleteBorrowRecordById(id);
    }

    /**
     * 查询用户是否借阅了某本图书（借阅中）
     *
     * @param userId 用户ID
     * @param bookId 图书ID
     * @return 借阅记录，如果没有则返回null
     */
    @Override
    public BorrowRecord selectUserBorrowingBook(Long userId, Long bookId) {
        return borrowRecordMapper.selectUserBorrowingBook(userId, bookId);
    }

    /**
     * 借书
     *
     * @param bookId 图书ID
     * @return 结果
     */
    @Override
    @Transactional
    public int borrowBook(Long bookId) {
        // 获取当前登录用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null) {
            throw new LibraryException(LibraryExceptionEnum.USER_NOT_LOGIN);
        }
        Long currentUserId = loginUser.getUser().getUserId();

        // 查询图书信息
        Book book = bookMapper.selectBookById(bookId);
        if (book == null) {
            throw new LibraryException(LibraryExceptionEnum.BOOK_NOT_EXIST);
        }

        // 检查图书是否上架
        if (!"0".equals(book.getStatus())) {
            throw new LibraryException(LibraryExceptionEnum.BOOK_NOT_AVAILABLE);
        }

        // 检查图书可借数量
        if (book.getAvailableQuantity() <= 0) {
            throw new LibraryException(LibraryExceptionEnum.BOOK_NO_STOCK);
        }

        // 检查用户是否有逾期未还的图书
        boolean hasOverdue = borrowRecordMapper.checkUserHasOverdue(currentUserId);
        if (hasOverdue) {
            throw new LibraryException(LibraryExceptionEnum.USER_HAS_OVERDUE);
        }

        // 统计用户当前借阅数量
        int borrowCount = borrowRecordMapper.countUserBorrowing(currentUserId);
        if (borrowCount >= 5) {
            throw new LibraryException(LibraryExceptionEnum.BORROW_LIMIT_EXCEEDED);
        }

        // 创建借阅记录
        BorrowRecord borrowRecord = new BorrowRecord();
        borrowRecord.setBookId(bookId);
        borrowRecord.setUserId(currentUserId);
        borrowRecord.setBorrowTime(new Date());
        
        // 应还日期：借书日期 + 30天
        LocalDateTime borrowDateTime = LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault());
        LocalDateTime dueDateTime = borrowDateTime.plusDays(30);
        borrowRecord.setDueDate(Date.from(dueDateTime.atZone(ZoneId.systemDefault()).toInstant()));
        
        borrowRecord.setStatus("0");
        borrowRecord.setRenewCount(0);
        borrowRecord.setCreateTime(DateUtils.getNowDate());
        
        // 插入借阅记录
        borrowRecordMapper.insertBorrowRecord(borrowRecord);

        // 更新图书数量
        book.setAvailableQuantity(book.getAvailableQuantity() - 1);
        book.setBorrowedQuantity(book.getBorrowedQuantity() + 1);
        book.setUpdateTime(DateUtils.getNowDate());
        bookMapper.updateBook(book);

        return 1;
    }

    /**
     * 续借图书
     *
     * @param id 借阅记录ID
     * @return 结果
     */
    @Override
    @Transactional
    public int renewBorrow(Long id) {
        // 查询借阅记录
        BorrowRecord borrowRecord = borrowRecordMapper.selectBorrowRecordById(id);
        if (borrowRecord == null) {
            throw new LibraryException(LibraryExceptionEnum.BORROW_RECORD_NOT_EXIST);
        }

        // 检查是否已归还
        if ("1".equals(borrowRecord.getStatus())) {
            throw new LibraryException(LibraryExceptionEnum.BORROW_ALREADY_RETURNED);
        }

        // 检查是否已逾期（实时计算）
        if (borrowRecord.isOverdue()) {
            throw new LibraryException(LibraryExceptionEnum.BORROW_OVERDUE);
        }

        // 检查续借次数
        if (borrowRecord.getRenewCount() >= 3) {
            throw new LibraryException(LibraryExceptionEnum.RENEW_LIMIT_EXCEEDED);
        }

        // 续借次数 +1
        borrowRecord.setRenewCount(borrowRecord.getRenewCount() + 1);
        
        // 应还日期延后30天
        LocalDateTime dueDateTime = LocalDateTime.ofInstant(borrowRecord.getDueDate().toInstant(), ZoneId.systemDefault());
        LocalDateTime newDueDateTime = dueDateTime.plusDays(30);
        borrowRecord.setDueDate(Date.from(newDueDateTime.atZone(ZoneId.systemDefault()).toInstant()));
        
        borrowRecord.setUpdateTime(DateUtils.getNowDate());
        
        return borrowRecordMapper.updateBorrowRecord(borrowRecord);
    }

    /**
     * 归还图书
     *
     * @param id 借阅记录ID
     * @return 结果
     */
    @Override
    @Transactional
    public int returnBorrow(Long id) {
        // 查询借阅记录
        BorrowRecord borrowRecord = borrowRecordMapper.selectBorrowRecordById(id);
        if (borrowRecord == null) {
            throw new LibraryException(LibraryExceptionEnum.BORROW_RECORD_NOT_EXIST);
        }

        // 检查是否已归还
        if ("1".equals(borrowRecord.getStatus())) {
            throw new LibraryException(LibraryExceptionEnum.BORROW_ALREADY_RETURNED);
        }

        // 更新借阅记录状态为已归还
        borrowRecord.setStatus("1");
        borrowRecord.setUpdateTime(DateUtils.getNowDate());
        
        borrowRecordMapper.updateBorrowRecord(borrowRecord);

        // 更新图书数量
        Book book = bookMapper.selectBookById(borrowRecord.getBookId());
        if (book != null) {
            book.setAvailableQuantity(book.getAvailableQuantity() + 1);
            book.setBorrowedQuantity(book.getBorrowedQuantity() - 1);
            book.setUpdateTime(DateUtils.getNowDate());
            bookMapper.updateBook(book);
        }

        return 1;
    }
}
