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
public class PersonalFragment extends Fragment {

    private     MainActivity            activity;
    private     Oauth2AccessToken       mAccessToken;
    private     View                    view;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

        activity = (MainActivity) getActivity();
        mAccessToken = AccessTokenKeeper.readAccessToken(activity);
    }

    public PersonalFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initView();
        return view;
    }

    /** 初始化view */
    public void initView() {
        view = View.inflate(activity, R.layout.fragment_personal, null);

        new TitlebarUtils(view)
                .setTitleContent("我")
                .setTitlebarTvRight("设置")
                .setRightOnClickListner(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastUtils.showToast(activity, "设置", Toast.LENGTH_SHORT);
                    }
                });
    }
}
