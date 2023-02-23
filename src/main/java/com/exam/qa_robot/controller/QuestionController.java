package com.exam.qa_robot.controller;


import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.qa_robot.common.Constants;
import com.exam.qa_robot.common.Result;
import com.exam.qa_robot.controller.dto.AnswerDTO;
import com.exam.qa_robot.entity.*;
import com.exam.qa_robot.exception.ServiceException;
import com.exam.qa_robot.service.IBookService;
import com.exam.qa_robot.service.IKnowledgeGraphService;
import com.exam.qa_robot.service.IUserService;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.exam.qa_robot.utils.getKeyword;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;


import com.exam.qa_robot.service.IQuestionService;

import org.springframework.web.bind.annotation.RestController;

import  com.exam.qa_robot.utils.LuceneUtils;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author vaifer
 * @since 2022-06-25
 */
@RestController
@RequestMapping("/question")
public class QuestionController {

    @Resource
    private KeywordsController keywordsController;
    @Resource
    private com.exam.qa_robot.utils.RestTemplateUtils restTemplateUtils;
    @Resource
    private com.exam.qa_robot.utils.getKeyword getKeyword;

    @Resource
    private IKnowledgeGraphService knowledgeGraphService;

    @Resource
    private LuceneUtils luceneUtils;
    @Resource
    private IQuestionService questionService;
    @Resource
    private IUserService userService;
    @Resource
    private IBookService bookService;

    public String  getbook(String book){
        QueryWrapper<Book> bookQueryWrapper =new QueryWrapper();
        bookQueryWrapper.eq("name",book);
        Book one = bookService.getOne(bookQueryWrapper);
        return one.getBookName();
    }

    @PostMapping("/test")
    public Result test() {

//调用QA提供的NER接口：根据问题提取实体
        String answer = restTemplateUtils.QAPost("NER", "泥鳅能和鸡蛋一起吃吗", null);
        AnswerDTO answerDTO = null;
        answerDTO = JSON.parseObject(answer, AnswerDTO.class);
        String entity = answerDTO.getEntity();
        List<KnowledgeGraph> list = knowledgeGraphService.getKg(entity);
        answerDTO.setList(list);

        return Result.success(answerDTO);
    }

    @PostMapping("/gen")
    public Result getQg(@RequestBody Question question) {
        if (StrUtil.isBlank(question.getText()) || null == question.getUid()) {
            return Result.error(Constants.CODE_400, "参数错误");
        }
        List<HashMap<String,String>> answer = null;
        try {
            //QG接口调用
            answer = restTemplateUtils.QGPost(question.getText());
            //将json数据转换为java对象/实体类
        } catch (Exception e) {
        }
        return Result.success(answer);
    }

    /**
     * 哈希检索
     * @param question
     * @return
     */
    @PostMapping("/hash")
    public Result getHash(@RequestBody Question question,@RequestParam String book) {

        if (StrUtil.isBlank(question.getText()) || null == question.getUid()) {
            return Result.error(Constants.CODE_400,"检索失败");
        }
        //保存并处理问题
        question.setType(2);
//        saveQUestion(question);
        //调用哈希
        System.out.println(book);
        Map<String,String> hashGet = restTemplateUtils.HashGet(question.getText(),getbook(book),"5");
        System.out.println(hashGet);
        return Result.success(hashGet);
    }

    /**
     * lucene
     * @param question
     * @return
     */
    @PostMapping("/lucene")
    public Result getLucene(@RequestBody Question question,@RequestParam String book) {
        book=getbook(book);
        if (StrUtil.isBlank(question.getText()) || null == question.getUid()) {
            return Result.error(Constants.CODE_400,"检索失败");
        }
        System.out.println("lucene=================");
        //保存并处理问题
        question.setType(3);
//        saveQUestion(question);

        System.out.println(book+"@"+question.getText());
        //lucene
        HashMap<String,String> luceneMap = new HashMap();
        try {
            luceneMap = luceneUtils.sendQuery(book+"@"+question.getText());
        } catch (IOException e) {
            luceneMap.put("0","服务未开启");
        }

        return Result.success(luceneMap);
    }

    /**
     * 保存问题，做分词保存
     * @param question
     */
    public void saveQUestion(Question question){
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("text", question.getText());
        queryWrapper.eq("uid", question.getUid());
        queryWrapper.eq("type", question.getType());

        Question one = questionService.getOne(queryWrapper);
        //问题语句及其分词保存
        if (one == null){
            questionService.saveOrUpdate(question);
            //获取问题id
            Question newOne = questionService.getOne(queryWrapper);
            question.setQid(newOne.getQid());
            getKeyword.cutWords(question);
        }else {
            question.setQid(one.getQid());
            question.setRaiseDt(new Date());
            questionService.saveOrUpdate(question);
        }
    }

