package com.exam.qa_robot.service;

import cn.hutool.core.date.DateTime;
import com.exam.qa_robot.entity.Validation;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author vaifer
 * @since 2022-07-01
 */
public interface IValidationService extends IService<Validation> {

    void saveCode(String email, String code, Integer type, DateTime expireDate);
}
