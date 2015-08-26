package com.panda.pweibo.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.panda.pweibo.constants.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;

/**
 * activity的基类
 *
 * Created by Administrator on 2015/8/26:20:27.
 */
public class BaseActivity extends Activity {

    protected RequestQueue              requestQueue;
    protected ImageCache                imageCache;
    protected ImageLoader               imageLoader;
    protected LruCache<String, Bitmap>  lruCache;
    protected Oauth2AccessToken         mAccessToken;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

        mAccessToken = AccessTokenKeeper.readAccessToken(this);
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
    }
}