package com.panda.pweibo.models;

import com.panda.pweibo.utils.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2015/8/22:9:35.
 */
public class Status implements Serializable {
    private     String              created_at;                 // 微博创建时间
    private     long                 id;                         // 微博ID
    private     long                 mid;                        // 微博MID
    private     String              idstr;                      // 字符串型的微博ID
    private     String              text;                       // 微博信息内容
    private     String              source;                     // 微博来源
    private     boolean             favorited;                  // 是否已收藏，true：是，false：否
    private     boolean             truncated;                  // 是否被截断，true：是，false：否
    private     String              in_reply_to_status_id;      // （暂未支持）回复ID
    private     String              in_reply_to_user_id;        // （暂未支持）回复人UID
    private     String              in_reply_to_screen_name;    // （暂未支持）回复人昵称
    private     String              thumbnail_pic;              // 缩略图片地址，没有时不返回此字段
    private     String              bmiddle_pic;                // 中等尺寸图片地址，没有时不返回此字段
    private     String              original_pic;               // 原始图片地址，没有时不返回此字段
//    private     Object              geo;                        // 地理信息字段
    private     User                user;                       // 微博作者的用户信息字段
    private     Status              retweeted_status;           // 被转发的原微博信息字段，当该微博为转发微博时返回
    private     int                 reposts_count;              // 转发数
    private     int                 comments_count;             // 评论数
    private     int                 attitudes_count;            // 表态数
    private     int                 mlevel;                     // 暂未支持
//    private     Object              visible;                    // 微博的可见性及指定可见分组信息。
                                                                // 该object中type取值，0：普通微博，1：私密微博，3：指定分组微博，4：密友微博；list_id为分组的组号
    private     ArrayList<PicUrls>  pic_ids;                    // 微博配图ID。多图时返回多图ID，用来拼接图片url。
                                                                // 用返回字段thumbnail_pic的地址配上该返回字段的图片ID，即可得到多个图片url。
//    private     ArrayList<Object>   ad;                         // 微博流内的推广微博ID

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

    public long getMid() {
        return mid;
    }

    public void setMid(long mid) {
        this.mid = mid;
    }

    public String getIdstr() {
        return idstr;
    }

    public void setIdstr(String idstr) {
        this.idstr = idstr;
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

    public boolean isFavorited() {
        return favorited;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    public boolean isTruncated() {
        return truncated;
    }

    public void setTruncated(boolean truncated) {
        this.truncated = truncated;
    }

    public String getIn_reply_to_status_id() {
        return in_reply_to_status_id;
    }

    public void setIn_reply_to_status_id(String in_reply_to_status_id) {
        this.in_reply_to_status_id = in_reply_to_status_id;
    }

    public String getIn_reply_to_user_id() {
        return in_reply_to_user_id;
    }

    public void setIn_reply_to_user_id(String in_reply_to_user_id) {
        this.in_reply_to_user_id = in_reply_to_user_id;
    }

    public String getIn_reply_to_screen_name() {
        return in_reply_to_screen_name;
    }

    public void setIn_reply_to_screen_name(String in_reply_to_screen_name) {
        this.in_reply_to_screen_name = in_reply_to_screen_name;
    }

    public String getThumbnail_pic() {
        return thumbnail_pic;
    }

    public void setThumbnail_pic(String thumbnail_pic) {
        this.thumbnail_pic = thumbnail_pic;
    }

    public String getBmiddle_pic() {
        return bmiddle_pic;
    }

    public void setBmiddle_pic(String bmiddle_pic) {
        this.bmiddle_pic = bmiddle_pic;
    }

    public String getOriginal_pic() {
        return original_pic;
    }

    public void setOriginal_pic(String original_pic) {
        this.original_pic = original_pic;
    }

//    public Object getGeo() {
//        return geo;
//    }

//    public void setGeo(Object geo) {
//        this.geo = geo;
//    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Status getRetweeted_status() {
        return retweeted_status;
    }

    public void setRetweeted_status(Status retweeted_status) {
        this.retweeted_status = retweeted_status;
    }

    public int getReposts_count() {
        return reposts_count;
    }

    public void setReposts_count(int reposts_count) {
        this.reposts_count = reposts_count;
    }

    public int getComments_count() {
        return comments_count;
    }

    public void setComments_count(int comments_count) {
        this.comments_count = comments_count;
    }

    public int getAttitudes_count() {
        return attitudes_count;
    }

    public void setAttitudes_count(int attitudes_count) {
        this.attitudes_count = attitudes_count;
    }

    public int getMlevel() {
        return mlevel;
    }

    public void setMlevel(int mlevel) {
        this.mlevel = mlevel;
    }

//    public Object getVisible() {
//        return visible;
//    }

//    public void setVisible(Object visible) {
//        this.visible = visible;
//    }

    public ArrayList<PicUrls> getPic_ids() {
        return pic_ids;
    }

    public void setPic_ids(ArrayList<PicUrls> pic_ids) {
        this.pic_ids = pic_ids;
    }

//    public ArrayList<Object> getAd() {
//        return ad;
//    }

//    public void setAd(ArrayList<Object> ad) {
//        this.ad = ad;
//    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof  Status) {
            return this.id == ((Status) o).id;
        }
        return super.equals(o);
    }

    public Status parseJson(JSONObject jsonObject) throws JSONException {
        if (null != jsonObject) {
            Status status = new Status();
            status.setCreated_at(jsonObject.optString("created_at"));
            status.setId(jsonObject.optInt("id"));
            status.setText(jsonObject.optString("text"));
            status.setSource(jsonObject.optString("source"));
            status.setFavorited(jsonObject.optBoolean("favorited"));
            status.setTruncated(jsonObject.optBoolean("truncated"));
//            status.setGeo(jsonObject.opt("geo"));
            status.setMid(jsonObject.optInt("mid"));
            status.setReposts_count(jsonObject.optInt("reposts_count"));
            status.setComments_count(jsonObject.optInt("comments_count"));
            status.setUser(new User().parseJson((JSONObject) jsonObject.opt("user")));
            status.setRetweeted_status(new Status().parseJson((JSONObject) jsonObject.opt("retweeted_status")));
            status.setReposts_count(jsonObject.optInt("reposts_count"));
            status.setComments_count(jsonObject.optInt("comments_count"));
            status.setAttitudes_count(jsonObject.optInt("comments_count"));
            status.setMlevel(jsonObject.optInt("mlevel"));
//            status.setVisible(jsonObject.opt("visible"));
            if (jsonObject.getJSONArray("pic_urls").length() != 0) {
                JSONArray jsonArray = jsonObject.getJSONArray("pic_urls");
                ArrayList<PicUrls> list = new ArrayList<PicUrls>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    PicUrls picUrls = new PicUrls().parseJson(obj);
                    list.add(picUrls);
                }
                status.setPic_ids(list);
            }
            //status.setPic_ids(jsonObject.opt("pic_ids"));
            //status.setAd(jsonObject.opt("ad"));

            return status;
        }
        return null;
    }
}
