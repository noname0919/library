package com.library.domain;

import java.math.BigDecimal;
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
 * 图书基本信息对象 book
 *
 * @author xiangziyang
 * @date 2025-12-30
 */
@Data
public class Book extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 图书ID */
    @ApiModelProperty(value = "图书ID")
    private Long id;

    /** ISBN号 */
    @NotBlank(message = "ISBN号不能为空")
    @Excel(name = "ISBN号")
    private String isbn;

    /** 图书名称 */
    @NotBlank(message = "图书名称不能为空")
    @Excel(name = "图书名称")
    private String bookName;

    /** 作者姓名 */
    @NotBlank(message = "作者姓名不能为空")
    @Excel(name = "作者姓名")
    private String authorName;

    /** 出版社名称 */
    @NotBlank(message = "出版社名称不能为空")
    @Excel(name = "出版社名称")
    private String publisherName;

    /** 分类 */
    @NotNull(message = "分类不能为空")
    @Excel(name = "分类", dictType = "library_category")
    private Long categoryId;

    /** 出版日期 */
    @NotNull(message = "出版日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "出版日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date publishDate;

    /** 图书价格 */
    @NotNull(message = "图书价格不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "图书价格必须大于0")
    @Excel(name = "图书价格")
    private BigDecimal price;

    /** 封面图片 */
    @NotBlank(message = "封面图片不能为空")
    private String image;

    /** 总馆藏数量 */
    @NotNull(message = "总馆藏数量不能为空")
    @Min(value = 0, message = "总馆藏数量不能小于0")
    @Excel(name = "总馆藏数量")
    private Long totalQuantity;

    /** 可借数量 */
    @NotNull(message = "可借数量不能为空")
    @Min(value = 0, message = "可借数量不能小于0")
    @Excel(name = "可借数量")
    private Long availableQuantity;

    /** 已借数量 */
    @NotNull(message = "已借数量不能为空")
    @Min(value = 0, message = "已借数量不能小于0")
    @Excel(name = "已借数量")
    private Long borrowedQuantity;

    /** 图书状态（0上架 1下架） */
    @NotBlank(message = "图书状态不能为空")
    @Pattern(regexp = "^[01]$", message = "图书状态只能是0或1")
    @Excel(name = "图书状态", readConverterExp = "0=上架,1=下架")
    private String status;

    /** 当前用户是否已借阅该图书（非数据库字段） */
    @ApiModelProperty(value = "当前用户是否已借阅")
    private Boolean isBorrowedByCurrentUser;

    /** 当前借阅记录ID（非数据库字段，用于还书） */
    @ApiModelProperty(value = "当前借阅记录ID")
    private Long currentBorrowRecordId;

}
