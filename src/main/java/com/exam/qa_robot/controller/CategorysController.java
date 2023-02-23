package com.exam.qa_robot.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.qa_robot.entity.Book;
import com.exam.qa_robot.service.IBookService;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.exam.qa_robot.common.Result;

import com.exam.qa_robot.service.ICategorysService;
import com.exam.qa_robot.entity.Categorys;


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
@RequestMapping("/categorys")
public class CategorysController {

    @Resource
    private ICategorysService categorysService;

    @Resource
    private IBookService bookService;



    @PostMapping
    public Result  save(@RequestBody Categorys categorys){
        //新增或者更新
        return Result.success(categorysService.saveOrUpdate(categorys));
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id){
        return  Result.success(categorysService.removeById(id));
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids){
        return  Result.success(categorysService.removeBatchByIds(ids));
    }

    /**
     * 查询学科分类及书名
     * @return
     */
    @GetMapping
    public Result findCategorys(){
        List<Categorys> categorysList = categorysService.list();

        HashMap<String, List<Book>> map = new HashMap();
        for (Categorys one :categorysList){
            QueryWrapper<Book> wrapper=new QueryWrapper();
            wrapper.orderByDesc("bid");
            System.out.println("122222222222222");
            Integer cid = one.getCid();
            String category = one.getCategory();
            wrapper.eq("cid",cid);
            List<Book> list = bookService.list(wrapper);
            map.put(category,list);

        }
        return  Result.success(map);
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id){
        return Result.success(categorysService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {
        QueryWrapper<Categorys> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        return Result.success(categorysService.page(new Page<>(pageNum, pageSize),queryWrapper));
    }




}

