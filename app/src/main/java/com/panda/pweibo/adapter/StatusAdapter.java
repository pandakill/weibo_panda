package com.panda.pweibo.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.Html;
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


import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.NetworkImageView;
import com.panda.pweibo.activity.StatusDetailActivity;
import com.panda.pweibo.activity.WriteCommentActivity;
import com.panda.pweibo.R;
import com.panda.pweibo.models.PicUrls;
import com.panda.pweibo.models.Status;
import com.panda.pweibo.models.User;
import com.panda.pweibo.utils.DateUtils;
import com.panda.pweibo.utils.StringUtils;
import com.panda.pweibo.utils.ToastUtils;
import com.panda.pweibo.widget.WrapHeightGridView;

import java.util.ArrayList;
import java.util.List;

/**
 * frag_home的适配器,输出微博
 *
 * Created by Administrator on 2015/8/22:15:22.
 */
public class StatusAdapter extends BaseAdapter {

    private Context                     mContext;
    private List<Status>                mStatusList;
    private ImageLoader                 mImageLoader;

    public StatusAdapter(Context context, List<Status> listStatus, ImageLoader imageLoader) {
        mContext        = context;
        mStatusList     = listStatus;
        mImageLoader    = imageLoader;
    }

    @Override
    public int getCount() {
        return mStatusList.size();
    }

    @Override
    public Object getItem(int position) {
        return mStatusList.get(position);
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
            //convertView = View.inflate(context, R.layout.item_status, null);
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_status,null);
            // 调用init方法,初始化控件
            init(holder, convertView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            resetViewHolder(holder);
        }

        final Status    status  = (Status) getItem(position);
        User            user    = status.getUser();
        final Status retweeted_status = status.getRetweeted_status();

        holder.pwb_textview_sender.setText(user.getScreen_name());
        holder.pwb_textview_item_status_from_and_when.setText(
                new DateUtils().String2Date(status.getCreated_at())
                        + " 来自 " + Html.fromHtml(status.getSource()));
        holder.pwb_textview_content.setText(StringUtils.getWeiboContent(mContext,
                holder.pwb_textview_content, status.getText()));

        // 如果微博有图片,且不是转发的微博,则在正文部分显示图片
        if ( status.getPic_ids() != null && status.getRetweeted_status() == null) {
            setImages(holder.include_status_image,
                    holder.pwb_gridview_status_image,
                    holder.pwb_imageview_status_image,
                    status);
        }
        // 如果有转发的微博,则显示转发微博内容;如果转发的微博有图片,则显示转发微博布局的图片
        if (retweeted_status != null) {

            holder.include_retweeted_status.setVisibility(View.VISIBLE);

            String retweeted_content = "@" + retweeted_status.getUser().getName() + ":"
                                          + retweeted_status.getText();

            holder.pwb_textview_retweeted_content.setText(
                    StringUtils.getWeiboContent(mContext, holder.pwb_textview_retweeted_content,
                            retweeted_content));

            if (retweeted_status.getPic_ids() != null) {
                setImages(holder.include_retweeted_status_image,
                        holder.pwb_gridview_retweeted_status_image,
                        holder.pwb_imageview_retweeted_status_image,
                        retweeted_status);
            }
        }

        holder.textview_share_bottom.setText(
                status.getReposts_count() == 0 ? "转发" : status.getReposts_count() + "");
        holder.textview_comment_bottom.setText(
                status.getComments_count() == 0 ? "评论" : status.getComments_count() + "");
        holder.textview_praise_bottom.setText(
                status.getAttitudes_count() == 0 ? "点赞" : status.getAttitudes_count() + "");

