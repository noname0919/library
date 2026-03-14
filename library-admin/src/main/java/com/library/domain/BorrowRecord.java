package com.library.domain;

import java.util.Date;
import javax.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.library.common.annotation.Excel;
import com.library.common.core.domain.BaseEntity;

/**
 * 借阅记录对象 borrow_record
 *
 * @author xiangziyang
 * @date 2026-03-02
 */
@Data
public class BorrowRecord extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 借阅记录ID */
    @ApiModelProperty(value = "借阅记录ID")
    private Long id;

    /** 图书ID */
    @NotNull(message = "图书ID不能为空")
    @ApiModelProperty(value = "图书ID")
    private Long bookId;

    /** 用户ID（读者） */
    @NotNull(message = "用户ID不能为空")
    @ApiModelProperty(value = "用户ID")
    private Long userId;

    /** 借书时间 */
    @NotNull(message = "借书时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "借书时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date borrowTime;

    /** 应还日期 */
    @NotNull(message = "应还日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "应还日期", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date dueDate;

    /** 续借次数 */
    @ApiModelProperty(value = "续借次数")
    private Integer renewCount;

    /** 归还时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "归还时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date returnTime;

    /** 借阅状态（0借阅中，1已归还，2逾期） */
    @NotBlank(message = "借阅状态不能为空")
    @Pattern(regexp = "^[012]$", message = "借阅状态只能是0、1或2")
    @Excel(name = "借阅状态", readConverterExp = "0=借阅中,1=已归还,2=逾期")
    private String status;

    /** 图书名称（非数据库字段，用于展示） */
    @ApiModelProperty(value = "图书名称")
    @Excel(name = "图书名称")
    private String bookName;

    /** ISBN号（非数据库字段，用于展示） */
    @ApiModelProperty(value = "ISBN号")
    @Excel(name = "ISBN号")
    private String isbn;

    /** 读者账号（非数据库字段，用于展示） */
    @ApiModelProperty(value = "读者账号")
    @Excel(name = "读者账号")
    private String userName;

    /** 读者姓名（非数据库字段，用于展示） */
    @ApiModelProperty(value = "读者姓名")
    @Excel(name = "读者姓名")
    private String nickName;

    /**
     * 判断是否逾期（实时计算）
     * @return true-已逾期，false-未逾期
     */
    public boolean isOverdue() {
        if ("1".equals(this.status)) {
            return false; // 已归还不算逾期
        }
        if (this.dueDate == null) {
            return false;
        }
        return new Date().after(this.dueDate);
    }

    /**
     * 获取实际状态（实时计算是否逾期）
     * @return 0-借阅中，1-已归还，2-逾期
     */
    public String getActualStatus() {
        if ("1".equals(this.status)) {
            return "1"; // 已归还
        }
        return isOverdue() ? "2" : "0";
    }
}
