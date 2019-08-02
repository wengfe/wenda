package com.nowcoder.controller;

import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventProduce;
import com.nowcoder.async.EventType;
import com.nowcoder.model.*;
import com.nowcoder.service.CommentService;
import com.nowcoder.service.FollowService;
import com.nowcoder.service.QuestionService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class FollowController {
    private static final Logger logger = LoggerFactory.getLogger(FollowController.class);

    @Autowired
    CommentService commentService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService userService;

    @Autowired
    FollowService followService;

    @Autowired
    EventProduce eventProduce;

    @Autowired
    QuestionService questionService;

    @RequestMapping(value = {"/followUser"},method = {RequestMethod.POST})
    @ResponseBody
    public String follow(@RequestParam("userId") int userId){
        if (hostHolder.getUser() == null){
            return WendaUtil.getJSONString(999);
        }

        boolean ret = followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId);

//        发送异步消息通知队列
        eventProduce.fireEvent(new EventModel(EventType.FOLLOW)
                        .setActorId(hostHolder.getUser().getId())
                        .setEnetityOwnerId(userId).setEntityType(EntityType.ENTITY_USER).setEntityId(userId));

//        返回关注人数
        return WendaUtil.getJSONString(ret ? 0 : 1, String.valueOf(followService.getFolloweesCount(hostHolder.getUser().getId(),userId)));
    }


    @RequestMapping(value = {"/unfollowUser"},method = {RequestMethod.POST})
    @ResponseBody
    public String unfollow(@RequestParam("userId") int userId){
        if (hostHolder.getUser() == null){
            return WendaUtil.getJSONString(999);
        }

        boolean ret = followService.unfollow(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId);

//        发送异步消息通知队列
        eventProduce.fireEvent(new EventModel(EventType.UNFOLLOW)
                .setActorId(hostHolder.getUser().getId())
                .setEnetityOwnerId(userId).setEntityType(EntityType.ENTITY_USER).setEntityId(userId));

//        返回关注人数
        return WendaUtil.getJSONString(ret ? 0 : 1, String.valueOf(followService.getFolloweesCount(hostHolder.getUser().getId(),userId)));
    }


    @RequestMapping(value = {"/followQuestion"},method = {RequestMethod.POST})
    @ResponseBody
    public String followQuestion(@RequestParam("questionId") int questionId){
        if (hostHolder.getUser() == null){
            return WendaUtil.getJSONString(999);
        }

        Question q = questionService.selectById(questionId);
        if (q == null){
            return WendaUtil.getJSONString(1, "问题不存在");
        }

        boolean ret = followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, questionId);

//        发送异步消息通知队列
        eventProduce.fireEvent(new EventModel(EventType.FOLLOW)
                .setActorId(hostHolder.getUser().getId())
                .setEnetityOwnerId(q.getUserId()).setEntityType(EntityType.ENTITY_QUESTION).setEntityId(questionId));

//        返回关注者信息及问题被关注人数
        Map<String, Object> info = new HashMap<String, Object>();
        info.put("headUrl", hostHolder.getUser().getHeadUrl());
        info.put("name", hostHolder.getUser().getName());
        info.put("id", hostHolder.getUser().getId());
        info.put("count", followService.getFollowersCount(EntityType.ENTITY_QUESTION, questionId));

//        返回关注人数
        return WendaUtil.getJSONString(ret ? 0 : 1, info);
    }


    @RequestMapping(value = {"/unfollowQuestion"},method = {RequestMethod.POST})
    @ResponseBody
    public String unfollowQuestion(@RequestParam("questionId") int questionId){
        if (hostHolder.getUser() == null){
            return WendaUtil.getJSONString(999);
        }

        Question q = questionService.selectById(questionId);
        if (q == null){
            return WendaUtil.getJSONString(1, "问题不存在");
        }

        boolean ret = followService.unfollow(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, questionId);

//        发送异步消息通知队列
        eventProduce.fireEvent(new EventModel(EventType.UNFOLLOW)
                .setActorId(hostHolder.getUser().getId())
                .setEnetityOwnerId(q.getUserId()).setEntityType(EntityType.ENTITY_QUESTION).setEntityId(questionId));

//        返回关注者信息及问题被关注人数
        Map<String, Object> info = new HashMap<String, Object>();
        info.put("headUrl", hostHolder.getUser().getHeadUrl());
        info.put("name", hostHolder.getUser().getName());
        info.put("id", hostHolder.getUser().getId());
        info.put("count", followService.getFollowersCount(EntityType.ENTITY_QUESTION, questionId));

//        返回关注人数
        return WendaUtil.getJSONString(ret ? 0 : 1, info);
    }


//    关注列表
    @RequestMapping(path = {"/user/{uid}/followees"},method = {RequestMethod.GET})
    public String followees(Model model, @PathVariable("uid") int userId){
        List<Integer> followeeIds = followService.getFollowees(userId, EntityType.ENTITY_USER, 0, 10);
        if (hostHolder.getUser() != null){
            model.addAttribute("followees", getUsersInfo(hostHolder.getUser().getId(), followeeIds));
        }else{
            model.addAttribute("followees", getUsersInfo(0, followeeIds));
        }
        model.addAttribute("followeeCount", followService.getFolloweesCount(userId, EntityType.ENTITY_USER));
        model.addAttribute("curUser", userService.getUser(userId));
        return "followees";
    }

//    粉丝列表
    @RequestMapping(path = {"/user/{uid}/followers"},method = {RequestMethod.GET})
    public String followers(Model model, @PathVariable("uid") int userId){
        List<Integer> followerIds = followService.getFollowees(userId, EntityType.ENTITY_USER, 0, 10);
        if (hostHolder.getUser() != null){
            model.addAttribute("followers", getUsersInfo(hostHolder.getUser().getId(), followerIds));
        }else{
            model.addAttribute("followers", getUsersInfo(0, followerIds));
        }

        model.addAttribute("followerCount", followService.getFollowersCount(EntityType.ENTITY_USER, userId));
        model.addAttribute("curUser", userService.getUser(userId));
        return "followers";
    }

//    取出关注者列表
    private List<ViewObject> getUsersInfo(int localUserId, List<Integer> userIds){
        List<ViewObject> userInfos = new ArrayList<>();
        for (Integer uid: userIds){
            User user = userService.getUser(uid);
            if (user == null){
                continue;
            }

            ViewObject vo = new ViewObject();
            vo.set("user", user);
            vo.set("followerCount", followService.getFollowersCount(EntityType.ENTITY_USER, uid));
            vo.set("followeeCount", followService.getFolloweesCount(EntityType.ENTITY_USER, uid));
            if (localUserId != 0){
                vo.set("followed", followService.isFollower(localUserId, EntityType.ENTITY_USER, uid));
            }else {
                vo.set("followed", false);
            }

            userInfos.add(vo);
        }
        return userInfos;
    }



}
