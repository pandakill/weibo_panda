package com.panda.pweibo.adapter;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.panda.pweibo.R;
import com.panda.pweibo.activity.MainActivity;
import com.panda.pweibo.models.PicUrls;

import java.util.ArrayList;

/**
 * 图片浏览器的适配器
 * 
 * Created by Administrator on 2015/09/01.
 */
public class ImageBrowserAdapter extends PagerAdapter {

    private MainActivity        mContext;
//    private Activity            mContext;
    private ArrayList<PicUrls>  mPicUrlsList;
    private ArrayList<View>     mPicViewList;
    private ImageLoader         mImageLoader;

    public ImageBrowserAdapter(Activity context, ArrayList<PicUrls> picUrlsList, ImageLoader imageLoader) {
        mContext = (MainActivity) context;
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

    @Override
    public View instantiateItem(ViewGroup container, int position) {

        int index = position % mPicUrlsList.size();
        View view = mPicViewList.get(index);

        final ImageView pwb_iv_image_browser = (ImageView) view.findViewById(R.id.pwb_iv_image_browser);
        PicUrls picUrl = mPicUrlsList.get(index);

//        String url = picUrl.

        return view;
    }
}
