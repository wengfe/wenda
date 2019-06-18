package com.nowcoder.service;

import com.nowcoder.dao.QuestionDAO;
import com.nowcoder.model.Question;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class QuestionService {
    private static final Logger logger = LoggerFactory.getLogger(QuestionService.class);

    @Autowired
    QuestionDAO questionDAO;

    @Autowired
    SensitiveService sensitiveService;

    public int addQuestion(Question question) {
//        html 过滤
        question.setContent((HtmlUtils.htmlEscape(question.getContent())));
        question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));
//        敏感词过滤
        question.setContent(sensitiveService.filter(question.getContent()));
        question.setTitle(sensitiveService.filter(question.getTitle()));

        return questionDAO.addQuestion(question) > 0 ? question.getId() : 0;
    }

    public Question selectById(int qid){
        Question question = questionDAO.selectById(qid);
        return question;
    }

    public List<Question> getLatestQuestions(int id, int offset, int limit) {
        return questionDAO.selectLatestQuestions(id, offset, limit);
    }
}
