package com.panda.pweibo.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.panda.pweibo.widget.Pull2RefreshListView;

import java.util.List;

/**
 * 微博详情页的tab下面的viewpager适配器
 *
 * Created by Administrator on 2015/9/12:11:42.
 */
public class StatusTabAdapter extends PagerAdapter {

    private Context mContext;
    private List<View> mViewList;

    /**
     * 构造函数,初始化context和viewlist
     * @param context 传入的context
     */
    public StatusTabAdapter(Context context, List<View> list) {
        mContext = context;
        mViewList = list;
    }

    @Override
    public int getCount() {
        return mViewList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mViewList.get(position));
        return mViewList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViewList.get(position));
    }
}
