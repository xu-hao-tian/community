package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 会话.
 *
 * @Author: 许昊天
 * @Date: 2021/06/11/16:12
 * @Description:
 */
@Mapper
public interface MessageMapper {

    /**
     * 查询当前用户的会话列表，针对每个会话只返回一条最新的私信.
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    List<Message> selectConversations(@Param("userId") int userId,@Param("offset") int offset,@Param("limit") int limit);

    /**
     * 查询当前用户的会话数量.
     * @param userId
     * @return
     */
    int selectConversationCount(int userId);

    /**
     * 查询某个会话所包含的私信列表.
     * @param conversationId
     * @param offset
     * @param limit
     * @return
     */
    List<Message> selectLetters(@Param("conversationId") String conversationId,@Param("offset") int offset,@Param("limit") int limit);

    /**
     * 查询某个会话所包含的私信数量.
     * @param conversationId
     * @return
     */
    int selectLetterCount(String conversationId);

    /**
     * 查询未读私信的数量.
     * @param userId
     * @param conversationId
     * @return
     */
    int selectLetterUnreadCount(@Param("userId") int userId,@Param("conversationId") String conversationId);

    /**
     * 新增消息
     * @param message
     * @return
     */
    int insertMessage(Message message);

    /**
     * 修改消息的状态
     * @param ids
     * @param status
     * @return
     */
    int updateStatus(@Param("ids") List<Integer> ids, @Param("status") int status);

    /**
     * 查询某个主题下最新的通知
     * @param userId
     * @param topic
     * @return
     */
    Message selectLatestNotice(@Param("userId") int userId, @Param("topic") String topic);

    /**
     * 查询某个主题所包含的通知数量
     * @param userId
     * @param topic
     * @return
     */
    int selectNoticeCount(@Param("userId") int userId, @Param("topic") String topic);

    /**
     * 查询未读的通知的数量
     * @param userId
     * @param topic
     * @return
     */
    int selectNoticeUnreadCount(@Param("userId") int userId, @Param("topic") String topic);

    /**
     * 查询某个主题的所包含的通知列表
     * @param userId
     * @param topic
     * @param offset
     * @param limit
     * @return
     */
    List<Message> selectNotices(@Param("userId") int userId,@Param("topic") String topic,@Param("offset")  int offset,@Param("limit") int limit);
}