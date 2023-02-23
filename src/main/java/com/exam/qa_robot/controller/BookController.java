package com.exam.qa_robot.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.exam.qa_robot.common.Result;
import com.exam.qa_robot.service.IBookService;
import com.exam.qa_robot.entity.Book;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author vaifer
 * @since 2022-07-11
 */
@RestController
@RequestMapping("/book")
public class BookController {

    @Resource
    private IBookService bookService;


    @PostMapping
    public Result  save(@RequestBody Book book){
        //新增或者更新
        return Result.success(bookService.saveOrUpdate(book));
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id){
        return  Result.success(bookService.removeById(id));
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids){
        return  Result.success(bookService.removeBatchByIds(ids));
    }

    @GetMapping
    public Result findAll(){
        return  Result.success(bookService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id){
        return Result.success(bookService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                                    @RequestParam Integer pageSize) {
        QueryWrapper<Book> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        return Result.success(bookService.page(new Page<>(pageNum, pageSize),queryWrapper));
    }




}

