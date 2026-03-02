package com.library.service;

import java.util.List;
import com.library.domain.BorrowRecord;

/**
 * 借阅记录Service接口
 * 
 * @author xiangziyang
 * @date 2026-03-02
 */
public interface IBorrowRecordService {
    /**
     * 查询借阅记录
     * 
     * @param id 借阅记录主键
     * @return 借阅记录
     */
    BorrowRecord selectBorrowRecordById(Long id);

    /**
     * 查询借阅记录列表
     * 
     * @param borrowRecord 借阅记录
     * @return 借阅记录集合
     */
    List<BorrowRecord> selectBorrowRecordList(BorrowRecord borrowRecord);

    /**
     * 查询借阅记录列表（借阅中+逾期）
     * 
     * @param borrowRecord 借阅记录
     * @return 借阅记录集合
     */
    List<BorrowRecord> selectBorrowingList(BorrowRecord borrowRecord);

    /**
     * 新增借阅记录
     * 
     * @param borrowRecord 借阅记录
     * @return 结果
     */
    int insertBorrowRecord(BorrowRecord borrowRecord);

    /**
     * 修改借阅记录
     * 
     * @param borrowRecord 借阅记录
     * @return 结果
     */
    int updateBorrowRecord(BorrowRecord borrowRecord);

    /**
     * 批量删除借阅记录
     * 
     * @param ids 需要删除的借阅记录主键集合
     * @return 结果
     */
    int deleteBorrowRecordByIds(Long[] ids);

    /**
     * 删除借阅记录
     * 
     * @param id 借阅记录主键
     * @return 结果
     */
    int deleteBorrowRecordById(Long id);

    /**
     * 借书
     * 
     * @param bookId 图书ID
     * @return 结果
     */
    int borrowBook(Long bookId);

    /**
     * 续借图书
     * 
     * @param id 借阅记录ID
     * @return 结果
     */
    int renewBorrow(Long id);

    /**
     * 归还图书
     * 
     * @param id 借阅记录ID
     * @return 结果
     */
    int returnBorrow(Long id);

    /**
     * 查询用户是否借阅了某本图书（借阅中）
     * 
     * @param userId 用户ID
     * @param bookId 图书ID
     * @return 借阅记录，如果没有则返回null
     */
    BorrowRecord selectUserBorrowingBook(Long userId, Long bookId);
}
