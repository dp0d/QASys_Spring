package com.exam.qa_robot.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.exam.qa_robot.common.Result;

import com.exam.qa_robot.service.IKnowledgeGraphService;
import com.exam.qa_robot.entity.KnowledgeGraph;


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
@RequestMapping("/kgraph")
public class KnowledgeGraphController {

    @Resource
    private IKnowledgeGraphService knowledgeGraphService;


    @PostMapping
    public Result  save(@RequestBody KnowledgeGraph knowledgeGraph){
        //新增或者更新
        return Result.success(knowledgeGraphService.saveOrUpdate(knowledgeGraph));
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id){
        return  Result.success(knowledgeGraphService.removeById(id));
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids){
        return  Result.success(knowledgeGraphService.removeBatchByIds(ids));
    }

    @GetMapping
    public Result findAll(){
        return  Result.success(knowledgeGraphService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id){
        return Result.success(knowledgeGraphService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                                    @RequestParam Integer pageSize) {
        QueryWrapper<KnowledgeGraph> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        return Result.success(knowledgeGraphService.page(new Page<>(pageNum, pageSize),queryWrapper));
    }




}

