package com.panda.pweibo.models;

import org.json.JSONObject;

/**
 * Created by Administrator on 2015/8/24:16:23.
 */
public class PicUrls {
    private String thumbnail_pic;   // 缩略图片地址
    private String bmiddle_pic;     // 中等尺寸图片地址
    private String original_pic;    // 原始图片地址

    public void setBmiddle_pic(String bmiddle_pic) {
        this.bmiddle_pic = bmiddle_pic;
    }

    public String getBmiddle_pic() {
        return bmiddle_pic;
    }

    public void setOriginal_pic(String original_pic) {
        this.original_pic = original_pic;
    }

    public String getOriginal_pic() {
        return original_pic;
    }

    public void setThumbnail_pic(String thumbnail_pic) {
        this.thumbnail_pic = thumbnail_pic;
    }

    public String getThumbnail_pic() {
        return thumbnail_pic;
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
