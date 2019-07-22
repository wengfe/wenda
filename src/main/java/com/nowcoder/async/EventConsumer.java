package com.nowcoder.async;

import com.alibaba.fastjson.JSON;
import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    private Map<EventType, List<EventHandle>> config = new HashMap<EventType, List<EventHandle>>();
    private ApplicationContext applicationContext;

    @Autowired
    JedisAdapter jedisAdapter;

    @Override
    public void afterPropertiesSet() throws Exception {
        //    找到所有 EventHandle 的实现类
        Map<String, EventHandle> beans = applicationContext.getBeansOfType(EventHandle.class);
        if (beans != null){
//            遍历获取到的 EventHandle, 并根据 handle 取值相应的 eventType
            for (Map.Entry<String, EventHandle> entry : beans.entrySet()){
                List<EventType> eventTypes = entry.getValue().getSupportEventTypes();

                for (EventType type : eventTypes){
                    if(!config.containsKey(type)){
                        config.put(type, new ArrayList<EventHandle>());
                    }
                    config.get(type).add(entry.getValue());
                }
            }
        }
//        使用线程来完成事件处理  可用线程池优化
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
//                循环查询 event，
                while (true){
                    String key = RedisKeyUtil.getBizEventQueueKey();
                    List<String> events = jedisAdapter.brpop(0,key);
                    for (String message : events){
                        logger.info("EventInfo message: " + message);
                        if (message.equals(key)){
                            continue;
                        }
//                        反序列化 type 类型
                        EventModel eventModel = JSON.parseObject(message, EventModel.class);
                        if (!config.containsKey(eventModel.getType())){
                            logger.error("不能识别的事件");
                            continue;
                        }
//                        将识别的事件类型交给相应的 doHandle
                        for (EventHandle handle : config.get(eventModel.getType())){
                            handle.doHandle(eventModel);
                        }
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
