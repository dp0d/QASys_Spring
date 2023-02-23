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
 * @since 2022-06-27
 */
@Getter
@Setter
@TableName("_corpus")
@ApiModel(value = "Corpus对象", description = "")
public class Corpus implements Serializable {

    private static final long serialVersionUID = 1L;

      @ApiModelProperty("文件编号")
        @TableId(value = "fid", type = IdType.AUTO)
      private Integer fid;

      @ApiModelProperty("对应用户id")
      private Integer uid;

      @ApiModelProperty("原始文件名")
      private String filename;

      @ApiModelProperty("文件保存地址")
      private String docUrl;

      @ApiModelProperty("学科分类id")
      private Integer cid;

      @ApiModelProperty("文件类型")
      private String type;

      @ApiModelProperty("文件大小")
      private Long size;

      @ApiModelProperty("文件md5")
      private String md5;

      @ApiModelProperty("完全的处理结果")
      private String rurl;

      @ApiModelProperty("正文")
      private String durl;

      @ApiModelProperty("结构化文本")
      private String surl;


}
