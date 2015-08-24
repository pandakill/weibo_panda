package com.panda.pweibo.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.util.LruCache;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.NetworkImageView;
import com.panda.pweibo.R;
import com.panda.pweibo.models.Status;
import com.panda.pweibo.models.User;
import com.android.volley.toolbox.ImageLoader.ImageCache;

import java.util.List;

/**
 * Created by Administrator on 2015/8/22:15:22.
 */
public class StatusAdapter extends BaseAdapter {

    private Context context;
    private List<Status> listStatus;
    private RequestQueue requestQueue;
    private ImageCache imageCache;
    private LruCache<String, Bitmap> lruCache;

    public StatusAdapter(Context context, List<Status> listStatus, RequestQueue requestQueue) {
        this.context        = context;
        this.listStatus     = listStatus;
        this.requestQueue   = requestQueue;
    }

    @Override
    public int getCount() {
        return listStatus.size();
    }

    @Override
    public Object getItem(int position) {
        return listStatus.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.item_status, null);

            holder.pwb_ll_item_status                       = (LinearLayout)        convertView.findViewById(R.id.pwb_ll_item_status);
            holder.pwb_imageview_item_status_avatar         = (ImageView)           convertView.findViewById(R.id.pwb_imageview_item_status_avatar);
            holder.pwb_rl_content                           = (RelativeLayout)      convertView.findViewById(R.id.pwb_rl_content);
            holder.pwb_textview_sender                      = (TextView)            convertView.findViewById(R.id.pwb_textview_sender);
            holder.pwb_textview_item_status_from_and_when   = (TextView)            convertView.findViewById(R.id.pwb_textview_item_status_from_and_when);

            holder.pwb_textview_content                     = (TextView)            convertView.findViewById(R.id.pwb_textview_content);
            holder.include_status_image                     = (FrameLayout)         convertView.findViewById(R.id.include_status_image);
            holder.pwb_imageview_status_image               = (ImageView)           convertView.findViewById(R.id.pwb_imageview_status_image);

            holder.include_retweeted_status                 = (LinearLayout)        convertView.findViewById(R.id.include_retweeted_status);
            holder.pwb_textview_retweeted_content           = (TextView)            convertView.findViewById(R.id.pwb_textview_retweeted_content);

            holder.pwb_ll_share_tottom                      = (LinearLayout)        convertView.findViewById(R.id.pwb_ll_share_tottom);
            holder.pwb_imageview_share_bottom               = (ImageView)           convertView.findViewById(R.id.pwb_imageview_share_bottom);
            holder.textview_share_bottom                    = (TextView)            convertView.findViewById(R.id.textview_share_bottom);
            holder.pwb_ll_comment_tottom                    = (LinearLayout)        convertView.findViewById(R.id.pwb_ll_comment_tottom);
            holder.pwb_imageview_comment_bottom             = (ImageView)           convertView.findViewById(R.id.pwb_imageview_comment_bottom);
            holder.textview_comment_bottom                  = (TextView)            convertView.findViewById(R.id.textview_comment_bottom);
            holder.pwb_ll_praise_tottom                     = (LinearLayout)        convertView.findViewById(R.id.pwb_ll_praise_tottom);
            holder.pwb_imageview_praise_bottom              = (ImageView)           convertView.findViewById(R.id.pwb_imageview_praise_bottom);
            holder.textview_praise_bottom                   = (TextView)            convertView.findViewById(R.id.textview_praise_bottom);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Status    status  = (Status) getItem(position);
        User            user    = status.getUser();

        holder.pwb_textview_sender.setText(user.getScreen_name());
        holder.pwb_textview_item_status_from_and_when.setText(status.getCreated_at() + " 来自 " + Html.fromHtml(status.getSource()));
        holder.pwb_textview_content.setText(status.getText());
        holder.textview_share_bottom.setText(status.getReposts_count() == 0 ? "转发" : status.getReposts_count() + "");
        holder.textview_comment_bottom.setText(status.getComments_count() == 0 ? "评论" : status.getComments_count() + "");
        holder.textview_praise_bottom.setText(status.getAttitudes_count() == 0 ? "点赞" : status.getAttitudes_count() + "");

        /** 加载用户头像图片 */
        lruCache = new LruCache<String, Bitmap>(20);
        imageCache = new ImageCache() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
            @Override
            public Bitmap getBitmap(String key) {
                return lruCache.get(key);
            }

            @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
            @Override
            public void putBitmap(String key, Bitmap value) {
                lruCache.put(key, value);
            }
        };
        ImageLoader imageLoader = new ImageLoader(requestQueue, imageCache);
//        holder.pwb_imageview_item_status_avatar.setTag(user.getId());
//        holder.pwb_imageview_item_status_avatar.setImageUrl(user.getProfile_image_url(), imageLoader);

        ImageListener listener = ImageLoader.getImageListener(holder.pwb_imageview_item_status_avatar,
                R.drawable.ic_com_sina_weibo_sdk_logo, R.drawable.pwb_avatar);
        imageLoader.get(user.getProfile_image_url(), listener);
        return convertView;
    }

    public static class ViewHolder {

        public  LinearLayout        pwb_ll_item_status;                         // 单条微博的linearlayout布局
//        public  NetworkImageView    pwb_imageview_item_status_avatar;           // 微博发布者头像
        public  ImageView           pwb_imageview_item_status_avatar;           // 微博发布者头像
        public  RelativeLayout      pwb_rl_content;                             // 微博发布者昵称的relativelayout布局
        public  TextView            pwb_textview_sender;                        // 微博发布者昵称
        public  TextView            pwb_textview_item_status_from_and_when;     // 微博来源

        public  TextView            pwb_textview_content;                       // 微博正文
        public  FrameLayout         include_status_image;                       // 微博的图片布局
        public  ImageView           pwb_imageview_status_image;                 // 微博的图片部分

        public  LinearLayout        include_retweeted_status;                   // 微博转发内容的布局
        public  TextView            pwb_textview_retweeted_content;             // 微博转发内容

        public  LinearLayout        pwb_ll_share_tottom;                        // 底部分享按钮的linearlayout布局
        public  ImageView           pwb_imageview_share_bottom;                 // 分享按钮的图片
        public  TextView            textview_share_bottom;                      // 分享按钮的textview
        public  LinearLayout        pwb_ll_comment_tottom;                      // 底部分享按钮的linearlayout布局
        public  ImageView           pwb_imageview_comment_bottom;               // 分享按钮的图片
        public  TextView            textview_comment_bottom;                    // 分享按钮的textview
        public  LinearLayout        pwb_ll_praise_tottom;                       // 底部分享按钮的linearlayout布局
        public  ImageView           pwb_imageview_praise_bottom;                // 分享按钮的图片
        public  TextView            textview_praise_bottom;                     // 分享按钮的textview
    }
}
