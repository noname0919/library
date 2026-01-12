package com.library.common.exception;

/**
 * 图书管理系统异常枚举类
 * 
 * @author ruoyi
 */
public enum LibraryExceptionEnum {
    /** 通用异常 */
    COMMON_ERROR(500, "操作失败"),
    
    /** 图书相关异常 */
    BOOK_NOT_FOUND(1001, "图书不存在"),
    BOOK_EXIST(1002, "图书已存在"),
    BOOK_STOCK_NOT_ENOUGH(1003, "图书库存不足"),
    BOOK_BORROW_LIMIT_EXCEEDED(1004, "图书借阅数量已达上限"),
    BOOK_RETURN_DATE_EXCEEDED(1005, "图书已超期归还"),

    
    /** 借阅相关异常 */
    BORROW_RECORD_NOT_FOUND(3001, "借阅记录不存在"),
    BORROW_RECORD_ALREADY_RETURNED(3002, "该借阅记录已归还"),
    BORROW_RECORD_ALREADY_OVERDUE(3003, "该借阅记录已超期"),
    
    /** 分类相关异常 */
    CATEGORY_NOT_FOUND(4001, "分类不存在"),
    CATEGORY_NAME_EXIST(4002, "分类名称已存在"),
    CATEGORY_HAS_CHILDREN(4003, "分类下存在子分类，无法删除"),
    CATEGORY_HAS_BOOKS(4004, "分类下存在图书，无法删除"),
    ;
    
    /** 异常码 */
    private final Integer code;
    
    /** 异常消息 */
    private final String message;
    
    LibraryExceptionEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
}