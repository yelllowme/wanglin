package com.wanglinkeji.wanglin.model;

/**
 * Created by Administrator on 2016/8/23.
 * 评论Model
 */
public class CommentModel {

    //评论Id
    private int Id;

    //发表评论的人的名字
    private String user;

    //评论内容
    private String commentDetails;

    //评论时间
    private String date;

    //被评论的“评论”Id
    private int replyId;

    //被评论的“评论”的用户的Id
    private int replyUserId;

    //被评论的“评论”的用户的昵称
    private String replyUserName;

    public int getReplyId() {
        return replyId;
    }

    public void setReplyId(int replyId) {
        this.replyId = replyId;
    }

    public int getReplyUserId() {
        return replyUserId;
    }

    public void setReplyUserId(int replyUserId) {
        this.replyUserId = replyUserId;
    }

    public String getReplyUserName() {
        return replyUserName;
    }

    public void setReplyUserName(String replyUserName) {
        this.replyUserName = replyUserName;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCommentDetails() {
        return commentDetails;
    }

    public void setCommentDetails(String commentDetails) {
        this.commentDetails = commentDetails;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
