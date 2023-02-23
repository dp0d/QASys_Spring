package com.exam.qa_robot.controller.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


/**
 * 标签化对象
 */
@AllArgsConstructor
@Data
public class LableDto {
    private Integer id;
    private String label;
    private String text;
    private List<String> keys;
    private List<String> labels;
}
