package com.exam.qa_robot.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.qa_robot.common.Constants;
import com.exam.qa_robot.common.Result;
import com.exam.qa_robot.controller.dto.UserDTO;
import com.exam.qa_robot.controller.dto.UserPasswdDTO;
import com.exam.qa_robot.entity.Book;
import com.exam.qa_robot.entity.Validation;
import com.exam.qa_robot.exception.ServiceException;
import com.exam.qa_robot.service.IBookService;
import com.exam.qa_robot.service.IValidationService;
import com.exam.qa_robot.utils.TokenUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;


import com.exam.qa_robot.service.IUserService;
import com.exam.qa_robot.entity.User;


import org.springframework.web.bind.annotation.RestController;

import static com.exam.qa_robot.utils.EncryptUtil.encrypt;


/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author vaifer
 * @since 2022-06-25
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private IValidationService validationService;

    @Resource
    private IBookService bookService;

    @Resource
    private IUserService userService;
    @Resource
    private com.exam.qa_robot.utils.RestTemplateUtils RestTemplateController;

    @PostMapping("/test")
    public Result test() {
//        RestTemplateController.getLabel("http://10.27.244.105:9090/corpus/download/630d3016684e457dbbf622e82256fefd.txt");

//        Map<String, String> request = new HashMap<>();
//        if (ab.equals("1")) {
//            request.put("query1", "111");
//            request.put("query2", "222");
//            RestTemplateController.RestTemplateTestGet(request);
//        } else {
//
//            String a="从中医的角度讲，泥鳅有很高的药用价值，能补中益气、养肾生精。非常适宜身体虚弱、脾胃虚寒、体虚盗汗的人食用，对某些急性肝炎的治疗也十分有益。而从现代营养学的角度分析，泥鳅是鱼类里的补钙冠军，同等重量下，泥鳅的钙含量是鲤鱼的近6倍，是带鱼的10倍左右。泥鳅同时富含有利于钙吸收的维生素，因此是很好的补钙食物。此外，泥鳅还富含亚精胺和核苷，能增加皮肤弹性和湿润度，并提高身体的抗病毒能力。建议采用清蒸或炖煮的方式烹调泥鳅，这样能够较好地保存其营养价值，如果能搭配豆腐一起吃，补钙效果会更好。";
//            //W2V
//            RestTemplateController.QAPost("preader", "怎么吃泥鳅",a);
//        }

        return Result.success();
    }

