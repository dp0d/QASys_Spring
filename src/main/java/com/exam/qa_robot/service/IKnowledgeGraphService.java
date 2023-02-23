package com.exam.qa_robot.service;

import com.exam.qa_robot.entity.KnowledgeGraph;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author vaifer
 * @since 2022-07-11
 */
public interface IKnowledgeGraphService extends IService<KnowledgeGraph> {

    List<KnowledgeGraph> getKg(String entity);
}
