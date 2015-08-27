package com.panda.pweibo.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.panda.pweibo.R;
import com.panda.pweibo.models.Message;

import java.util.List;

/**
 * Created by Administrator on 2015/8/27:10:06.
 */
public class MessageCenterAdapter extends BaseAdapter {

    private List<Message>   listMessage;
    private Context         context;

    public MessageCenterAdapter(Context context, List<Message> listMessage) {
        this.context        =   context;
        this.listMessage    =   listMessage;
    }

    @Override
    public int getCount() {
        return listMessage.size();
    }

    @Override
    public Message getItem(int position) {
        return listMessage.get(position);
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
            convertView = View.inflate(context, R.layout.item_message_center, null);
            initView(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        initView(holder, convertView);

        final Message message = getItem(position);
        holder.pwb_iv_messagecenter_left.setImageResource(message.getImage());
        holder.pwb_tv_messagecenter_content.setText(message.getMessage());

        return convertView;
    }

    private void initView(ViewHolder holder, View convertView) {
        holder.pwb_iv_messagecenter_left =
                (ImageView) convertView.findViewById(R.id.pwb_iv_messagecenter_left);
        holder.pwb_tv_messagecenter_content =
                (TextView) convertView.findViewById(R.id.pwb_tv_messagecenter_content);
    }

    private class ViewHolder {
        public ImageView pwb_iv_messagecenter_left;
        public TextView  pwb_tv_messagecenter_content;
    }
}