        // 设置每条微博的监听器
        holder.pwb_ll_item_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, StatusDetailActivity.class);
                intent.putExtra("status", status);

                mContext.startActivity(intent);
            }
        });

        // 设置转发微博的监听器
        holder.include_retweeted_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assert retweeted_status != null;
                ToastUtils.showToast(mContext, "转发微博" + retweeted_status.getUser().getName(), Toast.LENGTH_SHORT);
            }
        });

        // 设置分享按钮监听器
        holder.pwb_ll_share_tottom.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastUtils.showToast(mContext, "转发按钮被点击", Toast.LENGTH_LONG);
                    }
                });

        // 设置评论按钮监听器
        // 如果有评论,则跳转至详情页
        // 如果评论为空,则跳转至写评论页
        holder.pwb_ll_comment_tottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status.getComments_count() == 0) {
                    Intent intent = new Intent(mContext, WriteCommentActivity.class);
                    intent.putExtra("status", status);
                    mContext.startActivity(intent);
                } else {
                    Intent intent = new Intent(mContext, StatusDetailActivity.class);
                    intent.putExtra("status", status);
                    mContext.startActivity(intent);
                }
            }
        });

        // 设置点赞监听器
        holder.pwb_ll_praise_tottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showToast(mContext, "点赞按钮被点击", Toast.LENGTH_SHORT);
            }
        });


        // 加载用户头像图片
        ImageListener listener;
        listener = ImageLoader.getImageListener(holder.pwb_imageview_item_status_avatar,
                R.drawable.ic_com_sina_weibo_sdk_logo, R.drawable.pwb_avatar);
        mImageLoader.get(user.getProfile_image_url(), listener);

        return convertView;
    }

    // 加载微博图片,如果为多图的话,显示九宫格形式,否则显示单图形式
    public void setImages(FrameLayout imgContainer, GridView gv,
                          NetworkImageView iv, Status status) {
        ArrayList<PicUrls> list = status.getPic_ids();

        if (list != null && list.size() > 1) {
            imgContainer.setVisibility(View.VISIBLE);
            gv.setVisibility(View.VISIBLE);
            iv.setVisibility(View.GONE);

            StatusGridViewAdapter gridViewAdapter;
            gridViewAdapter = new StatusGridViewAdapter(mContext, list, mImageLoader);
            gv.setAdapter(gridViewAdapter);

        } else if (list != null && list.size() == 1) {
            imgContainer.setVisibility(View.VISIBLE);
            gv.setVisibility(View.GONE);
            iv.setVisibility(View.VISIBLE);

            iv.setTag(list.get(0).getThumbnail_pic());
            iv.setImageUrl(list.get(0).getThumbnail_pic(), mImageLoader);
        } else {
            imgContainer.setVisibility(View.GONE);
        }
    }

    /** 初始化listView的每个item当中的控件 */
    public ViewHolder init(ViewHolder holder, View convertView) {
        if (holder != null) {
            holder.pwb_ll_item_status =
                    (LinearLayout) convertView.findViewById(R.id.pwb_ll_item_status);
            holder.pwb_imageview_item_status_avatar =
                    (ImageView) convertView.findViewById(R.id.pwb_imageview_item_status_avatar);
            holder.pwb_rl_content =
                    (RelativeLayout) convertView.findViewById(R.id.pwb_rl_content);
            holder.pwb_textview_sender =
                    (TextView) convertView.findViewById(R.id.pwb_textview_sender);
            holder.pwb_textview_item_status_from_and_when =
                    (TextView) convertView.findViewById(R.id.pwb_textview_item_status_from_and_when);

            holder.pwb_textview_content =
                    (TextView) convertView.findViewById(R.id.pwb_textview_content);
            holder.include_status_image =
                    (FrameLayout) convertView.findViewById(R.id.include_status_image);
            holder.pwb_gridview_status_image =
                    (WrapHeightGridView) holder.include_status_image
                            .findViewById(R.id.pwb_gridview_status_image);
            holder.pwb_imageview_status_image =
                    (NetworkImageView) holder.include_status_image
                            .findViewById(R.id.pwb_imageview_status_image);

            holder.include_retweeted_status =
                    (LinearLayout) convertView.findViewById(R.id.include_retweeted_status);
            holder.pwb_textview_retweeted_content =
                    (TextView) convertView.findViewById(R.id.pwb_textview_retweeted_content);
            holder.include_retweeted_status_image =
                    (FrameLayout) convertView.findViewById(R.id.include_retweeted_status_image);
            holder.pwb_gridview_retweeted_status_image =
                    (WrapHeightGridView) holder.include_retweeted_status_image
                            .findViewById(R.id.pwb_gridview_status_image);
            holder.pwb_imageview_retweeted_status_image =
                    (NetworkImageView) holder.include_retweeted_status_image
                            .findViewById(R.id.pwb_imageview_status_image);

            holder.pwb_ll_share_tottom =
                    (LinearLayout) convertView.findViewById(R.id.pwb_ll_share_tottom);
            holder.pwb_imageview_share_bottom =
                    (ImageView) convertView.findViewById(R.id.pwb_imageview_share_bottom);
            holder.textview_share_bottom =
                    (TextView) convertView.findViewById(R.id.textview_share_bottom);
            holder.pwb_ll_comment_tottom =
                    (LinearLayout) convertView.findViewById(R.id.pwb_ll_comment_tottom);
            holder.pwb_imageview_comment_bottom =
                    (ImageView) convertView.findViewById(R.id.pwb_imageview_comment_bottom);
            holder.textview_comment_bottom =
                    (TextView) convertView.findViewById(R.id.textview_comment_bottom);
            holder.pwb_ll_praise_tottom =
                    (LinearLayout) convertView.findViewById(R.id.pwb_ll_praise_tottom);
            holder.pwb_imageview_praise_bottom =
                    (ImageView) convertView.findViewById(R.id.pwb_imageview_praise_bottom);
            holder.textview_praise_bottom =
                    (TextView) convertView.findViewById(R.id.textview_praise_bottom);

            return holder;
        }
        return null;
    }

    /** 重置holder控件,将图片布局和转发部分设置为隐藏、其他布局设置为空 */
    public void resetViewHolder(ViewHolder holder) {
        holder.include_status_image.setVisibility(View.GONE);
        holder.include_retweeted_status.setVisibility(View.GONE);
        holder.include_retweeted_status_image.setVisibility(View.GONE);
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
        public  WrapHeightGridView  pwb_gridview_status_image;                  // 微博的九宫格图片部分
        public  NetworkImageView    pwb_imageview_status_image;                 // 微博的图片部分

        public  LinearLayout        include_retweeted_status;                   // 转发微博内容的布局
        public  TextView            pwb_textview_retweeted_content;             // 转发微博的内容
        public  FrameLayout         include_retweeted_status_image;             // 转发微博的图片布局
        public  WrapHeightGridView  pwb_gridview_retweeted_status_image;        // 转发微博的九宫格图片部分
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
