package com.exam.qa_robot.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 接口统一返回包装类
 */
@NoArgsConstructor
@AllArgsConstructor
@Data//添加get set方法
public class Result {
    //标识成功/失败
    private  String code;
    //请求失败的原因
    private String msg;
    //携带的数据
    private Object data;

    public static Result success(){
        return new Result(Constants.CODE_200,"",null);
    }

    public static Result success(Object data){
        return new Result(Constants.CODE_200,"",data);
    }
    public static Result success(String code,Object data,String msg){
        return new Result(code,msg,data);
    }
    public static Result error(String code,String msg){
        return new Result(code,msg,null);
    }
    public static Result error(){
        return new Result(Constants.CODE_500,"System Error",null);
    }

}