    /**
     * QA提问
     * @param question
     * @return
     */
    @PostMapping
    public Result saveAndGetQA(@RequestBody Question question,@RequestParam String book) {
        book=getbook(book);
        if (StrUtil.isBlank(question.getText()) || null == question.getUid()) {
            return Result.error(Constants.CODE_400, "参数错误");
        }
        QueryWrapper<Book> bookQueryWrapper =new QueryWrapper();
        bookQueryWrapper.eq("name",book);

        question.setType(1);

        //获取该用户最近的问题并计算与当前问题的相似度
        QueryWrapper<Question> wrapper =new QueryWrapper();
        wrapper.eq("uid", question.getUid());
        wrapper.orderByDesc("raise_dt");
        wrapper.ge("raise_dt",DateUtil.offsetMinute(new Date(),-5));
        wrapper.last("limit 1");
        Question pre = questionService.getOne(wrapper);
        String questionText=question.getText();
        System.out.println("ssim-----------------------------question.getText()"+question.getText());
        if (pre !=null){//如果能获取到上一个问题（五分钟内提问的）
            //计算相似度
            System.out.println(pre.getText()+question.getText());
            String ssim = null;
            try {
                ssim = restTemplateUtils.SSIM(pre.getText(), question.getText());
            } catch (Exception e) {
                ssim="0";
            }
            System.out.println("answerdedddddddddddddddddddddd"+ssim);
            //相似度足够则拼接问题
            if (Float.parseFloat(ssim)>0.6){
                questionText+= pre.getText();
            }
        }
        System.out.println("ssim-----------------------------question.getText()"+question.getText());
        //调用哈希
        Map<String, String> hashGet = null;
        String questionF="";
        int hash=1;
        try {
            hashGet = restTemplateUtils.HashGet(questionText, book, "1");
            System.out.println("哈希返回的结果" + hashGet);

            questionF=hashGet.get("0");
            System.out.println("使用hash--------------------------------------"+questionF);
        } catch (Exception e) {
            hash=0;
        }

        HashMap<String,String> luceneMap = new HashMap();
        if (hash==0){
            try {
                luceneMap = luceneUtils.sendQuery(book+"@"+question.getText());
                questionF=luceneMap.get("0");
                System.out.println("使用lucene--------------------------------------"+questionF);
            } catch (IOException e) {
                luceneMap.put("0","服务未开启");
            }

        }

        System.out.println(questionF+"=================================");
        //调用深度模型接口-----------------------------------------------------------------待补充
        AnswerDTO answerDTO = null;
        try {
            //QA接口调用
            String answer = restTemplateUtils.QAPost("preader", questionText, questionF);
            System.out.println(answer);
            System.out.println("QA答案:"+answer);
            //将json数据转换为java对象/实体类
            answerDTO = JSON.parseObject(answer, AnswerDTO.class);
        } catch (Exception e) {
            answerDTO=new AnswerDTO("QA系统问题，提问失败","","","",null);
        }

        //获取知识图谱
        //调用QA：根据问题提取实体
        String getEntity = restTemplateUtils.QAPost("NER", questionText, null);
        AnswerDTO answerDTO1 = JSON.parseObject(getEntity, AnswerDTO.class);
        String entity = answerDTO1.getEntity();
        //模糊查询知识图谱信息
        QueryWrapper<KnowledgeGraph> knowledgeGraphQueryWrapper = new QueryWrapper();
        knowledgeGraphQueryWrapper.like("k", entity);
        knowledgeGraphQueryWrapper.orderByDesc("id");
        List<KnowledgeGraph> list = knowledgeGraphService.getKg(entity);
        //添加知识图谱信息
        answerDTO.setList(list);
        answerDTO.setEntity(entity);
//        if (list.get(0)==null){System.out.println("zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz");}



//        //封装结果
//        Map<String, String> response = new HashMap<>();
//        response.put("answer", "答案");
//        response.put("context", "段落");

        //在数据库中新增问题并处理关键词
        saveQUestion(question);
        System.out.println(answerDTO.getAnswer());
        System.out.println();
        return Result.success(answerDTO);
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        return Result.success(questionService.removeById(id));
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        return Result.success(questionService.removeBatchByIds(ids));
    }

    /**
     * 获取热门问题
     * @return
     */
    @PostMapping("/major")
    public Result getMajorQueston() {
//        //获取到关键词
//        QueryWrapper<Keywords> queryWrapper = new QueryWrapper<>();
//        queryWrapper.orderByDesc("frequency");//安装词频从大到小排序
//        queryWrapper.last("limit 2");//获取前10个频率最高的名词
//        System.out.println("===================================");
//        return keywordsService.list(queryWrapper);
        List<Keywords> keywords = keywordsController.getmajorKeywords();
        Map questions=new HashMap<Integer,String>();
        Map record =new HashMap<String ,Integer>();
        for(int i =0;i<keywords.toArray().length;i++){
            Keywords keyword = keywords.get(i);
            //获取到qid
            Integer qid = getQuestion(keyword.getQid_list());
            String text = questionService.getById(qid).getText();
            if (record.get(text)!=null){continue;}
            questions.put(i+1,text);
            record.put(text,i);
        }

        return Result.success(questions);
    }
    public Integer getQuestion(String str){
        int len=str.length();
        str=str.substring(0,len-1);
        int start = str.lastIndexOf(",")+1;
        str=str.substring(start,len-1);
        return Integer.parseInt(str);
    }


    @GetMapping
    public Result findAll() {
        return Result.success(questionService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(questionService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam Integer uid) {

        //根据uid查询问题，根据uid判断用户权限，设置是否可以查询所有用户的问题·
        User user = userService.getById(uid);
        if (user == null) {
            throw new ServiceException(Constants.CODE_401, "用户账号不存在");
        }
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("raise_dt");
        if (user.getType() == 1 && user.getEnabled() == 1) {//用户账号启用且为普通用户
            queryWrapper.eq("uid", uid);
        } else if (user.getEnabled() != 1) {
            throw new ServiceException(Constants.CODE_401, "用户账号不可用");
        }

        return Result.success(questionService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }


}

