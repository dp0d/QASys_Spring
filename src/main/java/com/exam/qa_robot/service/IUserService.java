package com.exam.qa_robot.service;

import com.exam.qa_robot.controller.dto.UserDTO;
import com.exam.qa_robot.controller.dto.UserPasswdDTO;
import com.exam.qa_robot.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author vaifer
 * @since 2022-06-25
 */
public interface IUserService extends IService<User> {

    UserDTO login(UserDTO userDTO);

    User register(User user);

    void updatePasswd(UserPasswdDTO userPasswdDTO);

    UserDTO emailCheck(UserDTO userDTO);

    void sendEmailCode(String email,Integer type);
}
