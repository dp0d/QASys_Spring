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
@TableName("_book")
@ApiModel(value = "Book对象", description = "")
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

      @ApiModelProperty("书id")
        private Integer bid;

      @ApiModelProperty("书名")
      private String bookName;

      @ApiModelProperty("对应学科分类id")
      private Integer cid;

      private String  name;


}
