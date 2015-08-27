package com.panda.pweibo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;

import com.panda.pweibo.R;
import com.panda.pweibo.constants.AccessTokenKeeper;


/**
 * 项目的欢迎页activity
 *
 * Created by Administrator on 2015/8/21:13:45.
 */
public class SplashActivity extends BaseActivity {

    private static final int    WHAT_INTENT2LOGIN   =   1;      //未登录的标记
    private static final int    WHAT_INTENT2MAIN    =   2;      //已经登录标记
    private static final long   SPLASH_DUE_TIME     =   1000;   //欢迎页面停留时间

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 读取token信息,判断授权是否过期,如token有效,则在进程发送延时消息WHAT_INTENT2MAIN
        mAccessToken = AccessTokenKeeper.readAccessToken(SplashActivity.this);
        if (mAccessToken.isSessionValid()) {
            handler.sendEmptyMessageDelayed(WHAT_INTENT2MAIN, SPLASH_DUE_TIME);
        } else {
            handler.sendEmptyMessageDelayed(WHAT_INTENT2LOGIN, SPLASH_DUE_TIME);
        }
    }

    /**
     * 进程消息处理
     * 若进程收到WHAT_INTENT2MAIN,则打开PWBItemStatusActivity
     * 若进程收到WHAT_INTENT2LOGIN,则打开PWBAuthActivity
     */
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case WHAT_INTENT2LOGIN:
                    Intent intent = new Intent(SplashActivity.this, PWBAuthActivity.class);
                    startActivity(intent);
                    finish();
                    break;

                case WHAT_INTENT2MAIN:
                    intent = new Intent(SplashActivity.this, PWBAuthActivity.class);
                    startActivity(intent);
                    finish();
                    break;

                default:
                    break;
            }
        }
    };
}
