package com.exam.qa_robot.controller.dto;

import com.exam.qa_robot.entity.Book;
import lombok.Data;
import org.apache.ibatis.annotations.Delete;

import java.util.List;

/**
 * 接受前端登录请求的参数
 */
@Data
public class UserDTO {
    private Integer uid;
    private String avatarUrl;
    private String username;
    private String passwd;
    private String token;
    private String email;
    private Integer type;
    private String code;//邮箱验证码
    private List<Book> bookList;
}
