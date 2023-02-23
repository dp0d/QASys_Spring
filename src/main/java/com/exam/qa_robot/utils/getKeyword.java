package com.exam.qa_robot.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.exam.qa_robot.entity.Keywords;
import com.exam.qa_robot.entity.Question;
import com.exam.qa_robot.service.IKeywordsService;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.springframework.stereotype.Component;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import javax.annotation.Resource;
import java.io.*;
import java.util.List;

@Component
public class getKeyword {

    @Resource
    private IKeywordsService keywordsService;

    public void in() {
        String str = "如何做美男子";
        Result result = ToAnalysis.parse(str); //封装的分词结果对象，包含一个terms列表
//		 System.out.println(result);
        List<Term> terms = result.getTerms(); //term列表，元素就是拆分出来的词以及词性
//		 System.out.println(terms);
        for (Term term : terms) {
            System.out.println(term.getName());        //分词的内容
            System.out.println(term.getNatureStr());    //分词的词性
            //值得注意的是当分词词性不在分词表范围内时，返回结果是一个null字符串而非null
            System.out.println(term.getOffe());        //分词在原文本中的起始位置
        }


    }

    /**
     * 对问题做分词并存入数据库计算词频
     *
     * @param question
     * @return
     */
    public void cutWords(Question question) {

        Result result = ToAnalysis.parse(question.getText()); //封装的分词结果对象，包含一个terms列表
        List<Term> terms = result.getTerms(); //term列表，元素就是拆分出来的词以及词性
        String qid = question.getQid().toString();

        for (Term term : terms) {
            if (term.getNatureStr().contains("n")) {//提取名词
                String word = term.getName();

                QueryWrapper<Keywords> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("word", word);
                Keywords one = keywordsService.getOne(queryWrapper);


                if (one == null) {
                    keywordsService.save(new Keywords(null, word, 1,null,","+qid+","));
                } else if (!one.getQid_list().contains(","+qid+",")){
                    Integer frequency = one.getFrequency() + 1;
                    one.setFrequency(frequency);
                    one.setQid_list(one.getQid_list()+qid+",");
                    keywordsService.saveOrUpdate(one);
                }
//                keywords+=term.getName();
//                keywords+="|";

            }
            //值得注意的是当分词词性不在分词表范围内时，返回结果是一个null字符串而非null
//            System.out.println(term.getOffe());		//分词在原文本中的起始位置
        }
    }

    public void bb() {
        StringReader sr = new StringReader("怎么吃泥鳅？ ");
        IKSegmenter ik = new IKSegmenter(sr, true);
        Lexeme lex = null;
        while (true) {
            try {
                if (!((lex = ik.next()) != null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.print(lex.getLexemeText() + " ");
        }
    }

    public static void main(String[] args) throws IOException {

//        System.out.println(cut);
    }
}