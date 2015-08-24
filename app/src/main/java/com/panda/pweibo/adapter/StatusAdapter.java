package com.panda.pweibo.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.NetworkImageView;
import com.panda.pweibo.R;
import com.panda.pweibo.models.PicUrls;
import com.panda.pweibo.models.Status;
import com.panda.pweibo.models.User;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.panda.pweibo.utils.ToastUtils;

import java.util.ArrayList;
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
    private ImageLoader imageLoader;

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

        imageLoader = new ImageLoader(requestQueue, imageCache);

        if (convertView == null) {
            holder = new ViewHolder();
            //convertView = View.inflate(context, R.layout.item_status, null);
            convertView = LayoutInflater.from(context).inflate(R.layout.item_status,null);
            /** 调用init方法,初始化控件 */
            init(holder, convertView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            resetViewHolder(holder);
        }

        final Status    status  = (Status) getItem(position);
        User            user    = status.getUser();
        Status retweeted_status = status.getRetweeted_status();

        holder.pwb_textview_sender.setText(user.getScreen_name());
        holder.pwb_textview_item_status_from_and_when.setText(status.getCreated_at() + " 来自 " + Html.fromHtml(status.getSource()));
        holder.pwb_textview_content.setText(status.getText());

        /** 如果微博有图片,且不是转发的微博,则在正文部分显示图片 */
        if ( status.getPic_ids() != null && status.getRetweeted_status() == null) {
            setImages(holder.include_status_image, holder.pwb_gridview_status_image, holder.pwb_imageview_status_image, status);
        }
        /** 如果有转发的微博,则显示转发微博内容;如果转发的微博有图片,则显示转发微博布局的图片 */
        if (retweeted_status != null) {
            String sender = "@" + retweeted_status.getUser().getName() + ":";
            holder.include_retweeted_status.setVisibility(View.VISIBLE);
            holder.pwb_textview_retweeted_content.setText(sender + retweeted_status.getText());
            if (status.getPic_ids() != null) {
                setImages(holder.include_retweeted_status_image, holder.pwb_gridview_retweeted_status_image, holder.pwb_imageview_retweeted_status_image, status);
            }
        }
        holder.textview_share_bottom.setText(status.getReposts_count() == 0 ? "转发" : status.getReposts_count() + "");
        holder.textview_comment_bottom.setText(status.getComments_count() == 0 ? "评论" : status.getComments_count() + "");
        holder.textview_praise_bottom.setText(status.getAttitudes_count() == 0 ? "点赞" : status.getAttitudes_count() + "");

        /** 加载用户头像图片 */
        ImageListener listener = ImageLoader.getImageListener(holder.pwb_imageview_item_status_avatar,
                R.drawable.ic_com_sina_weibo_sdk_logo, R.drawable.pwb_avatar);
        imageLoader.get(user.getProfile_image_url(), listener);
        return convertView;
    }

    /** 加载微博图片,如果为多图的话,显示九宫格形式,否则显示单图形式 */
    public void setImages(FrameLayout imgContainer, GridView gv, NetworkImageView iv, Status status) {
        ArrayList<PicUrls> list = status.getPic_ids();

        if (list != null && list.size() > 1) {
            imgContainer.setVisibility(View.VISIBLE);
            gv.setVisibility(View.VISIBLE);
            iv.setVisibility(View.GONE);

            StatusGridViewAdapter gridViewAdapter;
            gridViewAdapter = new StatusGridViewAdapter(context, list, imageLoader, imgContainer);
            gv.setAdapter(gridViewAdapter);

        } else if (list != null && list.size() == 1) {
            imgContainer.setVisibility(View.VISIBLE);
            gv.setVisibility(View.GONE);
            iv.setVisibility(View.VISIBLE);

            iv.setTag(list.get(0).getThumbnail_pic());
            iv.setImageUrl(list.get(0).getThumbnail_pic(), imageLoader);
        } else {
            imgContainer.setVisibility(View.GONE);
        }
    }

    /** 初始化listView的每个item当中的控件 */
    public ViewHolder init(ViewHolder holder, View convertView) {
        if (holder != null) {
            holder.pwb_ll_item_status                       = (LinearLayout)        convertView.findViewById(R.id.pwb_ll_item_status);
            holder.pwb_imageview_item_status_avatar         = (ImageView)           convertView.findViewById(R.id.pwb_imageview_item_status_avatar);
            holder.pwb_rl_content                           = (RelativeLayout)      convertView.findViewById(R.id.pwb_rl_content);
            holder.pwb_textview_sender                      = (TextView)            convertView.findViewById(R.id.pwb_textview_sender);
            holder.pwb_textview_item_status_from_and_when   = (TextView)            convertView.findViewById(R.id.pwb_textview_item_status_from_and_when);

            holder.pwb_textview_content                     = (TextView)            convertView.findViewById(R.id.pwb_textview_content);
            holder.include_status_image                     = (FrameLayout)         convertView.findViewById(R.id.include_status_image);
            holder.pwb_gridview_status_image                = (GridView)            holder.include_status_image.findViewById(R.id.pwb_gridview_status_image);
            holder.pwb_imageview_status_image               = (NetworkImageView)    holder.include_status_image.findViewById(R.id.pwb_imageview_status_image);

            holder.include_retweeted_status                 = (LinearLayout)        convertView.findViewById(R.id.include_retweeted_status);
            holder.pwb_textview_retweeted_content           = (TextView)            convertView.findViewById(R.id.pwb_textview_retweeted_content);
            holder.include_retweeted_status_image           = (FrameLayout)         convertView.findViewById(R.id.include_retweeted_status_image);
            holder.pwb_gridview_retweeted_status_image      = (GridView)            holder.include_retweeted_status_image.findViewById(R.id.pwb_gridview_status_image);
            holder.pwb_imageview_retweeted_status_image     = (NetworkImageView)    holder.include_retweeted_status_image.findViewById(R.id.pwb_imageview_status_image);

            holder.pwb_ll_share_tottom                      = (LinearLayout)        convertView.findViewById(R.id.pwb_ll_share_tottom);
            holder.pwb_imageview_share_bottom               = (ImageView)           convertView.findViewById(R.id.pwb_imageview_share_bottom);
            holder.textview_share_bottom                    = (TextView)            convertView.findViewById(R.id.textview_share_bottom);
            holder.pwb_ll_comment_tottom                    = (LinearLayout)        convertView.findViewById(R.id.pwb_ll_comment_tottom);
            holder.pwb_imageview_comment_bottom             = (ImageView)           convertView.findViewById(R.id.pwb_imageview_comment_bottom);
            holder.textview_comment_bottom                  = (TextView)            convertView.findViewById(R.id.textview_comment_bottom);
            holder.pwb_ll_praise_tottom                     = (LinearLayout)        convertView.findViewById(R.id.pwb_ll_praise_tottom);
            holder.pwb_imageview_praise_bottom              = (ImageView)           convertView.findViewById(R.id.pwb_imageview_praise_bottom);
            holder.textview_praise_bottom                   = (TextView)            convertView.findViewById(R.id.textview_praise_bottom);

            return holder;
        }
        return null;
    }

    /** 重置holder控件,将图片布局和转发部分设置为隐藏、其他布局设置为空 */
    public void resetViewHolder(ViewHolder holder) {
        holder.pwb_textview_sender.setText(null);
        holder.pwb_textview_item_status_from_and_when.setText(null);
        holder.pwb_textview_content.setText(null);
        holder.include_status_image.setVisibility(View.GONE);
//        holder.pwb_gridview_status_image.setAdapter(null);
        holder.pwb_imageview_status_image.setImageDrawable(null);
        holder.include_retweeted_status.setVisibility(View.GONE);
        holder.pwb_textview_retweeted_content.setText(null);
        holder.include_retweeted_status_image.setVisibility(View.GONE);
//        holder.pwb_gridview_retweeted_status_image.setAdapter(null);
        holder.pwb_imageview_retweeted_status_image.setImageDrawable(null);
    }

    /** 每个item的控件 */
    public static class ViewHolder {

        public  LinearLayout        pwb_ll_item_status;                         // 单条微博的linearlayout布局
        public  ImageView           pwb_imageview_item_status_avatar;           // 微博发布者头像
        public  RelativeLayout      pwb_rl_content;                             // 微博发布者昵称的relativelayout布局
        public  TextView            pwb_textview_sender;                        // 微博发布者昵称
        public  TextView            pwb_textview_item_status_from_and_when;     // 微博来源

        public  TextView            pwb_textview_content;                       // 微博正文
        public  FrameLayout         include_status_image;                       // 微博的图片布局
        public  GridView            pwb_gridview_status_image;                  // 微博的九宫格图片部分
        public  NetworkImageView    pwb_imageview_status_image;                 // 微博的图片部分

        public  LinearLayout        include_retweeted_status;                   // 转发微博内容的布局
        public  TextView            pwb_textview_retweeted_content;             // 转发微博的内容
        public  FrameLayout         include_retweeted_status_image;             // 转发微博的图片布局
        public  GridView            pwb_gridview_retweeted_status_image;        // 转发微博的九宫格图片部分
        public  NetworkImageView    pwb_imageview_retweeted_status_image;       // 转发微博的图片部分

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
