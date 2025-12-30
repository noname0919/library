package com.library.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
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
public class Book extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 图书ID */
    private Long id;

    /** ISBN号 */
    @Excel(name = "ISBN号")
    private String isbn;

    /** 图书名称 */
    @Excel(name = "图书名称")
    private String bookName;

    /** 作者姓名 */
    @Excel(name = "作者姓名")
    private String authorName;

    /** 出版社名称 */
    @Excel(name = "出版社名称")
    private String publisherName;

    /** 分类 */
    @Excel(name = "分类")
    private Long categoryId;

    /** 出版日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "出版日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date publishDate;

    /** 图书价格 */
    @Excel(name = "图书价格")
    private BigDecimal price;

    /** 封面图片 */
    @Excel(name = "封面图片")
    private String image;

    /** 总馆藏数量 */
    @Excel(name = "总馆藏数量")
    private Long totalQuantity;

    /** 可借数量 */
    @Excel(name = "可借数量")
    private Long availableQuantity;

    /** 已借数量 */
    @Excel(name = "已借数量")
    private Long borrowedQuantity;

    /** 图书状态（0上架 1下架） */
    @Excel(name = "图书状态", readConverterExp = "0=上架,1=下架")
    private String status;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setIsbn(String isbn) 
    {
        this.isbn = isbn;
    }

    public String getIsbn() 
    {
        return isbn;
    }

    public void setBookName(String bookName) 
    {
        this.bookName = bookName;
    }

    public String getBookName() 
    {
        return bookName;
    }

    public void setAuthorName(String authorName) 
    {
        this.authorName = authorName;
    }

    public String getAuthorName() 
    {
        return authorName;
    }

    public void setPublisherName(String publisherName) 
    {
        this.publisherName = publisherName;
    }

    public String getPublisherName() 
    {
        return publisherName;
    }

    public void setCategoryId(Long categoryId) 
    {
        this.categoryId = categoryId;
    }

    public Long getCategoryId() 
    {
        return categoryId;
    }

    public void setPublishDate(Date publishDate) 
    {
        this.publishDate = publishDate;
    }

    public Date getPublishDate() 
    {
        return publishDate;
    }

    public void setPrice(BigDecimal price) 
    {
        this.price = price;
    }

    public BigDecimal getPrice() 
    {
        return price;
    }

    public void setImage(String image) 
    {
        this.image = image;
    }

    public String getImage() 
    {
        return image;
    }

    public void setTotalQuantity(Long totalQuantity) 
    {
        this.totalQuantity = totalQuantity;
    }

    public Long getTotalQuantity() 
    {
        return totalQuantity;
    }

    public void setAvailableQuantity(Long availableQuantity) 
    {
        this.availableQuantity = availableQuantity;
    }

    public Long getAvailableQuantity() 
    {
        return availableQuantity;
    }

    public void setBorrowedQuantity(Long borrowedQuantity) 
    {
        this.borrowedQuantity = borrowedQuantity;
    }

    public Long getBorrowedQuantity() 
    {
        return borrowedQuantity;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("isbn", getIsbn())
            .append("bookName", getBookName())
            .append("authorName", getAuthorName())
            .append("publisherName", getPublisherName())
            .append("categoryId", getCategoryId())
            .append("publishDate", getPublishDate())
            .append("price", getPrice())
            .append("image", getImage())
            .append("totalQuantity", getTotalQuantity())
            .append("availableQuantity", getAvailableQuantity())
            .append("borrowedQuantity", getBorrowedQuantity())
            .append("status", getStatus())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
