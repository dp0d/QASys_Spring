package com.exam.qa_robot.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.exam.qa_robot.common.Result;

import com.exam.qa_robot.service.IKeywordsService;
import com.exam.qa_robot.entity.Keywords;


import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author vaifer
 * @since 2022-07-08
 */
@RestController
@RequestMapping("/keywords")
public class KeywordsController {

    @Resource
    private IKeywordsService keywordsService;


    /**
     * 获取热门关键词
     * @return
     */
    @PostMapping
    public Result  getmajor(){
        return Result.success(getmajorKeywords());
    }

    public List<Keywords> getmajorKeywords(){

        QueryWrapper<Keywords> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("frequency");//安装词频从大到小排序
        queryWrapper.last("limit 10");//获取前10个频率最高的名词

        return keywordsService.list(queryWrapper);
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id){
        return  Result.success(keywordsService.removeById(id));
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids){
        return  Result.success(keywordsService.removeBatchByIds(ids));
    }

    @GetMapping
    public Result findAll(){
        return  Result.success(keywordsService.list());
    }


    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id){
        return Result.success(keywordsService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                                    @RequestParam Integer pageSize) {
        QueryWrapper<Keywords> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        return Result.success(keywordsService.page(new Page<>(pageNum, pageSize),queryWrapper));
    }




}

