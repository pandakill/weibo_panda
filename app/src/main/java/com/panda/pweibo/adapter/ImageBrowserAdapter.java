package com.panda.pweibo.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.panda.pweibo.R;
import com.panda.pweibo.models.PicUrls;
import com.panda.pweibo.widget.TouchNetworkImageView;

import java.util.ArrayList;

/**
 * 图片浏览器的适配器
 * 
 * Created by Administrator on 2015/09/01.
 */
public class ImageBrowserAdapter extends PagerAdapter {

    private Activity            mContext;
    private ArrayList<PicUrls>  mPicUrlsList;
    private ArrayList<View>     mPicViewList;
    private ImageLoader         mImageLoader;

    public ImageBrowserAdapter(Activity context, ArrayList<PicUrls> picUrlsList, ImageLoader imageLoader) {
        mContext = context;
        mPicUrlsList = picUrlsList;
        mImageLoader = imageLoader;
        initImage();
    }

    /** 填充显示图片的页面布局 */
    private void initImage() {
        mPicViewList = new ArrayList<>();

        for (int i = 0; i < mPicUrlsList.size(); i ++) {
            View view = View.inflate(mContext, R.layout.item_grid_image, null);
            mPicViewList.add(view);
        }
    }

    @Override
    public int getCount() {
        if (mPicUrlsList.size() > 1) {
            return  Integer.MAX_VALUE;
        }
        return mPicUrlsList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    @Override
    public View instantiateItem(ViewGroup container, int position) {

        int index = position % mPicUrlsList.size();
        PicUrls picUrl = mPicUrlsList.get(index);

        String url = picUrl.IsShowOriImg() ? picUrl.getOriginal_pic() : picUrl.getBmiddle_pic();

        ViewHolder holder = new ViewHolder();
        holder.pwb_sv_item_image = (ScrollView) View.inflate(mContext, R.layout.item_grid_image, null);
        holder.pwb_iv_image_browser
                = (TouchNetworkImageView) holder.pwb_sv_item_image.findViewById(R.id.pwb_iv_image_browser);

        // 获取屏幕宽高
        DisplayMetrics metric = new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay().getMetrics(metric);
        final int screenHeight = metric.heightPixels;
        final int screenWidth = metric.widthPixels;

        // 加载图片
        holder.pwb_iv_image_browser.setImageUrl(url, mImageLoader);

        // 设置图片的显示位置
        float scale = (float) getBitmap(position).getHeight() / getBitmap(position).getWidth();
        int height = Math.max((int) (screenWidth * scale), screenHeight);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(screenWidth, height);
        holder.pwb_iv_image_browser.setLayoutParams(params);

        // 将scrollview加入viewGroup当中
        container.addView(holder.pwb_sv_item_image);

        return holder.pwb_sv_item_image;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public PicUrls getPic(int position) {
        return mPicUrlsList.get(position % mPicUrlsList.size());
    }

    public Bitmap getBitmap(int position) {
        Bitmap bitmap = null;
        View view = mPicViewList.get(position % mPicViewList.size());
        TouchNetworkImageView iv_image_browser = (TouchNetworkImageView) view.findViewById(R.id.pwb_iv_image_browser);
        Drawable drawable = iv_image_browser.getDrawable();
        if(drawable != null && drawable instanceof BitmapDrawable) {
            BitmapDrawable bd = (BitmapDrawable) drawable;
            bitmap = bd.getBitmap();
        }

        return bitmap;
    }

    protected class ViewHolder {
        private ScrollView pwb_sv_item_image;
        private TouchNetworkImageView pwb_iv_image_browser;
    }
}
