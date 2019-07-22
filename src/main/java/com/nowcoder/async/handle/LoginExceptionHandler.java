package com.nowcoder.async.handle;

import com.nowcoder.async.EventHandle;
import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventType;
import com.nowcoder.util.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class LoginExceptionHandler implements EventHandle {
    @Autowired
    MailSender mailSender;
//    是否发送邮件标识
    boolean isSend = false;

    @Override
    public void doHandle(EventModel model) {
//        经过判断 发现登陆异常
        if (isSend){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("username", model.getExt("username"));
            mailSender.sendWithHTMLTemplate(model.getExt("email"), "登陆 IP 异常", "mail/login_exception.html", map);
        }
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LOGIN);
    }
}
