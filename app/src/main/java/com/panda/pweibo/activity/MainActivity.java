package com.panda.pweibo.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.LruCache;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.Volley;
import com.panda.pweibo.BaseApplication;
import com.panda.pweibo.R;
import com.panda.pweibo.fragment.FragmentController;
import com.panda.pweibo.utils.ToastUtils;

/**
 * 项目的主activity
 */
public class MainActivity extends FragmentActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    private long mExitTime = 0;
    private     ImageButton         mImageButton;
    public     FragmentController  mController;

    public      RequestQueue        mRequestQueue;
    public      ImageCache          mImageCache;
    public      ImageLoader         mImageLoader;
    public LruCache<String,Bitmap>  mLruCache;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    @Override
    protected void onCreate(Bundle saveInstanceState) {

        super.onCreate(saveInstanceState);

        mController = FragmentController.getInstance(MainActivity.this, R.id.fl_content);
        //默认展示第一个
        mController.showFragment(0);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mRequestQueue = BaseApplication.getsRequestQueue();

        mLruCache = new LruCache<>(40);
        mImageCache = new ImageCache() {
            @Override
            public Bitmap getBitmap(String key) {
                return mLruCache.get(key);
            }

            @Override
            public void putBitmap(String key, Bitmap value) {
                mLruCache.put(key, value);
            }
        };

        mImageLoader = new ImageLoader(mRequestQueue, mImageCache);

        initView();
    }

    private void initView() {

        RadioGroup radioGroup;
        radioGroup = (RadioGroup) findViewById(R.id.pwb_radiogroup_fragment_tab);
        mImageButton = (ImageButton) findViewById(R.id.pwb_imagebutton_add);

        radioGroup.setOnCheckedChangeListener(MainActivity.this);
        mImageButton.setOnClickListener(MainActivity.this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case  R.id.pwb_radiobutton_home:
                mController.showFragment(0);
                break;

            case R.id.pwb_radiobutton_message:
                mController.showFragment(1);
                break;

            case R.id.pwb_radiobutton_search:
                mController.showFragment(2);
                break;

            case R.id.pwb_radiobutton_pesonal:
                mController.showFragment(3);
                break;

            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pwb_imagebutton_add:
                Intent intent = new Intent(this, WriteStatusActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {//
                // 如果两次按键时间间隔大于2000毫秒，则不退出
                ToastUtils.showToast(this, "再按一次退出程序", Toast.LENGTH_SHORT);
                mExitTime = System.currentTimeMillis();// 更新mExitTime
            } else {
                System.exit(0);// 否则退出程序
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
