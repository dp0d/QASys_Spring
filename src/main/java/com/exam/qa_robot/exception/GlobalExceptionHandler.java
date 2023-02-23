package com.exam.qa_robot.exception;

import com.exam.qa_robot.common.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 如果抛出ServiceException，则调用本方法
     * @param se
     * @return
     */
    @ExceptionHandler(ServiceException.class)
    @ResponseBody
  public Result handle(ServiceException se){
      return Result.error(se.getCode(), se.getMessage());
  }
}
