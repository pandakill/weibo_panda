package com.panda.pweibo.fragment;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentActivity;

import java.util.ArrayList;

/**
 * fragment的控制类
 *
 * Created by Administrator on 2015/8/21:15:38.
 */
public class FragmentController {

    private     int                     mContainerId;        //存放fragment的id
    private     FragmentManager         mFragmentManager;
    private     ArrayList<Fragment>     mFragmentList;

    private static FragmentController   controller;

    private FragmentController(FragmentActivity activity, int containerId) {
        mContainerId = containerId;
        mFragmentManager = activity.getSupportFragmentManager();
        initFragments();
    }

    /** 初始化fragments */
    private void initFragments() {
        mFragmentList = new ArrayList<>();
        mFragmentList.add(new HomeFragment());
        mFragmentList.add(new MessageFragment());
        mFragmentList.add(new SearchFragment());
        mFragmentList.add(new PersonalFragment());

        // 开启事务
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        for (Fragment fragment : mFragmentList) {
            ft.add(mContainerId, fragment);
        }
        ft.commitAllowingStateLoss();
    }

    /** 打开fragment并展示 */
    public void showFragment(int position) {
        hideFragments();
        Fragment fragment = mFragmentList.get(position);
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.show(fragment);
        ft.commitAllowingStateLoss();
    }

    /** 隐藏不显示的fragment */
    public void hideFragments() {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        for (Fragment fragment : mFragmentList) {
            if (fragment != null) {
                ft.hide(fragment);
            }
        }
        ft.commitAllowingStateLoss();
    }

    public static FragmentController getInstance(FragmentActivity activity, int containerId) {
        if (controller == null) {
            controller = new FragmentController(activity, containerId);
        }
        return controller;
    }
}
