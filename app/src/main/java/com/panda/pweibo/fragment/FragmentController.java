package com.panda.pweibo.fragment;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentActivity;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/8/21:15:38.
 */
public class FragmentController {

    private     int                     containerId;        //存放fragment的id
    private     FragmentManager         fragmentManager;
    private     ArrayList<Fragment>     fragments;

    private static FragmentController   controller;

    private FragmentController(FragmentActivity activity, int containerId) {
        this.containerId = containerId;
        fragmentManager = activity.getSupportFragmentManager();
        initFragments();
    }

    /** 初始化fragments */
    private void initFragments() {
        fragments = new ArrayList<Fragment>();
        fragments.add(new HomeFragment());
        fragments.add(new MessageFragment());
        fragments.add(new SearchFragment());
        fragments.add(new PersonalFragment());

        // 开启事务
        FragmentTransaction ft = fragmentManager.beginTransaction();
        for (Fragment fragment : fragments) {
            ft.add(containerId, fragment);
        }
        ft.commitAllowingStateLoss();
    }

    /** 打开fragment并展示 */
    public void showFragment(int position) {
        hideFragments();
        Fragment fragment = fragments.get(position);
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.show(fragment);
        ft.commitAllowingStateLoss();
    }

    /** 隐藏不显示的fragment */
    public void hideFragments() {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        for (Fragment fragment : fragments) {
            if (fragment != null) {
                ft.hide(fragment);
            }
        }
        ft.commitAllowingStateLoss();
    }

    /** 根据下标获取fragment */
    public Fragment getFragment(int position) {
        return fragments.get(position);
    }

    public static FragmentController getInstance(FragmentActivity activity, int containerId) {
        if (controller == null) {
            controller = new FragmentController(activity, containerId);
        }
        return controller;
    }
}
