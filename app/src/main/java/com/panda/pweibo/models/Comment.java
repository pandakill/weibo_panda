package com.panda.pweibo.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * 消息类
 * Created by Administrator on 2015/8/26:12:07.
 */
public class Comment implements Serializable {
    private String  created_at;     // 评论创建时间
    private long    id;             // 评论的ID
    private String  text;           // 评论的内容
    private String  source;         // 评论的来源
    private User    user;           // 评论作者的用户信息字段
    private String  mid;            // 评论的MID
    private String  idstr;          // 字符串型的评论ID
    private Status  status;         // 评论的微博信息字段
    private Comment reply_comment;  // 评论来源评论，当本评论属于对另一评论的回复时返回此字段

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getIdstr() {
        return idstr;
    }

    public void setIdstr(String idstr) {
        this.idstr = idstr;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Comment getReply_comment() {
        return reply_comment;
    }

    public void setReply_comment(Comment reply_comment) {
        this.reply_comment = reply_comment;
    }

    public Comment parseJson(JSONObject jsonObject) throws JSONException {
        if (jsonObject != null) {
            Comment comment = new Comment();
            comment.setCreated_at(jsonObject.optString("created_at"));
            comment.setId(jsonObject.optLong("id"));
            comment.setText(jsonObject.optString("text"));
            comment.setSource(jsonObject.optString("source"));
            comment.setUser(new User().parseJson(jsonObject.optJSONObject("user")));
            comment.setMid(jsonObject.optString("mid"));
            comment.setIdstr(jsonObject.optString("idstr"));
            comment.setStatus(new Status().parseJson(jsonObject.optJSONObject("status")));
            comment.setReply_comment(new Comment().parseJson(jsonObject.optJSONObject("reply_comment")));
            return comment;
        }
        return null;
    }
}
