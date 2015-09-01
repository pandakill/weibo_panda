package com.panda.pweibo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.panda.pweibo.MyFragment;
import com.panda.pweibo.R;
import com.panda.pweibo.activity.MainActivity;
import com.panda.pweibo.adapter.UserItemAdapter;
import com.panda.pweibo.models.UserItem;
import com.panda.pweibo.utils.TitlebarUtils;
import com.panda.pweibo.widget.WrapHeightListView;

import java.util.ArrayList;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class SearchFragment extends MyFragment {

    private MainActivity        mActivity;
    private View                mView;
    private UserItemAdapter     mAdapter;

    private WrapHeightListView  pwb_lv_item;

    @Override
    public void onCreate(Bundle savaInstanceState) {
        super.onCreate(savaInstanceState);

        List<UserItem> userItemList = searchItem();

        mActivity = (MainActivity) getActivity();

        mAdapter = new UserItemAdapter(mActivity, userItemList);
    }

    public SearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initView();
        return mView;
    }

    /** 初始化view */
    public void initView() {
        mView = View.inflate(mActivity, R.layout.fragment_search, null);

        pwb_lv_item = (WrapHeightListView) mView.findViewById(R.id.pwb_lv_item);

        pwb_lv_item.setAdapter(mAdapter);

        new TitlebarUtils(mView).setTitleContent("发现");

    }

    protected List<UserItem> searchItem (){
        List<UserItem> data = new ArrayList<>();

        data.add(new UserItem(true, R.drawable.pwb_push_icon_app_small_1, "热门微博", ""));
        data.add(new UserItem(false, R.drawable.pwb_push_icon_app_small_2, "找人", ""));
        data.add(new UserItem(false, R.drawable.pwb_push_icon_app_small_3, "奔跑2015", ""));
        data.add(new UserItem(true, R.drawable.pwb_push_icon_app_small_4, "游戏中心", ""));
        data.add(new UserItem(true, R.drawable.pwb_push_icon_app_small_5, "周边", "\"棠下\"最佳晚饭去处排行榜"));

        return data;
    }

}
