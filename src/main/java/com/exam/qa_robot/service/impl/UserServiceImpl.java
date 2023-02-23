package com.exam.qa_robot.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.exam.qa_robot.common.Constants;
import com.exam.qa_robot.common.Validation_Enum;
import com.exam.qa_robot.controller.dto.UserDTO;
import com.exam.qa_robot.controller.dto.UserPasswdDTO;
import com.exam.qa_robot.entity.User;
import com.exam.qa_robot.entity.Validation;
import com.exam.qa_robot.exception.ServiceException;
import com.exam.qa_robot.mapper.UserMapper;
import com.exam.qa_robot.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.qa_robot.service.IValidationService;
import com.exam.qa_robot.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author vaifer
 * @since 2022-06-25
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Value("${spring.mail.username}")
    private String username;
    @Resource
    JavaMailSender javaMailSender;
    @Resource
    private UserMapper userMapper;
    @Resource
    private IValidationService validationService;

    private static final Log LOG = Log.get();
    @Override
    public UserDTO login(UserDTO userDTO) {
        User one = getUserInfo(userDTO);

        if (one != null){
            BeanUtil.copyProperties(one,userDTO,true);//忽略大小写
            //设置token
            String token = TokenUtils.genToken(one.getUid().toString(), one.getPasswd());
            userDTO.setToken(token);
            return userDTO;
        }else {
            throw new ServiceException(Constants.CODE_600,"用户名或密码错误");
        }
    }

    @Override
    public User register(User user) {
        User one = getUserInfo(user);
        if (one == null) {
            one = new User();
            BeanUtil.copyProperties(user,one,true);//忽略大小写
            save(one);//把copy完之后的用户对象存储到数据库
        }else {
            throw new ServiceException(Constants.CODE_600,"用户已存在");
        }
        return one;
    }

    @Override
    public void updatePasswd(UserPasswdDTO userPasswdDTO) {
        int update = userMapper.updatePasswd(userPasswdDTO);
        if (update < 1) {
            throw new ServiceException(Constants.CODE_600,"密码错误");
        }

    }

    /**
     * 邮箱验证
     * @param userDTO
     * @return
     */
    @Override
    public UserDTO emailCheck(UserDTO userDTO) {
        String email = userDTO.getEmail();
        String code = userDTO.getCode();
        //先做邮箱验证码验证
        QueryWrapper<Validation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email",email);
        queryWrapper.eq("code",code);
        queryWrapper.ge("time",new Date());
        Validation one = validationService.getOne(queryWrapper);
        if (one == null) {
            throw new ServiceException("-1","验证码不正确或已过期");
        }
        //验证通过后查询用户信息，登录
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("email",email);
        User user = getOne(userQueryWrapper);

        BeanUtil.copyProperties(user,userDTO,true);//忽略大小写
        //设置token
        String token = TokenUtils.genToken(user.getUid().toString(), user.getPasswd());
        userDTO.setToken(token);
        return userDTO;
    }

    @Override
    public void sendEmailCode(String email,Integer type) {

        Date now = new Date();
        //先查询同类型的验证码
        QueryWrapper<Validation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email",email);
        queryWrapper.eq("type",type);
        queryWrapper.ge("time",now);
        Validation validation = validationService.getOne(queryWrapper);
        if (validation != null) {
            throw new ServiceException("-1","当前您的验证码仍然有效，请不要重复发送");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        String text="";
        if ((Validation_Enum.LOGIN.getCode()).equals(type)) {//邮箱登录
            text="您正在进行登录操作，本次的登录验证码为：";
        }else if ((Validation_Enum.FORGET_PASS.getCode()).equals(type)){
            text="您正在进行密码找回操作，本次的验证码为：";
        }else if (Validation_Enum.CHANGE_PASS.getCode().equals(type)){
            text="您正在进行密码变更操作，本次的验证码为：";
        }else {
            throw new ServiceException("-1","未定义的操作");
        }
        message.setText("test");//邮件内容
        message.setSubject("【问答机器人】邮箱验证码");//邮件主题
        String code = RandomUtil.randomNumbers(4);//随机产生四位验证码
        message.setText(text+code+"   ，有效期5分钟，请勿向任何人泄露此验证码！");
        message.setSentDate(now);
        message.setFrom(username);//发件人
        message.setTo(email);//收件人
//        message.setCc("");//抄送人
//        message.setBcc("");//密送人
        javaMailSender.send(message);
        //发送成功之后把验证码存入数据库
        validationService.saveCode(email,code, type, DateUtil.offsetMinute(now,5));


    }

    private User getUserInfo(UserDTO userDTO){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email",userDTO.getEmail());
        queryWrapper.eq("passwd",userDTO.getPasswd());
        User one;
        try {
            one = getOne(queryWrapper);//从数据库查询用户信息
        } catch (Exception e) {
            LOG.error(e);
            throw new ServiceException(Constants.CODE_500,"系统错误：查询到多个重名用户");
        }
        return one;
    }
    private User getUserInfo(User userDTO){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email",userDTO.getEmail());
        queryWrapper.eq("passwd",userDTO.getPasswd());
        User one;
        try {
            one = getOne(queryWrapper);//从数据库查询用户信息
        } catch (Exception e) {
            LOG.error(e);
            throw new ServiceException(Constants.CODE_500,"系统错误：查询到多个重名用户");
        }
        return one;
    }

}
