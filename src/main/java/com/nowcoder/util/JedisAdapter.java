package com.nowcoder.util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nowcoder.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.*;

import java.util.List;
import java.util.Set;

@Service
public class JedisAdapter implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);
    private JedisPool pool;

    public static void print(int index, Object object) {
        System.out.println(String.format("%d,%s", index, object.toString()));
    }

    public static void main(String[] args) {
        Jedis jedis = new Jedis("redis://localhost:6379/9");
//        清空所有库
//        jedis.flushAll()
//        清空当前库
        jedis.flushDB();

//        get set
        jedis.set("hello", "java");
        print(1, jedis.get("hello"));
//        设置保存时间
        jedis.setex("code", 10, "0527");

//        数值操作
        jedis.set("pv", "100");
//        数值加法
        jedis.incr("pv");
        jedis.incrBy("pv", 5);
//      数值减法
        jedis.decrBy("pv", 2);
        print(2, "pv");
//        通配符查找
        print(3, jedis.keys("*"));

        String listName = "list";
        jedis.del(listName);
        for (int i = 0; i < 10; i++) {
            jedis.lpush(listName, "a" + String.valueOf(i));
        }
//        列表操作
        print(4, jedis.lrange(listName, 0, 12));
        print(4, jedis.lrange(listName, 2, 3));
        print(5, jedis.llen(listName));
        print(6, jedis.lpop(listName));
        print(7, jedis.llen(listName));
        print(7, jedis.lrange(listName, 0, 12));
        print(9, jedis.lindex(listName, 3));
//        指定 value 值插入，返回列表总长度
        print(10, jedis.linsert(listName, BinaryClient.LIST_POSITION.AFTER, "a4", "xx"));
        print(10, jedis.linsert(listName, BinaryClient.LIST_POSITION.AFTER, "a4", "bb"));
        print(11, jedis.lrange(listName, 0, 12));


//        hash
        String userKey = "userxx";
        jedis.hset(userKey, "name", "jim");
        jedis.hset(userKey, "age", "12");
        jedis.hset(userKey, "phone", "15800000000");

        print(12, jedis.hget(userKey, "name"));
        print(13, jedis.hgetAll(userKey));
        jedis.hdel(userKey, "phone");
        print(14, jedis.hgetAll(userKey));
        print(15, jedis.hexists(userKey, "email"));
        print(15, jedis.hexists(userKey, "age"));
        print(17, jedis.hkeys(userKey));
        print(18, jedis.hvals(userKey));
        jedis.hsetnx(userKey, "school", "zju");
        jedis.hsetnx(userKey, "name", "zzy");
        print(19, jedis.hgetAll(userKey));


//        set
        String likeKey1 = "commentLike1";
        String likeKey2 = "commentLike2";

        for (int i = 0; i < 10; i++) {
            jedis.sadd(likeKey1, String.valueOf(i));
            jedis.sadd(likeKey2, String.valueOf(i * i));
        }

        print(20, jedis.smembers(likeKey1));
        print(21, jedis.smembers(likeKey2));
//        并
        print(22, jedis.sunion(likeKey1, likeKey2));
//        不同
        print(23, jedis.sdiff(likeKey1, likeKey2));
//        交
        print(24, jedis.sinter(likeKey1, likeKey2));
        print(25, jedis.sismember(likeKey1, "12"));
        print(26, jedis.sismember(likeKey2, "16"));
        jedis.srem(likeKey1, "5");
        print(27, jedis.smembers(likeKey1));

//        从集合2向集合1移动元素
        jedis.smove(likeKey2, likeKey1, "25");
        print(28, jedis.smembers(likeKey1));
        print(28, jedis.smembers(likeKey2));

//        元素个数
        print(29, jedis.scard(likeKey1));

//        优先队列 堆
        String rankKey = "rankKey";
        jedis.zadd(rankKey, 15, "jim");
        jedis.zadd(rankKey, 60, "Ben");
        jedis.zadd(rankKey, 90, "Lee");
        jedis.zadd(rankKey, 75, "Lucy");
        jedis.zadd(rankKey, 80, "Mei");
        print(30, jedis.zcard(rankKey));

//        按区间显示 value
        print(31, jedis.zcount(rankKey, 61, 100));
        print(32, jedis.zscore(rankKey, "Lucy"));
        jedis.zincrby(rankKey, 2, "Lucy");
        print(33, jedis.zscore(rankKey, "Lucy"));
//        显示 key
        print(35, jedis.zrange(rankKey, 0, 100));

        print(36, jedis.zrange(rankKey, 1, 3));
//        倒序排序取值
        print(36, jedis.zrevrange(rankKey, 1, 3));
//        遍历
        for (Tuple tuple : jedis.zrangeByScoreWithScores(rankKey, "60", "100")) {
            print(37, tuple.getElement() + ":" + String.valueOf(tuple.getScore()));
        }

//        排名
        print(38, jedis.zrank(rankKey, "Ben"));
        print(39, jedis.zrevrank(rankKey, "Ben"));

//        分值一致按 value 字母排序
        String setKey = "zset";
        jedis.zadd(setKey, 1, "a");
        jedis.zadd(setKey, 1, "b");
        jedis.zadd(setKey, 1, "c");
        jedis.zadd(setKey, 1, "d");
        jedis.zadd(setKey, 1, "e");

        print(40, jedis.zlexcount(setKey, "-", "+"));
//        >b  <=d
        print(41, jedis.zlexcount(setKey, "(b", "[d"));
//        >=b <=d
        print(41, jedis.zlexcount(setKey, "[b", "[d"));
        jedis.zrem(setKey, "b");
        print(43, jedis.zrange(setKey, 0, 10));
//        根据字典序排序
        jedis.zremrangeByLex(setKey, "(c", "+");
        print(44, jedis.zrange(setKey, 0, 5));


//        print(45,jedis.get("pv"));
        /*
//        连接池 默认 8 个线程
        JedisPool pool = new JedisPool("redis://localhost:6379/10");
        for (int i = 0; i < 100; ++i) {
            Jedis j = pool.getResource();
            jedis.incr("pv");
            j.get("pv");
            System.out.println(j.get("pv"));
            print(45, j.get("pv"));
//            释放线程
            j.close();
        }*/

//        redis 建立缓存
        User user = new User();
        user.setName("xx");
        user.setPassword("ppp");
        user.setHeadUrl("a.jpg");
        user.setSalt("salt");
        user.setId(1);

        print(46, JSONObject.toJSONString(user));
//         对象序列化存储为 JSON 串
        jedis.set("user1", JSONObject.toJSONString(user));
        jedis.sadd("a:b:c", "4");
        String value = jedis.get("user1");
//        将 JSON 串 反序列化为对象
        User user2 = JSON.parseObject(value, User.class);
        print(47, user2);
//        String i = "1234567890090983541234";
//        System.out.println(i.substring(0,6)+"****"+i .substring(i.length()-4));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        pool = new JedisPool("redis://localhost:6379/10");
    }


    public long lpush(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lpush(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public long sadd(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sadd(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public long srem(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.srem(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public long scard(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.scard(key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public boolean sismember(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sismember(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return false;
    }

    public List<String> brpop(int timeout, String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.brpop(timeout, key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public Jedis getJedis(){
        return pool.getResource();
    }

    public Transaction multi(Jedis jedis){
        try {
            return jedis.multi();
        }catch (Exception e){
            logger.error("发生异常 " + e.getMessage());
        }
        return null;
    }

    public List<Object> exec (Transaction tx, Jedis jedis){
        try{
            return tx.exec();
        }catch (Exception e){
            logger.error(" 发生异常" + e.getMessage());
        }finally {
            if (tx != null){
                try{
                    tx.close();
                }catch (Exception e){
                    logger.error("发生异常 " + e.getMessage());
                }
            }
            if (jedis != null){
                jedis.close();
            }
        }
        return null;
    }

    public long zadd(String key, double score, String value){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zadd(key, score, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public Set<String> zrevrange(String key, int start, int end){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrevrange(key, start, end);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public long zcard(String key){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zcard(key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public Double zscore(String key, String member){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zscore(key, member);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

}
