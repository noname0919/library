package com.library.domain;

/**
 * 图书分类占比DTO
 *
 * @author xiangziyang
 * @date 2026-03-14
 */
public class CategoryStatsDTO {
    private String name;
    private Integer value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}