//    @GetMapping("/test2")
//    public String  testServer(@RequestBody String userDTO){
//        System.out.println("模拟其他服务器接收数据"+userDTO);
//
//        return "succ2";
//    }

    /**
     * 新增或更新
     *
     * @param user
     * @return
     */
    @PostMapping
    public Result save(@RequestBody User user) {
        if (StrUtil.isBlank(user.getEmail()) || StrUtil.isBlank(user.getPasswd())) {
            throw new ServiceException(Constants.CODE_400, "参数错误");
        }
        //密码加密
        String encrypt = encrypt(user.getPasswd());
        user.setPasswd(encrypt);
        System.out.println(encrypt);
        return Result.success(userService.saveOrUpdate(user));
    }

    /**
     * 用户登录
     *
     * @param userDTO
     * @return
     * @RequestBody把前端传来的json转为可以使用的java对象
     */

    @PostMapping("/login")
    public Result login(@RequestBody UserDTO userDTO) {
        if (StrUtil.isBlank(userDTO.getEmail()) || StrUtil.isBlank(userDTO.getPasswd())) {
            return Result.error(Constants.CODE_400, "参数错误");
        }
        //密码加密
        String encrypt = encrypt(userDTO.getPasswd());
        userDTO.setPasswd(encrypt);
        UserDTO dto = userService.login(userDTO);

        QueryWrapper<Book> wrapper =new QueryWrapper();
        List<Book> bookList = bookService.list(wrapper);
        dto.setBookList(bookList);
        return Result.success(dto);
    }

    @PostMapping("/emailLogin")
    public Result email(@RequestBody UserDTO userDTO) {
        String email = userDTO.getEmail();
        String code = userDTO.getCode();
        if (StrUtil.isBlank(email) || StrUtil.isBlank(code)) {
            return Result.error(Constants.CODE_400, "参数错误");
        }
        UserDTO dto = userService.emailCheck(userDTO);
        return Result.success(dto);
    }

    //    @AuthAccess
    @GetMapping("/send/{email}/{type}")
    public Result sendEmailCode(@PathVariable String email,
                                @PathVariable Integer type) {
        if (StrUtil.isBlank(email) || type == null) {
            throw new ServiceException(Constants.CODE_400, "参数错误");
        }
        QueryWrapper<User> wrapper =new QueryWrapper();
        wrapper.eq("email",email);
        User one = userService.getOne(wrapper);
        if (one ==null){
           return Result.error(Constants.CODE_401,"用户未注册");
        }
        userService.sendEmailCode(email, type);
        return Result.success();
    }

    @PostMapping("/register")
    public Result register(@RequestBody User user) {

        String email = user.getEmail();
        String passwd = user.getPasswd();
        String username = user.getUsername();
        if (StrUtil.isBlank(email) || StrUtil.isBlank(passwd) || StrUtil.isBlank(username)) {
            return Result.error(Constants.CODE_400, "参数错误");
        }
        //密码加密
        String encrypt = encrypt(passwd);
        user.setPasswd(encrypt);
        return Result.success(userService.register(user));
    }

    /**
     * 找回用户密码
     *
     * @param userPasswdDTO
     * @return
     */
    @PostMapping("/resetpw")
    public Result resetpw(@RequestBody UserPasswdDTO userPasswdDTO) {
        if (StrUtil.isBlank(userPasswdDTO.getNewpasswd())||StrUtil.isBlank(userPasswdDTO.getEmail()) || StrUtil.isBlank(userPasswdDTO.getCode())) {
            throw new ServiceException("-1", "参数异常");
        }
        User user = getUserByCode(userPasswdDTO, 2);
        //重置密码
        //密码加密
        String encrypt = encrypt(userPasswdDTO.getNewpasswd());
        userPasswdDTO.setNewpasswd(encrypt);
        userPasswdDTO.setPasswd(user.getPasswd());
        userPasswdDTO.setUid(user.getUid());
        userService.updatePasswd(userPasswdDTO);

        return Result.success();
    }

    /**
     * 密码变更
     *
     * @param userPasswdDTO
     * @return
     */
    @PostMapping("/changepw")
    public Result changepw(@RequestBody UserPasswdDTO userPasswdDTO) {
        if (userPasswdDTO.getUid()<1||StrUtil.isBlank(userPasswdDTO.getNewpasswd())|| StrUtil.isBlank(userPasswdDTO.getPasswd())) {
            throw new ServiceException("-1", "参数异常");
        }

//        User user = getUserByCode(userPasswdDTO, 3);
//        Integer  uid = getUserbyEmail(userPasswdDTO.getEmail()).getUid();
        //重置密码
//        userPasswdDTO.setUid(uid);
        //密码加密
        String npwEncrypt = encrypt(userPasswdDTO.getNewpasswd());
        String pwEncrypt = encrypt(userPasswdDTO.getPasswd());
        userPasswdDTO.setPasswd(pwEncrypt);
        userPasswdDTO.setNewpasswd(npwEncrypt);
        userService.updatePasswd(userPasswdDTO);

        return Result.success();
    }

    /**
     * 验证邮箱验证码，并获取相应的用户信息
     *
     * @param userPasswdDTO
     * @param type
     * @return
     */
    private User getUserByCode(UserPasswdDTO userPasswdDTO, int type) {
        //先查询邮箱验证表，做验证
        QueryWrapper<Validation> validationQueryWrapper = new QueryWrapper<>();
        validationQueryWrapper.ge("time", new Date());
        validationQueryWrapper.eq("email", userPasswdDTO.getEmail());
        validationQueryWrapper.eq("code", userPasswdDTO.getCode());
        validationQueryWrapper.eq("type", type);

        Validation one = validationService.getOne(validationQueryWrapper);
        if (one == null) {
            throw new ServiceException("-1", "验证码不正确或已过期");
        }
        //验证通过了，查询用户信息
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("email", userPasswdDTO.getEmail());
        User user = userService.getOne(userQueryWrapper);
        return user;
    }


    /**
     * 根据id删除用户
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        return Result.success(userService.removeById(id));
    }


    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        return Result.success(userService.removeBatchByIds(ids));
    }

    /**
     * 查询全部用户信息
     *
     * @return
     */
    @GetMapping
    public Result findAll() {
        return Result.success(userService.list());
    }

    /**
     * 根据用户类型查询该类型全部用户
     * @param type
     * @return
     */
    @GetMapping("/{type}")
    public Result findAllbyType(@PathVariable Integer type) {

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (type==5){
            queryWrapper.eq("type",2|3);
        }
        else {
            queryWrapper.eq("type",type);
        }
        return Result.success(userService.list());
    }

    /**
     * 根据id查询某个用户信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        User user = userService.getById(id);
        user.setPasswd(null);
        return Result.success(userService.getById(id));
    }


    /**
     * 根据用户邮箱查询用户个人信息
     *
     * @param email
     * @return
     */
    @GetMapping("/userinfo/{email}")
    public Result findOneByEmail(@PathVariable String email) {
        User user = getUserbyEmail(email);
        user.setPasswd(null);
        return Result.success(user);
    }

    public User getUserbyEmail(String email){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        User user = userService.getOne(queryWrapper);

        return user;
    }


    /**
     * 用户分页查询
     *
     * @param pageNum
     * @param pageSize
     * @param username
     * @param email
     * @return
     */
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer type,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String username,
                           @RequestParam(defaultValue = "") String email) {


        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("uid");
        //按类型获取
        if (type==5){
            queryWrapper.ge("type",2);
        }else if (-1!=type){
            queryWrapper.eq("type",type);
        }

        if (!"".equals(username)) {
            queryWrapper.like("username", username);
        }

        if (!"".equals(email)) {
            queryWrapper.like("email", email);
        }
        //获取当前用户信息
        User currentUser = TokenUtils.getCurrentUser();

//        queryWrapper.like("email",email);//支持多个like条件，使用or： queryWrapper.or().like()
        return Result.success(userService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }


}

