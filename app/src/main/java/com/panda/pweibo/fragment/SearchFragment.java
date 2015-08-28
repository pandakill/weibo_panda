package com.panda.pweibo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.panda.pweibo.MyFragment;
import com.panda.pweibo.R;
import com.panda.pweibo.activity.MainActivity;


/**
 * A placeholder fragment containing a simple view.
 */
public class SearchFragment extends MyFragment {

    private MainActivity    mActivity;
    private View            mView;

    @Override
    public void onCreate(Bundle savaInstanceState) {
        super.onCreate(savaInstanceState);

        mActivity = (MainActivity) getActivity();
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

    }
}
