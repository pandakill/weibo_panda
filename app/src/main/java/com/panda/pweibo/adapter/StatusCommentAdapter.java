package com.panda.pweibo.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.panda.pweibo.R;
import com.panda.pweibo.activity.StatusDetailActivity;
import com.panda.pweibo.activity.WriteCommentActivity;
import com.panda.pweibo.constants.Code;
import com.panda.pweibo.models.Comment;
import com.panda.pweibo.models.User;
import com.panda.pweibo.utils.DateUtils;
import com.panda.pweibo.utils.StringUtils;
import com.panda.pweibo.utils.ToastUtils;

import java.util.List;

/**
 * 评论列表的适配器
 *
 * Created by Administrator on 2015/8/26:11:58.
 */
public class StatusCommentAdapter extends BaseAdapter {

    private Context         mContext;
    private List<Comment>   mCommentList;
    private ImageLoader     mImageLoader;

    public StatusCommentAdapter(Context context, List<Comment> listComments,
                                ImageLoader imageLoader) {
        mContext        =   context;
        mCommentList   =   listComments;
        mImageLoader    =   imageLoader;
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
            convertView = View.inflate(mContext, R.layout.item_comment, null);
            initViewHolder(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Comment comment = getItem(position);
        final User    user    = comment.getUser();

        // 加载用户头像
        ImageListener listener;
        listener = ImageLoader.getImageListener(holder.pwb_imageview_item_status_avatar,
                R.drawable.pwb_test_image, R.drawable.pwb_avatar);
        mImageLoader.get(user.getProfile_image_url(), listener);

        holder.pwb_textview_sender.setText(user.getName());
        holder.pwb_textview_item_status_from_and_when.setText(
                new DateUtils().String2Date(comment.getCreated_at()));
        holder.pwb_textview_comment.setText(StringUtils.getWeiboContent(
                mContext, holder.pwb_textview_comment, comment.getText()));

        // 评论的点击事件监听器
        holder.pwb_ll_comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showToast(mContext, "回复评论", Toast.LENGTH_SHORT);
                Intent intent = new Intent(mContext, WriteCommentActivity.class);
                intent.putExtra("comment", comment);
                intent.putExtra("type", Code.REPLY_COMMENT);
                ((StatusDetailActivity) mContext).startActivityForResult(
                        intent, Code.REQUEST_CODE_WRITE_COMMENT_BACK_TO_DETAIL);
            }
        });

        // 将每个item通过canvas画布画出
        convertView.measure(View.MeasureSpec.makeMeasureSpec(256, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(256, View.MeasureSpec.EXACTLY));
        // 设置布局的尺寸和位置
        convertView.layout(0, 0, convertView.getMeasuredWidth(), convertView.getMeasuredHeight());
        // 生成bitmap
        Bitmap bitmap = Bitmap.createBitmap(convertView.getWidth(), convertView.getHeight(),
                Bitmap.Config.RGB_565);
        // 利用bitmap生成画布
        Canvas canvas = new Canvas(bitmap);
        // 把view中的内容绘制在画布上
        convertView.draw(canvas);

        return convertView;
    }

    private void initViewHolder(ViewHolder holder, View converView) {
        holder.pwb_ll_comments =
                (LinearLayout) converView.findViewById(R.id.pwb_ll_comments);
        holder.pwb_imageview_item_status_avatar =
                (ImageView) converView.findViewById(R.id.pwb_imageview_item_status_avatar);
        holder.pwb_textview_sender =
                (TextView) converView.findViewById(R.id.pwb_textview_sender);
        holder.pwb_textview_item_status_from_and_when =
                (TextView) converView.findViewById(R.id.pwb_textview_item_status_from_and_when);
        holder.pwb_textview_comment =
                (TextView) converView.findViewById(R.id.pwb_textview_comment);
    }

    /** 控件 */
    protected static class ViewHolder {
        public LinearLayout     pwb_ll_comments;
        public ImageView        pwb_imageview_item_status_avatar;
        public TextView         pwb_textview_sender;
        public TextView         pwb_textview_item_status_from_and_when;
        public TextView         pwb_textview_comment;
    }
}
