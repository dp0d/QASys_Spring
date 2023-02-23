package com.exam.qa_robot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

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
 * @since 2022-06-25
 */
@Getter
@Setter
@TableName("_question")
@ApiModel(value = "Question对象", description = "")
public class Question implements Serializable {

    private static final long serialVersionUID = 1L;

      @ApiModelProperty("问题id，自动增长")
        @TableId(value = "qid", type = IdType.AUTO)
      private Integer qid;

      @ApiModelProperty("对应用户")
      private Integer uid;

      @ApiModelProperty("提出时间")
      private Date raiseDt;

      @ApiModelProperty("内容")
      private String text;

      @ApiModelProperty("类型")
      private Integer type;

//    @ApiModelProperty("从内容中提取的关键词")
//      private String keywords;

}