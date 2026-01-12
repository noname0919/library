package com.library.common.exception;

/**
 * 图书管理系统自定义业务异常
 * 
 * @author ruoyi
 */
public class LibraryException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    /**
     * 错误码
     */
    private Integer code;
    
    /**
     * 错误提示
     */
    private String message;
    
    /**
     * 错误明细，内部调试错误
     */
    private String detailMessage;
    
    /**
     * 构造函数
     * 
     * @param exceptionEnum 异常枚举
     */
    public LibraryException(LibraryExceptionEnum exceptionEnum) {
        this.message = exceptionEnum.getMessage();
        this.code = exceptionEnum.getCode();
    }
    
    /**
     * 构造函数
     * 
     * @param exceptionEnum 异常枚举
     * @param detailMessage 详细错误信息
     */
    public LibraryException(LibraryExceptionEnum exceptionEnum, String detailMessage) {
        this.message = exceptionEnum.getMessage();
        this.code = exceptionEnum.getCode();
        this.detailMessage = detailMessage;
    }
    
    /**
     * 构造函数
     * 
     * @param code 错误码
     * @param message 错误消息
     */
    public LibraryException(Integer code, String message) {
        this.message = message;
        this.code = code;
    }
    
    /**
     * 构造函数
     * 
     * @param code 错误码
     * @param message 错误消息
     * @param detailMessage 详细错误信息
     */
    public LibraryException(Integer code, String message, String detailMessage) {
        this.message = message;
        this.code = code;
        this.detailMessage = detailMessage;
    }
    
    /**
     * 构造函数
     * 
     * @param message 错误消息
     */
    public LibraryException(String message) {
        this.message = message;
        this.code = LibraryExceptionEnum.COMMON_ERROR.getCode();
    }
    
    /**
     * 构造函数
     * 
     * @param message 错误消息
     * @param detailMessage 详细错误信息
     */
    public LibraryException(String message, String detailMessage) {
        this.message = message;
        this.code = LibraryExceptionEnum.COMMON_ERROR.getCode();
        this.detailMessage = detailMessage;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public void setCode(Integer code) {
        this.code = code;
    }
    
    @Override
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getDetailMessage() {
        return detailMessage;
    }
    
    public void setDetailMessage(String detailMessage) {
        this.detailMessage = detailMessage;
    }
}