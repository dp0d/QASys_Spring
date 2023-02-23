package com.exam.qa_robot.service.impl;

import com.exam.qa_robot.entity.Question;
import com.exam.qa_robot.mapper.QuestionMapper;
import com.exam.qa_robot.service.IQuestionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author vaifer
 * @since 2022-06-25
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements IQuestionService {

}
