package com.library.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.library.common.core.domain.model.LoginUser;
import com.library.common.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.library.common.annotation.Log;
import com.library.common.core.controller.BaseController;
import com.library.common.core.domain.AjaxResult;
import com.library.common.enums.BusinessType;
import com.library.domain.Book;
import com.library.service.IBookService;
import com.library.service.IBorrowRecordService;
import com.library.common.utils.poi.ExcelUtil;
import com.library.common.core.page.TableDataInfo;

/**
 * 图书基本信息Controller
 * 
 * @author xiangziyang
 * @date 2025-12-30
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/library/book")
public class BookController extends BaseController
{
    private final IBookService bookService;
    private final IBorrowRecordService borrowRecordService;

    /**
     * 查询图书基本信息列表
     */
    @PreAuthorize("@ss.hasPermi('library:book:list')")
    @GetMapping("/list")
    public TableDataInfo list(Book book)
    {
        startPage();
        List<Book> list = bookService.selectBookList(book);
        return getDataTable(list);
    }

    /**
     * 导出图书基本信息列表
     */
    @PreAuthorize("@ss.hasPermi('library:book:export')")
    @Log(title = "图书基本信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Book book)
    {
        List<Book> list = bookService.selectBookList(book);
        ExcelUtil<Book> util = new ExcelUtil<Book>(Book.class);
        util.exportExcel(response, list, "图书基本信息数据");
    }

    /**
     * 获取图书基本信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('library:book:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(bookService.selectBookById(id));
    }

    /**
     * 新增图书基本信息
     */
    @PreAuthorize("@ss.hasPermi('library:book:add')")
    @Log(title = "图书基本信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Book book)
    {

        return toAjax(bookService.insertBook(book));
    }

    /**
     * 修改图书基本信息
     */
    @PreAuthorize("@ss.hasPermi('library:book:edit')")
    @Log(title = "图书基本信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Book book)
    {
        return toAjax(bookService.updateBook(book));
    }

    /**
     * 删除图书基本信息
     */
    @PreAuthorize("@ss.hasPermi('library:book:remove')")
    @Log(title = "图书基本信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {

        return toAjax(bookService.deleteBookByIds(ids));
    }

    /**
     * 借书
     */
    @PreAuthorize("@ss.hasPermi('library:book:borrow')")
    @Log(title = "借阅图书", businessType = BusinessType.INSERT)
    @PostMapping("/borrow/{bookId}")
    public AjaxResult borrow(@PathVariable Long bookId) {
        return toAjax(bookService.borrowBook(bookId));
    }

    /**
     * 还书
     */
    @PreAuthorize("@ss.hasPermi('library:book:return')")
    @Log(title = "归还图书", businessType = BusinessType.UPDATE)
    @PutMapping("/return/{recordId}")
    public AjaxResult returnBook(@PathVariable Long recordId) {
        return toAjax(borrowRecordService.returnBorrow(recordId));
    }
}
