package com.exam.qa_robot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
  @TableName("_categorys")
@ApiModel(value = "Categorys对象", description = "")
public class Categorys implements Serializable {

    private static final long serialVersionUID = 1L;

      @ApiModelProperty("学科分类id")
        @TableId(value = "cid", type = IdType.AUTO)
      private Integer cid;

      @ApiModelProperty("学科分类")
      private String category;


}
