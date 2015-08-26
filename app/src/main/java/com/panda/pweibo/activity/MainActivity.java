package com.panda.pweibo.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.Volley;
import com.panda.pweibo.R;
import com.panda.pweibo.fragment.FragmentController;
import com.panda.pweibo.utils.ToastUtils;


public class MainActivity extends FragmentActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    private     RadioGroup          radioGroup;
    private     ImageButton         imageButton;
    private     FragmentController  controller;

    public      RequestQueue        requestQueue;
    public      ImageCache          imageCache;
    public      ImageLoader         imageLoader;
    public LruCache<String,Bitmap>  lruCache;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    @Override
    protected void onCreate(Bundle saveInstanceState) {

        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        controller = FragmentController.getInstance(MainActivity.this, R.id.fl_content);
        //默认展示第一个
        controller.showFragment(0);
        requestQueue = Volley.newRequestQueue(this);

        lruCache = new LruCache<>(40);
        imageCache = new ImageCache() {
            @Override
            public Bitmap getBitmap(String key) {
                return lruCache.get(key);
            }

            @Override
            public void putBitmap(String key, Bitmap value) {
                lruCache.put(key, value);
            }
        };

        imageLoader = new ImageLoader(requestQueue, imageCache);

        initView();
    }

    private void initView() {

        radioGroup = (RadioGroup) findViewById(R.id.pwb_radiogroup_fragment_tab);
        imageButton = (ImageButton) findViewById(R.id.pwb_imagebutton_add);

        radioGroup.setOnCheckedChangeListener(MainActivity.this);
        imageButton.setOnClickListener(MainActivity.this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case  R.id.pwb_radiobutton_home:
                controller.showFragment(0);
                break;

            case R.id.pwb_radiobutton_message:
                controller.showFragment(1);
                break;

            case R.id.pwb_radiobutton_search:
                controller.showFragment(2);
                break;

            case R.id.pwb_radiobutton_pesonal:
                controller.showFragment(3);
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
}
