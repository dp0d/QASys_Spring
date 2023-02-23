package com.exam.qa_robot.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author vaifer
 * @since 2022-07-11
 */
@Getter
@Setter

  @TableName("knowledge_graph")
@ApiModel(value = "KnowledgeGraph对象", description = "")
public class KnowledgeGraph implements Serializable {

    private static final long serialVersionUID = 1L;

    private String articleTitle;

    private String sectionTitle;

    private String sectionText;

    private Integer id;

    private String k;

    public KnowledgeGraph(String articleTitle, String sectionTitle, String sectionText) {
        this.articleTitle = articleTitle;
        this.sectionTitle = sectionTitle;
        this.sectionText = sectionText;
    }
}
