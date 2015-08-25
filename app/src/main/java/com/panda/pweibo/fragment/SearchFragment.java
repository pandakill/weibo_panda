package com.panda.pweibo.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.panda.pweibo.R;
import com.panda.pweibo.activity.MainActivity;
import com.panda.pweibo.constants.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;


/**
 * A placeholder fragment containing a simple view.
 */
public class SearchFragment extends Fragment {

    private MainActivity activity;
    private Oauth2AccessToken mAccessToken;
    private View view;

    @Override
    public void onCreate(Bundle savaInstanceState) {
        super.onCreate(savaInstanceState);

        activity = (MainActivity) getActivity();
        mAccessToken = AccessTokenKeeper.readAccessToken(activity);
    }

    public SearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initView();
        return view;
    }

    /** 初始化view */
    public void initView() {
        view = View.inflate(activity, R.layout.fragment_search, null);

    }
}
