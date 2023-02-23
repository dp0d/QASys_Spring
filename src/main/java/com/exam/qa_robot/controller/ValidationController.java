package com.exam.qa_robot.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.exam.qa_robot.common.Result;

import com.exam.qa_robot.service.IValidationService;
import com.exam.qa_robot.entity.Validation;


import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author vaifer
 * @since 2022-07-01
 */
@RestController
@RequestMapping("/validation")
public class ValidationController {

    @Resource
    private IValidationService validationService;


    @PostMapping
    public Result  save(@RequestBody Validation validation){
        //新增或者更新
        return Result.success(validationService.saveOrUpdate(validation));
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id){
        return  Result.success(validationService.removeById(id));
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids){
        return  Result.success(validationService.removeBatchByIds(ids));
    }

    @GetMapping
    public Result findAll(){
        return  Result.success(validationService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id){
        return Result.success(validationService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                                    @RequestParam Integer pageSize) {
        QueryWrapper<Validation> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("vid");
        return Result.success(validationService.page(new Page<>(pageNum, pageSize),queryWrapper));
    }




}

