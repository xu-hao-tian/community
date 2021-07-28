package com.nowcoder.community.util;


import org.omg.CORBA.StringHolder;

/**
 * 生成redisKey工具.
 *
 * @Author: 许昊天
 * @Date: 2021/06/13/10:30
 * @Description:
 */
public class RedisKeyUtil {

    // 分隔符
    private static final String SPLIT = ":";
    // 实体的赞
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    // 用户的赞
    private static final String PREFIX_USER_LIKE = "like:user";
    // 关注的目标
    private static final String PREFIX_FOLLOWEE = "followee";
    // 关注的粉丝
    private static final String PREFIX_FOLLOWER = "follower";
    // 登陆验证码
    private static final String PREFIX_KAPTCHA = "kaptcha";
    // 登陆的凭证
    private static final String PREFIX_TICKET = "ticket";
    // 登录的用户
    private static final String PREFIX_USER = "user";
    // 单日uv
    private static final String PREFIX_UV = "uv";
    // 区间uv
    private static final String PREFIX_DAU = "dau";
    // 帖子分数
    private static final String PREFIX_POST = "post";
    // 验证码
    private static final String PREFIX_CODE = "code";

    /**
     * 某个实体的赞
     * like:entity:entityType:entityId -> set(userId)
     * @param entityType
     * @param entityId
     * @return
     */
    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    /**
     * 某个用户的赞
     * like:user:userId -> int
     * @param userId
     * @return
     */
    public static String getUserLikeKey(int userId) {
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    /**
     * 某个用户关注的实体
     * followee:userId:entityType -> zset(entityId,now)
     * @param userId
     * @param entityType
     * @return
     */
    public static String getFolloweeKey(int userId, int entityType) {
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    /**
     * 某个实体拥有的粉丝
     * follower:entityType:entityId -> zset(userId,now)
     * @param entityType
     * @param entityId
     * @return
     */
    public static String getFollowerKey(int entityType, int entityId) {
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    /**
     * 登陆验证码
     * @param owner
     * @return
     */
    public static String getKaptchaKey(String owner) {
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

    /**
     * 登陆的凭证
     * @param ticket
     * @return
     */
    public static String getTicketKey(String ticket) {
        return PREFIX_TICKET + SPLIT + ticket;
    }

    /**
     * 用户
     * @param userId
     * @return
     */
    public static String getUserKey(int userId) {
        return PREFIX_USER + SPLIT + userId;
    }

    /**
     * 单日UV
     * @param date
     * @return
     */
    public static String getUVKey(String date) {
        return PREFIX_UV + SPLIT + date;
    }

    /**
     * 区间UV
     * @param startDate
     * @param endDate
     * @return
     */
    public static String getUVKey(String startDate, String endDate) {
        return PREFIX_UV + SPLIT + startDate + SPLIT + endDate;
    }

    /**
     * 单日活跃用户
     * @param date
     * @return
     */
    public static String getDAUKey(String date) {
        return PREFIX_DAU + SPLIT + date;
    }

    /**
     * 单日区间活跃用户
     * @param startDate
     * @param endDate
     * @return
     */
    public static String getDAUKey(String startDate, String endDate) {
        return PREFIX_DAU + SPLIT + startDate + SPLIT + endDate;
    }

    /**
     * 帖子分数
     * @return
     */
    public static String getPostScoreKey() {
        return PREFIX_POST + SPLIT + "score";
    }

    /**
     * 获取验证码
     * @param owner
     * @return
     */
    public static String getCodeKey(String owner) {
        return PREFIX_CODE + SPLIT + owner;
    }
}
