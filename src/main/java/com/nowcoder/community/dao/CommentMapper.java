package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.util.CommunityConstant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 显示评论.
 *
 * @Author: 许昊天
 * @Date: 2021/06/06/15:33
 * @Description:
 */
@Mapper
public interface CommentMapper {

    /**
     * 查询帖子.
     * @param entityType
     * @param entityId
     * @param offset
     * @param limit
     * @return
     */
    List<Comment> selectCommentsByEntity(@Param("entityType") int entityType,@Param("entityId") int entityId,@Param("offset") int offset,@Param("limit") int limit);

    /**
     * 查询回帖数量.
     * @param entityType
     * @param entityId
     * @return
     */
    int selectCountByEntity(@Param("entityType") int entityType,@Param("entityId") int entityId);

    /**
     * 增加评论.
     * @param comment
     * @return
     */
    int insertComment(Comment comment);

    /**
     * 查询触发的评论事件
     * @param id
     * @return
     */
    Comment selectCommentById(int id);

    /**
     * 查询帖子的回复数量
     * @param id
     * @return
     */
    int selectCommentCountById(int id);

    /**
     * 查询用户的回帖
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    List<Comment> selectCommentsByUserId(@Param("userId") int userId, @Param("offset") int offset, @Param("limit") int limit);
}
