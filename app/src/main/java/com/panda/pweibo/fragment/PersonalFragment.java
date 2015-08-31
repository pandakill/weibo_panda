package com.panda.pweibo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.JsonObjectRequest;
import com.panda.pweibo.MyFragment;
import com.panda.pweibo.R;
import com.panda.pweibo.activity.MainActivity;
import com.panda.pweibo.adapter.UserAdapter;
import com.panda.pweibo.models.UserItem;
import com.panda.pweibo.utils.TitlebarUtils;
import com.panda.pweibo.utils.ToastUtils;
import com.panda.pweibo.widget.WrapHeightListView;

import java.util.ArrayList;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class PersonalFragment extends MyFragment {

    private     MainActivity            mActivity;
    private     View                    mView;
    private     UserAdapter             mAdapter;
    private     List<UserItem>          mUserItemList;

    private     ImageView               pwb_imageview_item_status_avatar;
    private     TextView                pwb_textview_sender;
    private     TextView                pwb_textview_item_status_from_and_when;

    private     TextView                pwb_status_count;
    private     TextView                pwb_follows_count;
    private     TextView                pwb_followed_count;

    private     WrapHeightListView      pwb_lv_user;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

        mActivity = (MainActivity) getActivity();

        mUserItemList = personalItem();

        mAdapter = new UserAdapter(mActivity, mUserItemList);
    }

    public PersonalFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initView();
        loadData();
        setData();

        return mView;
    }

    /** 初始化view */
    public void initView() {
        mView = View.inflate(mActivity, R.layout.fragment_personal, null);
        pwb_lv_user = (WrapHeightListView) mView.findViewById(R.id.pwb_lv_user);

        pwb_imageview_item_status_avatar = (ImageView) mView.findViewById(
                R.id.pwb_imageview_item_status_avatar);
        pwb_textview_sender = (TextView) mView.findViewById(
                R.id.pwb_textview_sender);
        pwb_textview_item_status_from_and_when = (TextView) mView.findViewById(
                R.id.pwb_textview_item_status_from_and_when);

        pwb_status_count = (TextView) mView.findViewById(
                R.id.pwb_status_count);
        pwb_follows_count = (TextView) mView.findViewById(
                R.id.pwb_follows_count);
        pwb_followed_count = (TextView) mView.findViewById(
                R.id.pwb_followed_count);

        new TitlebarUtils(mView)
                .setTitleContent("我")
                .setTitlebarTvRight("设置")
                .setRightOnClickListner(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastUtils.showToast(mActivity, "设置", Toast.LENGTH_SHORT);
                    }
                });

        pwb_lv_user.setAdapter(mAdapter);
    }

    private void loadData() {
    }

    private void setData() {

    }

    protected List<UserItem> personalItem (){
        List<UserItem> data = new ArrayList<>();

        data.add(new UserItem(true, R.drawable.pwb_push_icon_app_small_1, "新的朋友", ""));
        data.add(new UserItem(true, R.drawable.pwb_push_icon_app_small_2, "微博等级", "Lv13"));
        data.add(new UserItem(false, R.drawable.pwb_push_icon_app_small_3, "我的相册", "(17)"));
        data.add(new UserItem(true, R.drawable.pwb_push_icon_app_small_4, "微博支付", ""));
        data.add(new UserItem(true, R.drawable.pwb_push_icon_app_small_5, "更多", "收藏、名片"));

        return data;
    }
}
