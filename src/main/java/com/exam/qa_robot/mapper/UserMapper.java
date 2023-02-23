package com.exam.qa_robot.mapper;

import com.exam.qa_robot.controller.dto.UserPasswdDTO;
import com.exam.qa_robot.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author vaifer
 * @since 2022-06-25
 */
public interface UserMapper extends BaseMapper<User> {
    @Update("update _user set passwd = #{newpasswd} where uid = #{uid} and passwd = #{passwd}")
    int updatePasswd(UserPasswdDTO userPasswdDTO);
}
