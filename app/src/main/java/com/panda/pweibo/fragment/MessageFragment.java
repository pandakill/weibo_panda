package com.panda.pweibo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.panda.pweibo.R;
import com.panda.pweibo.activity.MainActivity;
import com.panda.pweibo.constants.AccessTokenKeeper;
import com.panda.pweibo.utils.TitlebarUtils;
import com.panda.pweibo.utils.ToastUtils;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;


/**
 * A placeholder fragment containing a simple view.
 */
public class MessageFragment extends Fragment {

    private     View                    view;
    private     MainActivity            activity;
    private     Oauth2AccessToken       mAccessToken;

    public MessageFragment() {
    }

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

        activity        = (MainActivity) getActivity();
        mAccessToken    = AccessTokenKeeper.readAccessToken(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initView();
        return view;
    }

    /** 初始化view */
    public void initView(){

        view = View.inflate(activity, R.layout.fragment_message, null);

        new TitlebarUtils(view)
                .setTitleContent("消息")
                .setTitlebarTvRight("发起聊天")
                .setRightOnClickListner(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastUtils.showToast(activity, "发起聊天", Toast.LENGTH_LONG);
                    }
                });
    }
}
