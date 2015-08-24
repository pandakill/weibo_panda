package com.panda.pweibo.fragment;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.panda.pweibo.R;
import com.panda.pweibo.activity.MainActivity;
import com.panda.pweibo.activity.PWBAuthActivity;
import com.panda.pweibo.adapter.StatusAdapter;
import com.panda.pweibo.constants.AccessTokenKeeper;
import com.panda.pweibo.constants.Uri;
import com.panda.pweibo.models.Status;
import com.panda.pweibo.utils.TitlebarUtils;
import com.panda.pweibo.utils.ToastUtils;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class HomeFragment extends Fragment {

    private     View                        view;
    private     ListView                    listView;
    //private     RequestQueue                requestQueue;
    private     LruCache<String, Bitmap>    avatarCache;
    private     ImageCache                  imageCache;

    protected   MainActivity    activity;

    private Oauth2AccessToken mAccesssToken;

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

        activity = (MainActivity) getActivity();
        //requestQueue = Volley.newRequestQueue(activity);
        mAccesssToken = AccessTokenKeeper.readAccessToken(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        initView();
        loadData(1);
        return view;
    }

    private void initView() {
        view = View.inflate(activity, R.layout.fragment_home, null );

        new TitlebarUtils(view)
                .setTitlebarTvLeft("LEFT")
                .setTitleContent("首页")
                .setTitlebarIvRight(R.drawable.pwb_icon_share_black)
                .setLeftOnClickListner(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastUtils.showToast(activity, "LEFT被点击", Toast.LENGTH_LONG);
                    }
                })
                /** 调用微博官方接口,设置退出按钮监听器 */
                .setRightOnClickListner(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String uri = Uri.revokeoauth2 + "?access_token=" + mAccesssToken.getToken();
                        Log.i("tag", "uri=" + uri);
                        ToastUtils.showToast(activity, "退出按钮被点击", Toast.LENGTH_SHORT);
                        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, uri, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                if (null != response) {
                                    try {
                                        String value = response.getString("result");

                                        if (value.equals("true")) {
                                            ToastUtils.showToast(activity, "退出成功", Toast.LENGTH_SHORT);
                                            Intent intent = new Intent(activity, PWBAuthActivity.class);
                                            startActivity(intent);
                                        } else {
                                            ToastUtils.showToast(activity, "退出发生异常", Toast.LENGTH_SHORT);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                ToastUtils.showToast(activity, "退出发生异常", Toast.LENGTH_SHORT);
                            }
                        });
                        activity.requestQueue.add(stringRequest);
                    }
                });
        listView = (ListView) view.findViewById(R.id.listview_home);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public void loadData(int page) {
        //pd = ProgressDialog.show(activity, "正在加载数据...", "加载ing...");

        /**
         * TODO 测试的token有效期为一天，所以必须每天重新授权获取token
         */
        String uri = Uri.home_timeline;
        uri += "?access_token=" + mAccesssToken.getToken();
        uri += "&since_id=0&max_id=0&count=25&base_app=0&feature=0&trim_user=0&page=" + page;
        final ArrayList<Status> listStatus = new ArrayList<Status>();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, uri, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray statuses = response.getJSONArray("statuses");
                    for (int i = 0; i < statuses.length(); i ++) {
                        Status status = new Status().parseJson(statuses.getJSONObject(i));
                        listStatus.add(status);
                        ImageLoader imageLoader = new ImageLoader(activity.requestQueue, imageCache);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listView.setAdapter(new StatusAdapter(activity, listStatus, activity.requestQueue));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                ToastUtils.showToast(activity, "加载失败", Toast.LENGTH_SHORT);
            }
        });

        String url = "";
        avatarCache = new LruCache<String, Bitmap>(25);
        imageCache = new ImageCache() {
            @Override
            public Bitmap getBitmap(String key) {
                return avatarCache.get(key);
            }

            @Override
            public void putBitmap(String key, Bitmap bitmap) {
                avatarCache.put(key, bitmap);
            }
        };

        activity.requestQueue.add(jsonObjectRequest);
    }

}
