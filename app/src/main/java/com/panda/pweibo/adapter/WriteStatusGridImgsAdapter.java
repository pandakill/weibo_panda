package com.panda.pweibo.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.volley.toolbox.NetworkImageView;
import com.panda.pweibo.R;
import com.panda.pweibo.widget.WrapHeightGridView;

import java.util.List;

/**
 * 发微博时候的图片适配器
 *
 * Created by Administrator on 2015/9/1:14:04.
 */
public class WriteStatusGridImgsAdapter extends BaseAdapter {

    private Context             mContext;
    private List<Uri>           mUriList;
    private WrapHeightGridView  mGridView;

    public WriteStatusGridImgsAdapter(Context context, List<Uri> uriList, WrapHeightGridView gridView) {
        mContext    =   context;
        mUriList    =   uriList;
        mGridView   =   gridView;
    }

    @Override
    public int getCount() {
        return mUriList.size();
    }

    @Override
    public Uri getItem(int position) {
        return mUriList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_grid_image, null);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.pwb_iv_image_browser
                = (NetworkImageView) convertView.findViewById(R.id.pwb_iv_image_browser);
        holder.pwb_iv_delete = (ImageView) convertView.findViewById(R.id.pwb_iv_delete);

        int horizontalSpacing = mGridView.getHorizontalSpacing();
        int width = (mGridView.getWidth() - horizontalSpacing * 2
                - mGridView.getPaddingLeft() - mGridView.getPaddingRight()) / 3;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
        holder.pwb_iv_image_browser.setLayoutParams(params);

//        if(position < getCount() - 1) {
        // set data
        Uri item = getItem(position);
        holder.pwb_iv_image_browser.setImageURI(item);

        // 点击右上角的删除,则将该图片移除
        holder.pwb_iv_delete.setVisibility(View.VISIBLE);
        holder.pwb_iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUriList.remove(position);
                notifyDataSetChanged();
            }
        });
//        } else {
//            holder.pwb_iv_image_browser.setImageResource(R.drawable.pwb_compose_pic_add);
//            holder.pwb_iv_delete.setVisibility(View.GONE);
//        }

        return convertView;
    }

    protected class ViewHolder {
        public NetworkImageView pwb_iv_image_browser;
        public ImageView        pwb_iv_delete;
    }
}
