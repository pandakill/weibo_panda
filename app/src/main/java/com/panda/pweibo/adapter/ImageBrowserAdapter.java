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

        // 初始化每个item的控件
        ScrollView sv = new ScrollView(mContext);
        FrameLayout.LayoutParams svParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        sv.setLayoutParams(svParams);

        LinearLayout ll = new LinearLayout(mContext);
        LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        ll.setLayoutParams(llParams);
        sv.addView(ll);

        // 获取屏幕宽高
        DisplayMetrics metric = new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay().getMetrics(metric);
        final int screenHeight = metric.heightPixels;
        final int screenWidth = metric.widthPixels;

        final NetworkImageView iv = new NetworkImageView(mContext);
        iv.setScaleType(NetworkImageView.ScaleType.FIT_CENTER);
        ll.addView(iv);

        iv.setDefaultImageResId(R.drawable.pwb_test_image);
        iv.setErrorImageResId(R.drawable.ic_com_sina_weibo_sdk_logo);

        // 设置图片的显示位置
        float scale = (float) getBitmap(position).getHeight() / getBitmap(position).getWidth();
        int height = Math.max((int) (screenWidth * scale), screenHeight);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(screenWidth, height);
        iv.setLayoutParams(params);

        // 加载图片
        iv.setImageUrl(url, mImageLoader);

        // 将scrollview加入viewGroup当中
        container.addView(sv);

        return sv;

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
        ImageView iv_image_browser = (ImageView) view.findViewById(R.id.pwb_iv_image_browser);
        Drawable drawable = iv_image_browser.getDrawable();
        if(drawable != null && drawable instanceof BitmapDrawable) {
            BitmapDrawable bd = (BitmapDrawable) drawable;
            bitmap = bd.getBitmap();
        }

        return bitmap;
    }
}
