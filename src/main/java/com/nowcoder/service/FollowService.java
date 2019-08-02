package com.nowcoder.service;

import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class FollowService {
    @Autowired
    JedisAdapter jedisAdapter;

    public boolean follow(int userId, int entityType, int entityId){
        String followerKey = RedisKeyUtil.getBizFollowerKey(entityType, entityId);
        String followeeKey = RedisKeyUtil.getBizFolloweeKey(userId, entityType);
        Date date = new Date();

//        redis 事务加载
        Jedis jedis = jedisAdapter.getJedis();
        Transaction tx = jedisAdapter.multi(jedis);

//        将用户加入关注对象粉丝集
        tx.zadd(followerKey, date.getTime(), String.valueOf(userId));
//        将关注对象添加到用户关注列表
        tx.zadd(followeeKey, date.getTime(), String.valueOf(entityId));

//        redis 事务处理
        List<Object> ret = jedisAdapter.exec(tx, jedis);

        return ret.size() == 2 && (Long)ret.get(0) > 0 && (Long)ret.get(1) > 0;
    }

    public boolean unfollow(int userId, int entityType, int entityId){
        String followerKey = RedisKeyUtil.getBizFollowerKey(entityType, entityId);
        String followeeKey = RedisKeyUtil.getBizFolloweeKey(userId, entityType);
        Date date = new Date();

//        redis 事务加载
        Jedis jedis = jedisAdapter.getJedis();
        Transaction tx = jedisAdapter.multi(jedis);
        tx.zrem(followerKey, String.valueOf(userId));
        tx.zrem(followeeKey, String.valueOf(entityId));
//        redis 事务处理
        List<Object> ret = jedisAdapter.exec(tx, jedis);

        return ret.size() == 2 && (Long)ret.get(0) > 0 && (Long)ret.get(1) > 0;
    }

    private List<Integer> getIdsFromSet(Set<String> idset){
        List<Integer> ids = new ArrayList<>();
        for (String str : idset){
            ids.add(Integer.parseInt(str));
        }
        return ids;
    }

//    获取关注者
    public List<Integer> getFollowers(int entityType, int entityId, int count){
        String followerKey = RedisKeyUtil.getBizFollowerKey(entityType, entityId);
        return getIdsFromSet(jedisAdapter.zrevrange(followerKey, 0, count));
    }

    public List<Integer> getFollowers(int entityType, int entityId, int offset, int count){
        String followerKey = RedisKeyUtil.getBizFolloweeKey(entityType, entityId);
        return getIdsFromSet(jedisAdapter.zrevrange(followerKey, offset, count));
    }

    public List<Integer> getFollowees(int userId, int entityId, int count){
        String followeeKey = RedisKeyUtil.getBizFollowerKey(userId, entityId);
        return getIdsFromSet(jedisAdapter.zrevrange(followeeKey, 0, count));
    }

    public List<Integer> getFollowees(int userId, int entityId, int offset, int count){
        String followeeKey = RedisKeyUtil.getBizFolloweeKey(userId, entityId);
        return getIdsFromSet(jedisAdapter.zrevrange(followeeKey, offset, count));
    }

    public long getFolloweesCount(int userId, int entityId){
        String followeeKey = RedisKeyUtil.getBizFolloweeKey(userId, entityId);
        return jedisAdapter.zcard(followeeKey);
    }

    public long getFollowersCount(int entityType, int entityId){
        String followerKey = RedisKeyUtil.getBizFollowerKey(entityType, entityId);
        return jedisAdapter.zcard(followerKey);
    }

    public boolean isFollower(int userId, int entityType, int entityId){
        String followerKey = RedisKeyUtil.getBizFollowerKey(entityType, entityId);
        return jedisAdapter.zscore(followerKey, String.valueOf(userId)) != null;
    }
    
}
