package com.library.domain;

import com.library.common.annotation.Excel;

/**
 * 图书分类占比DTO
 *
 * @author xiangziyang
 * @date 2026-03-14
 */
public class CategoryStatsDTO {
    @Excel(name = "分类名称", sort = 1)
    private String name;
    
    @Excel(name = "数量", sort = 2)
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}