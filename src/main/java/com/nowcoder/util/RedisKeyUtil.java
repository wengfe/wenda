package com.nowcoder.util;

public class RedisKeyUtil {
    private static String SPLIT = ":";
    private static String BIZ_LIKE = "LIKE";
    private static String BIZ_DISLIKE = "DISLIKE";
    private static String BIZ_EVENTQUEUE = "EVENT_QUEUE";
//    粉丝
    private static String BIZ_FOLLOWER = "FOLLOWER";
//    关注对象
    private static String BIZ_FOLLOWEE = "FOLLOWEE";

    public static String getLikeKey(int entityType, int entityId){
        return BIZ_LIKE + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }

    public static String getDisLikeKey(int entityType, int entityId){
        return BIZ_DISLIKE + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }

    public static String getBizEventQueueKey(){
        return BIZ_EVENTQUEUE;
    }

    // 每个用户对某类实体的关注key
    public static String getBizFolloweeKey(int userId, int entityType){
        return BIZ_FOLLOWER + SPLIT + String.valueOf(userId) + SPLIT + String.valueOf(entityType);
    }
    // 某个实体的粉丝key
    public static String getBizFollowerKey(int entityType, int entityId){
        return BIZ_FOLLOWEE + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }
}
