package com.exam.qa_robot.controller.dto;

import com.exam.qa_robot.entity.KnowledgeGraph;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 问题答案封装类
 */
@Data
@AllArgsConstructor
public class AnswerDTO {
    private String answer;
    private String Context;
    private String title;
    private String entity;
    private List<KnowledgeGraph> list;

}
