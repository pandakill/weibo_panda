package com.panda.pweibo.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.panda.pweibo.R;
import com.panda.pweibo.models.PicUrls;
import com.panda.pweibo.utils.ToastUtils;
import com.panda.pweibo.widget.WrapHeightGridView;

import java.util.ArrayList;

/**
 * 微博九宫图图片加载适配器
 *
 * Created by Administrator on 2015/8/24:16:43.
 */
public class StatusGridViewAdapter extends BaseAdapter{

    private Context             mContext;
    private ArrayList<PicUrls>  mPicUrlList;
    private ImageLoader         mImageLoader;

    public StatusGridViewAdapter(Context context, ArrayList<PicUrls> listPicUrls,
                                 ImageLoader imageLoader) {
        mContext     = context;
        mPicUrlList = listPicUrls;
        mImageLoader = imageLoader;
    }

    @Override
    public int getCount() {
        return mPicUrlList.size();
    }

    @Override
    public PicUrls getItem(int position) {
        return mPicUrlList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        WrapHeightGridView gv = (WrapHeightGridView) parent;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.include_grid_image, null);
            init(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // 设置gridveiw当中每个item的高度和宽度
        int horizontalSpacing = gv.getHorizontalSpacing();
        int numColumns = gv.getNumColumns();
        int itemWidth = (gv.getWidth() - (numColumns-1) * horizontalSpacing
                - gv.getPaddingLeft() - gv.getPaddingRight()) /numColumns;

        LayoutParams params = new LayoutParams(itemWidth, itemWidth);
        holder.imageView.setLayoutParams(params);

        // 加载图片
        final PicUrls urls = getItem(position);
        holder.imageView.setTag(urls.getThumbnail_pic());
        holder.imageView.setImageUrl(urls.getThumbnail_pic(), mImageLoader);

        return convertView;
    }

    /** 初始化控件 */
    public void init(ViewHolder holder,View convertView) {
        holder.imageView =
                (NetworkImageView) convertView.findViewById(R.id.pwb_imageview_status_image);
    }

    /** 设置gridview控件内容 */
    protected static class ViewHolder {
        public NetworkImageView imageView;
    }
}
