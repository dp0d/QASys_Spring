package com.exam.qa_robot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author vaifer
 * @since 2022-07-08
 */
@Getter
@Setter
@AllArgsConstructor
  @TableName("_keywords")
@ApiModel(value = "Keywords对象", description = "")
public class Keywords implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "wid", type = IdType.AUTO)
      private Integer wid;

      @ApiModelProperty("名词")
      private String word;

      @ApiModelProperty("频率")
      private Integer frequency;

      private LocalDateTime date;
      private String qid_list;


}
