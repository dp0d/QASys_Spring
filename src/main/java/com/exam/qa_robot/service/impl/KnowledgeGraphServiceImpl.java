package com.exam.qa_robot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.exam.qa_robot.entity.KnowledgeGraph;
import com.exam.qa_robot.mapper.KnowledgeGraphMapper;
import com.exam.qa_robot.service.IKnowledgeGraphService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author vaifer
 * @since 2022-07-11
 */
@Service
public class KnowledgeGraphServiceImpl extends ServiceImpl<KnowledgeGraphMapper, KnowledgeGraph> implements IKnowledgeGraphService {

    @Resource
    private KnowledgeGraphMapper knowledgeGraphMapper;
    @Override
    public List<KnowledgeGraph> getKg(String entity) {
        QueryWrapper<KnowledgeGraph> wrapper = new QueryWrapper();
        wrapper.like("k", entity);
        wrapper.orderByDesc("id");
        wrapper.select("Article_title", "section_title","k");
        List<KnowledgeGraph> knowledgeGraphs = knowledgeGraphMapper.selectList(wrapper);

        List<String> p2=new ArrayList<>();
        List<String> p3=new ArrayList<>();
        List<KnowledgeGraph> knowledgeGraphList= new ArrayList<KnowledgeGraph>();
        for (KnowledgeGraph k :knowledgeGraphs){
            String k1 = k.getK();
            String[] arr4 = ("| "+k1+" |").split("\\s+");
            for (int i =1;i<arr4.length-1; i++) {
                if (arr4[i].equals(entity)){continue;}
                if (!p2.contains(arr4[i])&&!p3.contains(k.getSectionTitle())){
                    p2.add(arr4[i]);
                    p3.add(k.getSectionTitle());
                    KnowledgeGraph knowledgeGraph=new KnowledgeGraph(entity,arr4[i],k.getSectionTitle());
                    knowledgeGraphList.add(knowledgeGraph);
                }

            }
        }
        return knowledgeGraphList;
    }
}
