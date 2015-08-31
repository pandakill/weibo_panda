package com.panda.pweibo;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * 基本application类
 * Created by Administrator on 2015/08/31.
 */
public class BaseApplication extends Application {
    public static RequestQueue sRequestQueue;

    @Override
    public void onCreate() {
        super.onCreate();

        sRequestQueue = Volley.newRequestQueue(getApplicationContext());
    }

    public static RequestQueue getsRequestQueue() {
        return sRequestQueue;
    }
}
