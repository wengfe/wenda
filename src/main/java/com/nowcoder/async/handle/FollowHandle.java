package com.nowcoder.async.handle;

import com.nowcoder.async.EventHandle;
import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventType;
import com.nowcoder.model.EntityType;
import com.nowcoder.model.Message;
import com.nowcoder.model.User;
import com.nowcoder.service.MessageService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class FollowHandle implements EventHandle {

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;


    @Override
    public void doHandle(EventModel model) {
        Message message = new Message();
        message.setFromId(WendaUtil.SYSTEM_USERID);
//        message.setToId(model.getEnetityOwnerId());
//        发通知给自己
        message.setToId(model.getActorId());
        message.setCreatedDate(new Date());
        User user = userService.getUser(model.getActorId());

        if (model.getEntityType() == EntityType.ENTITY_QUESTION){
            message.setContent(" 用户 " + user.getName()
                    + "关注了你的评论, http://127.0.0.1:8080/question/" + model.getEntityId());
        }else if (model.getEntityType() == EntityType.ENTITY_USER){
            message.setContent(" 用户 " + user.getName()
                    + "关注了你, http://127.0.0.1:8080/user/" + model.getActorId());
        }

        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.FOLLOW);
    }
}
