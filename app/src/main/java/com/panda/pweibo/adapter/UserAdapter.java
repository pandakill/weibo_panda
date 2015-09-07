package com.panda.pweibo.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.panda.pweibo.R;
import com.panda.pweibo.models.UserItem;

import java.util.List;

/**
 * 个人中心、发现的适配器
 *
 * Created by Administrator on 2015/8/31:15:28.
 */
public class UserAdapter extends BaseAdapter {

    private List<UserItem>      mUserItemList;
    private Context             mContext;

    public UserAdapter (Context context, List<UserItem> userItemList) {
        mContext = context;
        mUserItemList = userItemList;
    }

    @Override
    public int getCount() {
        return mUserItemList.size();
    }

    @Override
    public UserItem getItem(int position) {
        return mUserItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_user, null);
            holder = new ViewHolder();

            initViewHolder(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            resetViewHolder(holder);
        }

        final UserItem item = getItem(position);

        if (item.isShowTopDivider()) {
            holder.v_divider.setVisibility(View.VISIBLE);
        }

        holder.tv_subhead.setText(item.getSubhead());
        holder.tv_caption.setText(item.getCaption());
        holder.iv_left.setImageResource(item.getLeftImg());

        // 获取屏幕宽度大小、设置bitmap的宽度
        convertView.setDrawingCacheEnabled(true);
        WindowManager windowManager = ((Activity)mContext).getWindowManager();
        Display display = windowManager.getDefaultDisplay();

        // 如果没有调用这个方法，得到的bitmap为null
        convertView.measure(View.MeasureSpec.makeMeasureSpec(display.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(256, View.MeasureSpec.UNSPECIFIED));
        // 设置布局的尺寸和位置
        convertView.layout(0, 0, convertView.getMeasuredWidth(), convertView.getMeasuredHeight());
        // 获得绘图缓存中的Bitmap
        convertView.buildDrawingCache();
        Bitmap bitmap = convertView.getDrawingCache();

        Canvas canvas = new Canvas(bitmap);
        convertView.draw(canvas);

        return convertView;
    }

    private void initViewHolder(ViewHolder holder, View converView) {
        holder.v_divider    = converView.findViewById(R.id.v_divider);

        holder.iv_left      = (ImageView) converView.findViewById(R.id.iv_left);
        holder.tv_caption   = (TextView) converView.findViewById(R.id.tv_caption);
        holder.tv_subhead   = (TextView) converView.findViewById(R.id.tv_subhead);
    }

    private void resetViewHolder(ViewHolder holder){
        holder.v_divider.setVisibility(View.GONE);
    }

    /** 每个item的控件元素 */
    protected class ViewHolder{

        public View         v_divider;      //分割线

        public ImageView    iv_left;
        public TextView     tv_subhead;
        public TextView     tv_caption;

    }
}
