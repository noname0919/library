package com.library.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.library.common.annotation.Excel;
import com.library.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * 用户搜索历史对象 user_search_history
 * 
 * @author xiangziyang
 * @date 2026-03-03
 */
public class UserSearchHistory extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 历史记录ID */
    private Long historyId;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 搜索关键词 */
    @Excel(name = "搜索关键词")
    private String keyword;

    /** 搜索次数 */
    @Excel(name = "搜索次数")
    private Integer searchCount;

    /** 最后搜索时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "最后搜索时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date lastSearchTime;

    public Long getHistoryId() {
        return historyId;
    }

    public void setHistoryId(Long historyId) {
        this.historyId = historyId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Integer getSearchCount() {
        return searchCount;
    }

    public void setSearchCount(Integer searchCount) {
        this.searchCount = searchCount;
    }

    public Date getLastSearchTime() {
        return lastSearchTime;
    }

    public void setLastSearchTime(Date lastSearchTime) {
        this.lastSearchTime = lastSearchTime;
    }
}
