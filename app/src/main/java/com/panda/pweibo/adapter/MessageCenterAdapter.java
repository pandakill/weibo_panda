package com.panda.pweibo.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.panda.pweibo.R;
import com.panda.pweibo.activity.MessageActivity;
import com.panda.pweibo.models.Message;
import com.panda.pweibo.utils.ToastUtils;

import java.util.List;

/**
 * 消息中心的适配器
 *
 * Created by Administrator on 2015/8/27:10:06.
 */
public class MessageCenterAdapter extends BaseAdapter {

    private List<Message>   mMessageList;
    private Context         mContext;

    public MessageCenterAdapter(Context context, List<Message> listMessage) {
        mContext        =   context;
        mMessageList    =   listMessage;
    }

    @Override
    public int getCount() {
        return mMessageList.size();
    }

    @Override
    public Message getItem(int position) {
        return mMessageList.get(position);
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
            convertView = View.inflate(mContext, R.layout.item_message_center, null);
            initView(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        initView(holder, convertView);

        final Message message = getItem(position);
        holder.pwb_iv_messagecenter_left.setImageResource(message.getImage());
        holder.pwb_tv_messagecenter_content.setText(message.getMessage());

        initListener(holder, message);

        return convertView;
    }

    private void initView(ViewHolder holder, View convertView) {
        holder.pwb_ll_message =
                (LinearLayout) convertView.findViewById(R.id.pwb_ll_message);
        holder.pwb_iv_messagecenter_left =
                (ImageView) convertView.findViewById(R.id.pwb_iv_messagecenter_left);
        holder.pwb_tv_messagecenter_content =
                (TextView) convertView.findViewById(R.id.pwb_tv_messagecenter_content);
    }

    private void initListener(ViewHolder holder, Message message) {
        final Intent[] intent = new Intent[1];
        switch (message.getImage()) {
            case R.drawable.pwb_messagescenter_comments:
                holder.pwb_ll_message.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent[0] = new Intent(mContext, MessageActivity.class);
                        mContext.startActivity(intent[0]);
                    }
                });
                break;

            case R.drawable.pwb_messagescenter_at:
                holder.pwb_ll_message.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastUtils.showToast(mContext, "@我的", Toast.LENGTH_SHORT);
                    }
                });
                break;

            case R.drawable.pwb_messagescenter_good:
                holder.pwb_ll_message.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastUtils.showToast(mContext, "赞", Toast.LENGTH_SHORT);
                    }
                });
                break;

            case R.drawable.pwb_messagescenter_messagebox:
                holder.pwb_ll_message.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastUtils.showToast(mContext, "订阅消息", Toast.LENGTH_SHORT);
                    }
                });
                break;

            default:
                break;
        }
    }

    protected class ViewHolder {
        public LinearLayout pwb_ll_message;
        public ImageView    pwb_iv_messagecenter_left;
        public TextView     pwb_tv_messagecenter_content;
    }
}
