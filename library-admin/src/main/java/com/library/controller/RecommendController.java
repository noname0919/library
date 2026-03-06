package com.library.controller;

import com.library.common.annotation.Log;
import com.library.common.core.controller.BaseController;
import com.library.common.core.domain.AjaxResult;
import com.library.common.enums.BusinessType;
import com.library.domain.Book;
import com.library.service.IRecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 图书推荐Controller
 * 
 * @author xiangziyang
 * @date 2026-03-03
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/library/recommend")
public class RecommendController extends BaseController {

    private final IRecommendService recommendService;

    /**
     * 随机推荐
     * 
     * @param limit 推荐数量，默认10
     * @return 图书列表
     */
    @PreAuthorize("@ss.hasPermi('library:recommend:random')")
    @Log(title = "图书推荐", businessType = BusinessType.OTHER)
    @GetMapping("/random")
    public AjaxResult randomRecommend(@RequestParam(defaultValue = "10") Integer limit) {
        List<Book> books = recommendService.randomRecommend(limit);
        return AjaxResult.success(books);
    }

    /**
     * 热门推荐
     * 
     * @param limit 推荐数量，默认10
     * @return 图书列表
     */
    @PreAuthorize("@ss.hasPermi('library:recommend:hot')")
    @Log(title = "图书推荐", businessType = BusinessType.OTHER)
    @GetMapping("/hot")
    public AjaxResult hotRecommend(@RequestParam(defaultValue = "10") Integer limit) {
        List<Book> books = recommendService.hotRecommend(limit);
        return AjaxResult.success(books);
    }

    /**
     * 关键词推荐
     * 
     * @param limit 推荐数量，默认10
     * @return 图书列表
     */
    @PreAuthorize("@ss.hasPermi('library:recommend:keyword')")
    @Log(title = "图书推荐", businessType = BusinessType.OTHER)
    @GetMapping("/keyword")
    public AjaxResult keywordRecommend(@RequestParam(defaultValue = "10") Integer limit) {
        Long userId = getUserId();
        List<Book> books = recommendService.keywordRecommend(userId, limit);
        return AjaxResult.success(books);
    }

    /**
     * 综合推荐
     * 
     * @param limit 推荐数量，默认10
     * @return 图书列表
     */
    @PreAuthorize("@ss.hasPermi('library:recommend:all')")
    @Log(title = "图书推荐", businessType = BusinessType.OTHER)
    @GetMapping("/all")
    public AjaxResult mixedRecommend(@RequestParam(defaultValue = "10") Integer limit) {
        Long userId = getUserId();
        List<Book> books = recommendService.mixedRecommend(userId, limit);
        return AjaxResult.success(books);
    }
}
