package com.panda.pweibo.utils;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.panda.pweibo.R;

/**
 * 标题栏工具类，生成标题栏
 *
 * Created by Administrator on 2015/8/22:10:43.
 */
public class TitlebarUtils {

    /** 视图控件 */
    private     View            titlebar;
    private     ImageView       titlebar_imageview_left;
    private     TextView        titlebar_textview_left;
    private     TextView        titlebar_content;
    private     TextView        titlebar_textview_right;
    private     ImageView       titlebar_imageview_right;

    /** 初始化控件 */
    public TitlebarUtils(Activity context) {
        titlebar                    =               context.findViewById(R.id.titlebar);
        titlebar_imageview_left     = (ImageView)   titlebar.findViewById(R.id.titlebar_imageview_left);
        titlebar_textview_left      = (TextView)    titlebar.findViewById(R.id.titlebar_textview_left);
        titlebar_content            = (TextView)    titlebar.findViewById(R.id.pwb_textview_home_title);
        titlebar_imageview_right    = (ImageView)   titlebar.findViewById(R.id.titlebar_imageview_right);
        titlebar_textview_right     = (TextView)    titlebar.findViewById(R.id.titlebar_textview_right);
    }

    public TitlebarUtils(View context) {
        titlebar                    =               context.findViewById(R.id.titlebar);
        titlebar_imageview_left     = (ImageView)   titlebar.findViewById(R.id.titlebar_imageview_left);
        titlebar_textview_left      = (TextView)    titlebar.findViewById(R.id.titlebar_textview_left);
        titlebar_content            = (TextView)    titlebar.findViewById(R.id.pwb_textview_home_title);
        titlebar_imageview_right    = (ImageView)   titlebar.findViewById(R.id.titlebar_imageview_right);
        titlebar_textview_right     = (TextView)    titlebar.findViewById(R.id.titlebar_textview_right);
    }

    /**
     * 设置标题栏背景
     *
     * @param resid
     *              背景资源的id
     */
    public TitlebarUtils setTitlebgRes(int resid) {
        titlebar.setBackgroundResource(resid);
        return this;
    }

    /**
     * 设置标题栏的标题
     *
     * @param text
     *              标题文字
     */
    public TitlebarUtils setTitleContent(String text) {
        titlebar_content.setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
        titlebar_content.setText(text);
        return this;
    }

    /**
     * 设置标题栏左侧的ImageView,与setTitlebarTVLeft(String text)只能二选一
     *
     * @param resid
     *              图片的资源id
     */
    public TitlebarUtils setTitlebarIvLeft(int resid) {
        titlebar_imageview_left.setVisibility(resid > 0 ? View.VISIBLE : View.GONE);
        titlebar_imageview_left.setImageResource(resid);
        return this;
    }

    /**
     * 设置标题栏左侧的textView,与setTitlebarIVLeft(int resid)只能二选一
     *
     * @param text
     *              标题text
     */
    public TitlebarUtils setTitlebarTvLeft(String text) {
        titlebar_textview_left.setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
        titlebar_textview_left.setText(text);
        return this;
    }

    /**
     * 设置标题栏左侧的监听器
     *
     * @param listner
     *          点击事件监听器
     */
    public TitlebarUtils setLeftOnClickListner(OnClickListener listner) {
        if (titlebar_imageview_left.getVisibility() == View.VISIBLE) {
            titlebar_imageview_left.setOnClickListener(listner);
        } else if (titlebar_textview_left.getVisibility() == View.VISIBLE) {
            titlebar_textview_left.setOnClickListener(listner);
        }
        return this;
    }

    /**
     * 设置标题栏的右侧ImageView,与setTitlebarTVRight(String text)只能二选一
     * @param resid
     *              图片的资源id
     */
    public TitlebarUtils setTitlebarIvRight(int resid) {
        titlebar_imageview_right.setVisibility(resid > 0 ? View.VISIBLE : View.GONE);
        titlebar_imageview_right.setImageResource(resid);
        return this;
    }

    /**
     * 设置标题栏的右侧TextView,与setTitlebarIVRight(String text)只能二选一
     * @param text
     *              右侧标题
     */
    public TitlebarUtils setTitlebarTvRight(String text) {
        titlebar_textview_right.setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
        titlebar_textview_right.setText(text);
        return this;
    }

    /**
     * 设置标题栏右侧的监听器
     *
     * @param listner
     *          点击事件监听器
     */
    public TitlebarUtils setRightOnClickListner(OnClickListener listner) {
        if (titlebar_imageview_right.getVisibility() == View.VISIBLE) {
            titlebar_imageview_right.setOnClickListener(listner);
        } else if (titlebar_textview_right.getVisibility() == View.VISIBLE) {
            titlebar_textview_right.setOnClickListener(listner);
        }
        return this;
    }

    /**
     * 构建标题栏,返回titlebar
     */
    public View build() {
        return titlebar;
    }
}
