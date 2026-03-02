package com.library.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.library.common.annotation.Log;
import com.library.common.core.controller.BaseController;
import com.library.common.core.domain.AjaxResult;
import com.library.common.enums.BusinessType;
import com.library.domain.BorrowRecord;
import com.library.service.IBorrowRecordService;
import com.library.common.utils.poi.ExcelUtil;
import com.library.common.core.page.TableDataInfo;

/**
 * 借阅记录Controller
 * 
 * @author xiangziyang
 * @date 2026-03-02
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/library/borrow")
public class BorrowRecordController extends BaseController {
    private final IBorrowRecordService borrowRecordService;

    /**
     * 查询借阅记录列表（借阅中+逾期）
     */
    @PreAuthorize("@ss.hasPermi('library:borrow:list')")
    @GetMapping("/list")
    public TableDataInfo list(BorrowRecord borrowRecord)
    {
        startPage();
        List<BorrowRecord> list = borrowRecordService.selectBorrowRecordList(borrowRecord);
        return getDataTable(list);
    }

    /**
     * 查询借阅记录列表（借阅中+逾期，排除已归还）
     */
    @PreAuthorize("@ss.hasPermi('library:borrow:list')")
    @GetMapping("/borrowing")
    public TableDataInfo borrowingList(BorrowRecord borrowRecord)
    {
        startPage();
        List<BorrowRecord> list = borrowRecordService.selectBorrowingList(borrowRecord);
        return getDataTable(list);
    }

    /**
     * 查询归还记录列表（已归还）
     */
    @PreAuthorize("@ss.hasPermi('library:borrow:list')")
    @GetMapping("/returned")
    public TableDataInfo returnedList(BorrowRecord borrowRecord)
    {
        borrowRecord.setStatus("1");
        startPage();
        List<BorrowRecord> list = borrowRecordService.selectBorrowRecordList(borrowRecord);
        return getDataTable(list);
    }

    /**
     * 导出借阅记录列表
     */
    @PreAuthorize("@ss.hasPermi('library:borrow:export')")
    @Log(title = "借阅记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BorrowRecord borrowRecord)
    {
        List<BorrowRecord> list = borrowRecordService.selectBorrowRecordList(borrowRecord);
        ExcelUtil<BorrowRecord> util = new ExcelUtil<BorrowRecord>(BorrowRecord.class);
        util.exportExcel(response, list, "借阅记录数据");
    }

    /**
     * 获取借阅记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('library:borrow:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(borrowRecordService.selectBorrowRecordById(id));
    }

    /**
     * 续借图书
     */
    @PreAuthorize("@ss.hasPermi('library:borrow:renew')")
    @Log(title = "续借图书", businessType = BusinessType.UPDATE)
    @PutMapping("/renew/{id}")
    public AjaxResult renew(@PathVariable Long id)
    {
        return toAjax(borrowRecordService.renewBorrow(id));
    }
}
