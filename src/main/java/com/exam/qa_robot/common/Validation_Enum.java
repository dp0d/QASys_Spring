package com.exam.qa_robot.common;

public enum Validation_Enum {
    LOGIN(1),FORGET_PASS(2),CHANGE_PASS(3);
    private Integer code;

    public Integer getCode() {
        return code;
    }

    Validation_Enum(Integer code) {
        this.code = code;
    }
}
