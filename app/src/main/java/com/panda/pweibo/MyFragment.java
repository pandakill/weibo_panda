package com.panda.pweibo;

import android.support.v4.app.Fragment;

import java.lang.reflect.Field;

/**
 * 重写fragment的onDetach方法
 *
 * Created by Administrator on 2015/8/28:17:36.
 */
public class MyFragment extends Fragment {

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
