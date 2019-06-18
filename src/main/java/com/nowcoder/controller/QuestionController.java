package com.nowcoder.controller;

import com.nowcoder.model.HostHolder;
import com.nowcoder.model.Question;
import com.nowcoder.model.User;
import com.nowcoder.service.QuestionService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Controller
public class QuestionController {
    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);

    @Autowired
    QuestionService questionService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService userService;

    @RequestMapping(value = "/question/add", method = {RequestMethod.POST})
    @ResponseBody
    public String addQuestion(@RequestParam("title") String title, @RequestParam("content") String content){
        try{
            Question question = new Question();
            question.setTitle(title);
            question.setContent(content);
            question.setCommentCount(0);
            question.setCreatedDate(new Date());
            if (hostHolder.getUser() == null){
//                如果当前用户未登录，使用匿名用户 id 发起提问
//                question.setUserId(WendaUtil.ANONYMOUS_USERID);
                return WendaUtil.getJSONString(999);
            }else {
                question.setUserId(hostHolder.getUser().getId());
            }
            if (questionService.addQuestion(question) > 0){
                return  WendaUtil.getJSONString(0);
            }
        }catch (Exception e){
            logger.error("提问失败", e.getMessage());
        }
        return WendaUtil.getJSONString(1,"提问失败");
    }

    @RequestMapping(value = "/question/{qid}", method = RequestMethod.GET)
    public String QuestionDetail(Model model, @PathVariable("qid") int qid){
        Question question = questionService.selectById(qid);
        User user = userService.getUser(question.getUserId());
        model.addAttribute("question",question);
        model.addAttribute("user", user);
        return "detail";
    }
}
