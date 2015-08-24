package com.panda.pweibo.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.panda.pweibo.R;
import com.panda.pweibo.models.PicUrls;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/8/24:16:43.
 */
public class StatusGridViewAdapter extends BaseAdapter{

    private Context context;
    private ArrayList<PicUrls> listPicUrls;
    private ImageLoader imageLoader;
    private View inflate;

    public StatusGridViewAdapter(Context context, ArrayList<PicUrls> listPicUrls, ImageLoader imageLoader, View inflate) {
        this.context     = context;
        this.listPicUrls = listPicUrls;
        this.imageLoader = imageLoader;
        this.inflate = inflate;
    }

    @Override
    public int getCount() {
        return listPicUrls.size();
    }

    @Override
    public PicUrls getItem(int position) {
        return listPicUrls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            if (inflate.findViewById(R.id.include_status_image) != null) {
                convertView = View.inflate(context, R.layout.include_status_image, null);
            } else if (inflate.findViewById(R.id.include_retweeted_status_image) != null) {
                convertView = View.inflate(context, R.layout.include_status_image, null);
            }
            init(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        GridView gv = (GridView) parent;
        int horizontalSpacing = gv.getHorizontalSpacing();
        int numColumns = gv.getNumColumns();
        int itemWidth = (gv.getWidth() - (numColumns-1) * horizontalSpacing
                - gv.getPaddingLeft() - gv.getPaddingRight()) /numColumns;

        LayoutParams params = new LayoutParams(itemWidth, itemWidth);
        holder.imageView.setLayoutParams(params);

        PicUrls urls = getItem(position);
        holder.imageView.setTag(urls.getThumbnail_pic());
        holder.imageView.setImageUrl(urls.getThumbnail_pic(), imageLoader);

        return convertView;
    }

    public void init(ViewHolder holder,View convertView) {
        holder.imageView = (NetworkImageView) convertView.findViewById(R.id.pwb_imageview_status_image);
    }

    public static class ViewHolder {
        public NetworkImageView imageView;
    }
}
