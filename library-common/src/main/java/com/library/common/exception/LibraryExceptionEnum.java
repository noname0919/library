package com.library.common.exception;

/**
 * 图书管理系统异常枚举类
 * 
 * @author ruoyi
 */
public enum LibraryExceptionEnum {
    /** 通用异常 */
    COMMON_ERROR(500, "操作失败"),
    
    /** 用户相关异常 */
    USER_NOT_LOGIN(2001, "用户未登录"),
    
    /** 图书相关异常 */
    BOOK_NOT_EXIST(1001, "图书不存在"),
    BOOK_EXIST(1002, "图书已存在"),
    BOOK_NOT_AVAILABLE(1003, "图书未上架"),
    BOOK_NO_STOCK(1004, "图书暂无库存"),
    BOOK_QUANTITY_ERROR(1005, "图书数量错误"),
    
    /** 借阅相关异常 */
    USER_HAS_OVERDUE(3001, "您有逾期未还的图书，请先归还后再借书"),
    BORROW_LIMIT_EXCEEDED(3002, "您已达到借阅上限（最多5本）"),
    BORROW_RECORD_NOT_EXIST(3003, "借阅记录不存在"),
    BORROW_ALREADY_RETURNED(3004, "该图书已归还"),
    BORROW_OVERDUE(3005, "该图书已逾期，无法续借"),
    RENEW_LIMIT_EXCEEDED(3006, "该图书已续借3次，无法再次续借"),
    
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