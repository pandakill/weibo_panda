package com.panda.pweibo.adapter;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.NetworkImageView;
import com.panda.pweibo.R;
import com.panda.pweibo.models.Comment;
import com.panda.pweibo.models.Status;
import com.panda.pweibo.utils.DateUtils;
import com.panda.pweibo.utils.StringUtils;

import java.util.List;

/**
 * 消息列表的listview适配器
 *
 * Created by Administrator on 2015/8/27:14:28.
 */
public class MessageAdapter extends BaseAdapter {

    private List<Comment>               mCommentList;
    private Context                     mContext;
    private ImageLoader                 mImageLoader;

    public MessageAdapter(Context context, List<Comment> commentList, ImageLoader imageLoader) {
        mCommentList = commentList;
        mContext     = context;
        mImageLoader = imageLoader;
    }

    @Override
    public int getCount() {
        return mCommentList.size();
    }

    @Override
    public Comment getItem(int position) {
        return mCommentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_message, null);
            initViewHolder(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            resetViewHolser(holder);
        }

        final Comment   comment = getItem(position);
        final Status    status  = comment.getStatus();

        // 填充消息头部的作者信息
        ImageListener imageListener;
        imageListener = ImageLoader.getImageListener(holder.pwb_imageview_item_status_avatar,
                R.drawable.ic_com_sina_weibo_sdk_logo, R.drawable.pwb_avatar);
        mImageLoader.get(comment.getUser().getProfile_image_url(), imageListener);

        holder.pwb_textview_sender.setText(comment.getUser().getName());
        holder.pwb_textview_item_status_from_and_when.setText(
                new DateUtils().String2Date(comment.getCreated_at())
                        + " 来自 " + Html.fromHtml(comment.getSource()));

        // 填充消息正文
        holder.pwb_tv_message_content.setText(StringUtils.getWeiboContent(
                mContext, holder.pwb_tv_message_content, comment.getText()));

        // 如果消息是直接回复微博的,填充微博的参数
        if (comment.getReply_comment() == null) {
            holder.include_small_status.setVisibility(View.VISIBLE);
            holder.pwb_ll_retweet_message.setVisibility(View.GONE);

            initImage(status, holder.pwb_iv_small_status_image);

            holder.pwb_tv_retweeted_small_status_name.setText(status.getUser().getName());
            holder.pwb_tv_small_status_content.setText(status.getText());
        }

        // 如果消息是回复评论的,填充回复部分的参数
        if (comment.getReply_comment() != null) {
            holder.include_small_status.setVisibility(View.GONE);
            holder.pwb_ll_retweet_message.setVisibility(View.VISIBLE);

            initImage(status, holder.pwb_iv_retweeted_small_status_image);

            holder.pwb_tv_retweeted_small_status_name.setText(status.getUser().getName());
            holder.pwb_tv_small_status_content.setText(status.getText());
        }

        return convertView;
    }

    /** 如果微博有图片,则加载第一张图片,否则加载用户头像 */
    private void initImage(Status status, NetworkImageView networkImageView) {

        if (status.getPic_ids() != null) {
            networkImageView.setTag(
                    status.getPic_ids().get(0).getThumbnail_pic());
            networkImageView.setImageUrl(
                    status.getPic_ids().get(0).getThumbnail_pic(), mImageLoader);
        } else {
            networkImageView.setTag(
                    status.getUser().getProfile_image_url());
            networkImageView.setImageUrl(
                    status.getUser().getProfile_image_url(), mImageLoader);
        }
    }

    /** 初始化item的控件 */
    private void initViewHolder(ViewHolder holder, View convertView) {
        holder.pwb_imageview_item_status_avatar =
                (ImageView) convertView.findViewById(R.id.pwb_imageview_item_status_avatar);
        holder.pwb_textview_sender =
                (TextView) convertView.findViewById(R.id.pwb_textview_sender);
        holder.pwb_textview_item_status_from_and_when =
                (TextView) convertView.findViewById(R.id.pwb_textview_item_status_from_and_when);

        holder.pwb_tv_message_content =
                (TextView) convertView.findViewById(R.id.pwb_tv_message_content);

        holder.include_small_status =
                (LinearLayout) convertView.findViewById(R.id.include_small_status);
        holder.pwb_iv_small_status_image =
                (NetworkImageView) holder.include_small_status.findViewById(R.id.pwb_iv_small_status_image);
        holder.pwb_tv_small_status_name =
                (TextView) holder.include_small_status.findViewById(R.id.pwb_tv_small_status_name);
        holder.pwb_tv_small_status_content =
                (TextView) holder.include_small_status.findViewById(R.id.pwb_tv_small_status_content);


        holder.pwb_ll_retweet_message =
                (LinearLayout) convertView.findViewById(R.id.pwb_ll_retweet_message);
        holder.pwb_tv_retweeted_message_content =
                (TextView) convertView.findViewById(R.id.pwb_tv_retweeted_message_content);
        holder.include_retweeted_small_status =
                (LinearLayout) convertView.findViewById(R.id.include_retweeted_small_status);
        holder.pwb_iv_retweeted_small_status_image =
                (NetworkImageView) holder.pwb_tv_retweeted_message_content.findViewById(R.id.pwb_iv_small_status_image);
        holder.pwb_tv_retweeted_small_status_name =
                (TextView) holder.pwb_tv_retweeted_message_content.findViewById(R.id.pwb_tv_small_status_name);
        holder.pwb_tv_retweeted_small_status_content =
                (TextView) holder.pwb_tv_retweeted_message_content.findViewById(R.id.pwb_tv_small_status_content);
    }

    /** 重置两个布局的可见性 */
    private void resetViewHolser(ViewHolder holder) {
        holder.include_small_status.setVisibility(View.VISIBLE);
        holder.pwb_ll_retweet_message.setVisibility(View.GONE);
    }

    /** 每个item的控件 */
    protected class ViewHolder {
        // 头像部分
        public ImageView        pwb_imageview_item_status_avatar;
        public TextView         pwb_textview_sender;
        public TextView         pwb_textview_item_status_from_and_when;

        // 消息正文部分
        public TextView         pwb_tv_message_content;

        // 消息的微博部分
        public LinearLayout     include_small_status;
        public NetworkImageView pwb_iv_small_status_image;
        public TextView         pwb_tv_small_status_name;
        public TextView         pwb_tv_small_status_content;

        // 消息的转发微博部分
        public LinearLayout     pwb_ll_retweet_message;
        public TextView         pwb_tv_retweeted_message_content;
        public LinearLayout     include_retweeted_small_status;
        public NetworkImageView pwb_iv_retweeted_small_status_image;
        public TextView         pwb_tv_retweeted_small_status_name;
        public TextView         pwb_tv_retweeted_small_status_content;
    }
}
