package com.exam.qa_robot.service.impl;

import com.exam.qa_robot.entity.Book;
import com.exam.qa_robot.mapper.BookMapper;
import com.exam.qa_robot.service.IBookService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author vaifer
 * @since 2022-07-11
 */
@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements IBookService {

}
