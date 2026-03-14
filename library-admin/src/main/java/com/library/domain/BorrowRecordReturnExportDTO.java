package com.library.domain;

import com.library.common.annotation.Excel;

/**
 * 归还记录导出DTO
 *
 * @author xiangziyang
 * @date 2026-03-15
 */
public class BorrowRecordReturnExportDTO {
    @Excel(name = "图书名称")
    private String bookName;
    
    @Excel(name = "ISBN")
    private String isbn;
    
    @Excel(name = "读者账号")
    private String userName;
    
    @Excel(name = "读者姓名")
    private String nickName;
    
    @Excel(name = "借阅时间")
    private String borrowTime;
    
    @Excel(name = "应还日期")
    private String dueDate;
    
    @Excel(name = "归还时间")
    private String returnTime;

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getBorrowTime() {
        return borrowTime;
    }

    public void setBorrowTime(String borrowTime) {
        this.borrowTime = borrowTime;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(String returnTime) {
        this.returnTime = returnTime;
    }
}