package com.library.mapper;

import java.util.List;
import com.library.domain.BorrowRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 借阅记录Mapper接口
 * 
 * @author xiangziyang
 * @date 2026-03-02
 */
@Mapper
public interface BorrowRecordMapper {
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
     * 删除借阅记录
     * 
     * @param id 借阅记录主键
     * @return 结果
     */
    int deleteBorrowRecordById(Long id);

    /**
     * 批量删除借阅记录
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteBorrowRecordByIds(Long[] ids);

    /**
     * 检查用户是否有逾期未还的图书
     * 
     * @param userId 用户ID
     * @return true-有逾期，false-无逾期
     */
    boolean checkUserHasOverdue(Long userId);

    /**
     * 统计用户当前借阅数量
     * 
     * @param userId 用户ID
     * @return 借阅数量
     */
    int countUserBorrowing(Long userId);

    /**
     * 查询用户是否借阅了某本图书（借阅中）
     * 
     * @param userId 用户ID
     * @param bookId 图书ID
     * @return 借阅记录，如果没有则返回null
     */
    BorrowRecord selectUserBorrowingBook(@Param("userId") Long userId, @Param("bookId") Long bookId);
}
