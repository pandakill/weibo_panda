package com.panda.pweibo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.panda.pweibo.R;
import com.panda.pweibo.activity.MainActivity;
import com.panda.pweibo.utils.TitlebarUtils;
import com.panda.pweibo.utils.ToastUtils;


/**
 * A placeholder fragment containing a simple view.
 */
public class PersonalFragment extends Fragment {

    private     MainActivity            mActivity;
    private     View                    mView;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

        mActivity = (MainActivity) getActivity();
    }

    public PersonalFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initView();
        return mView;
    }

    /** 初始化view */
    public void initView() {
        mView = View.inflate(mActivity, R.layout.fragment_personal, null);

        new TitlebarUtils(mView)
                .setTitleContent("我")
                .setTitlebarTvRight("设置")
                .setRightOnClickListner(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastUtils.showToast(mActivity, "设置", Toast.LENGTH_SHORT);
                    }
                });
    }
}
