package com.nowcoder.controller;

import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventProduce;
import com.nowcoder.async.EventType;
import com.nowcoder.model.Comment;
import com.nowcoder.model.EntityType;
import com.nowcoder.model.HostHolder;
import com.nowcoder.service.CommentService;
import com.nowcoder.service.LikeService;
import com.nowcoder.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LikeController {
    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    EventProduce eventProduce;

    @Autowired
    CommentService commentService;

    @RequestMapping(value = {"/like"},method = {RequestMethod.POST})
    @ResponseBody
    public String like(@RequestParam("commentId") int commentId){
        if (hostHolder.getUser() == null){
            return WendaUtil.getJSONString(999);
        }


        Comment comment = commentService.getCommentById(commentId);
        int likeStatus = likeService.getLikeStatus(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, commentId);
        long likeCount;
//        双击赞同 = 取消
        if (likeStatus <= 0){
//        链式调用传参, 异步通知用户点赞信息
            eventProduce.fireEvent(new EventModel(EventType.LIKE)
                    .setActorId(hostHolder.getUser().getId()).setEntityId(commentId)
                    .setEntityType(EntityType.ENTITY_COMMENT).setEnetityOwnerId(comment.getUserId())
                    .setExt("questionId", String.valueOf(comment.getEntityId())));

            likeCount = likeService.like(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, commentId);
        }else {
            likeCount = likeService.disLike(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, commentId);
        }
        return WendaUtil.getJSONString(0, String.valueOf(likeCount));
    }

    @RequestMapping(value = {"/dislike"},method = {RequestMethod.POST})
    @ResponseBody
    public String dislike(@RequestParam("commentId") int commentId){
        if (hostHolder.getUser() == null){
            return WendaUtil.getJSONString(999);
        }

        long likeCount = likeService.disLike(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, commentId);
        return WendaUtil.getJSONString(0, String.valueOf(likeCount));
    }
}
