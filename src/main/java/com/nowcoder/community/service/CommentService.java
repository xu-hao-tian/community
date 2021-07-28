package com.nowcoder.community.service;

import com.nowcoder.community.dao.CommentMapper;
import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * 显示评论.
 *
 * @Author: 许昊天
 * @Date: 2021/06/06/15:48
 * @Description:
 */
@Service
public class CommentService implements CommunityConstant{

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private DiscussPostService discussPostService;

    /**
     * 获取帖子的信息
     * @param entityType
     * @param entityId
     * @param offset
     * @param limit
     * @return
     */
    public List<Comment> findCommentsByEntity(int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectCommentsByEntity(entityType, entityId, offset, limit);
    }

    /**
     * 获取帖子的回复数量
     * @param entityType
     * @param entityId
     * @return
     */
    public int findCommentCount(int entityType, int entityId) {
        return commentMapper.selectCountByEntity(entityType, entityId);
    }

    /**
     * 添加帖子的回复信息
     * @param comment
     * @return
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }

        // 添加评论
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        int rows = commentMapper.insertComment(comment);

        // 更新帖子评论数量
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            int count = commentMapper.selectCountByEntity(comment.getEntityType(), comment.getEntityId());
            discussPostService.updateCommentCount(comment.getEntityId(), count);
        }
        return rows;
    }

    /**
     * 获取触发的评论事件
     * @param id
     * @return
     */
    public Comment findCommentById(int id) {
        return commentMapper.selectCommentById(id);
    }

    /**
     * 获取帖子的回复数量
     * @param id
     * @return
     */
    public int findCommentCountById(int id) {
        return commentMapper.selectCommentCountById(id);
    }

    /**
     * 获取用户所有的回帖
     * @param id
     * @param offset
     * @param limit
     */
    public List<Comment> findCommentsByUserId(int id, int offset, int limit) {
        return commentMapper.selectCommentsByUserId(id, offset, limit);
    }
}
