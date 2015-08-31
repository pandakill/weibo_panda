package com.panda.pweibo.models;

import android.text.TextUtils;

import com.panda.pweibo.constants.Uri;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/8/24:16:23.
 */
public class PicUrls implements Serializable {
    private String thumbnail_pic;   // 缩略图片地址
    private String bmiddle_pic;     // 中等尺寸图片地址
    private String original_pic;    // 原始图片地址

    private boolean showOriImg;     //是否有原图

    /** 从缩略图url中截取末尾的图片id，用于拼接成其他质量图片url */
    public String getImageId() {
        int indexOf = thumbnail_pic.lastIndexOf("/") + 1;
        return thumbnail_pic.substring(indexOf);
    }

    public void setBmiddle_pic(String bmiddle_pic) {
        this.bmiddle_pic = bmiddle_pic;
    }

    public String getBmiddle_pic() {
        return TextUtils.isEmpty(bmiddle_pic) ? Uri.BMIDDLE_URL + getImageId() : bmiddle_pic;
    }

    public void setOriginal_pic(String original_pic) {
        this.original_pic = original_pic;
    }

    public String getOriginal_pic() {
        return TextUtils.isEmpty(original_pic) ? Uri.ORIGINAL_URL + getImageId() : original_pic;
    }

    public void setThumbnail_pic(String thumbnail_pic) {
        this.thumbnail_pic = thumbnail_pic;
    }

    public String getThumbnail_pic() {
        return thumbnail_pic;
    }

    public void setShowOriImg(boolean showOriImg) {
        this.showOriImg = showOriImg;
    }

    public boolean IsShowOriImg() {
        return showOriImg;
    }

    public PicUrls parseJson(JSONObject jsonObject) {
        if (jsonObject != null) {
            PicUrls picUrls = new PicUrls();
            picUrls.setBmiddle_pic(jsonObject.optString("bmiddle_pic"));
            picUrls.setOriginal_pic(jsonObject.optString("original_pic"));
            picUrls.setThumbnail_pic(jsonObject.optString("thumbnail_pic"));
            return picUrls;
        }
        return null;
    }
}
