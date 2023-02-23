package com.exam.qa_robot.service.impl;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.exam.qa_robot.entity.Validation;
import com.exam.qa_robot.mapper.ValidationMapper;
import com.exam.qa_robot.service.IValidationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author vaifer
 * @since 2022-07-01
 */
@Service
public class ValidationServiceImpl extends ServiceImpl<ValidationMapper, Validation> implements IValidationService {

    @Transactional//事务处理
    @Override
    public void saveCode(String email, String code, Integer type, DateTime expireDate) {
        Validation validation = new Validation();
        validation.setEmail(email);
        validation.setCode(code);
        validation.setTime(expireDate);
        validation.setType(type);
        //删除同类型的验证
        UpdateWrapper<Validation> wrapper = new UpdateWrapper<>();
        wrapper.eq("email",email);
        wrapper.eq("type",type);
        remove(wrapper);
        //再加入新的验证信息实例
        save(validation);
    }
}
