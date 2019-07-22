package com.nowcoder.async;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventProduce {
    @Autowired
    JedisAdapter jedisAdapter;

    public boolean fireEvent(EventModel eventModel){
         try{
//             使用 Redis 进行队列的存取
             String json = JSONObject.toJSONString(eventModel);
             String key = RedisKeyUtil.getBizEventQueueKey();
             jedisAdapter.lpush(key, json);

             return true;
         }catch (Exception e){
             return false;
         }
    }
}
