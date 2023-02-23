package com.exam.qa_robot.controller.dto;

import lombok.Data;

@Data
public class UserPasswdDTO {
    private int uid;
    private String phone;
    private String passwd;
    private String newpasswd;
    private String email;
    private String code;
}
