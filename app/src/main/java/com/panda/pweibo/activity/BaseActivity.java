package com.panda.pweibo.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.panda.pweibo.BaseApplication;
import com.panda.pweibo.constants.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;

/**
 * activity的基类
 *
 * Created by Administrator on 2015/8/26:20:27.
 */
public class BaseActivity extends Activity {

    protected RequestQueue              mRequestQueue;
    protected ImageCache                mImageCache;
    protected ImageLoader               mImageLoader;
    protected LruCache<String, Bitmap>  mLruCache;
    protected Oauth2AccessToken         mAccessToken;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

        mAccessToken = AccessTokenKeeper.readAccessToken(this);
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
    }
}