package com.exam.qa_robot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@TableName("_user")
@ApiModel(value = "User对象", description = "")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

      @ApiModelProperty("微机编码，自动增长，主键")
      @TableId(value = "uid", type = IdType.AUTO)
      private Integer uid;

      @ApiModelProperty("用户昵称")
        private String username;

      @JsonInclude(JsonInclude.Include.NON_EMPTY)
      @ApiModelProperty("密码")
      private String passwd;

      @ApiModelProperty("性别")
      private String gender;

      @ApiModelProperty("账号创建时间")
      private LocalDateTime creatDt;

      @ApiModelProperty("累计提问数")
      private Integer qNum;

      @ApiModelProperty("电话号")
      private String phone;

      @ApiModelProperty("邮箱")
      private String email;

      @ApiModelProperty("用户类别：(1:普通用户）（2:管理）（3：超管）")
      private Integer type;

      @ApiModelProperty("账号是否启用：1")
      private Integer enabled;

      @ApiModelProperty("头像")
      private String avatarUrl;


}
