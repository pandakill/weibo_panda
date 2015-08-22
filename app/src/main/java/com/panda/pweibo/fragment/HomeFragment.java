package com.panda.pweibo.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.panda.pweibo.R;
import com.panda.pweibo.activity.MainActivity;
import com.panda.pweibo.activity.PWBAuthActivity;
import com.panda.pweibo.constants.AccessTokenKeeper;
import com.panda.pweibo.constants.Constants;
import com.panda.pweibo.constants.Uri;
import com.panda.pweibo.models.response.HomeTimelineResponse;
import com.panda.pweibo.utils.TitlebarUtils;
import com.panda.pweibo.utils.ToastUtils;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.LogoutAPI;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A placeholder fragment containing a simple view.
 */
public class HomeFragment extends Fragment {

    private     View            view;
    private     Context         context;
    private     ListView        listView;
    private     RequestQueue    requestQueue;
    private     ProgressDialog  pd;

    protected   MainActivity    activity;

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

        activity = (MainActivity) getActivity();
        requestQueue = Volley.newRequestQueue(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        initView();
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
                // TODO token不合法
                .setRightOnClickListner(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        new LogoutAPI(activity, Constants.APP_KEY, AccessTokenKeeper.readAccessToken(HomeFragment.super.getView().getContext()))
//                                .logout(new RequestListener() {
//                                            @Override
//                                            public void onComplete(String response) {
//                                                if (!TextUtils.isEmpty(response)) {
//                                                    try {
//                                                        JSONObject jsonObject = new JSONObject(response);
//                                                        String value = jsonObject.getString("result");
//
//                                                        if (value.equals("true")) {
//                                                            ToastUtils.showToast(activity, "退出成功", Toast.LENGTH_LONG);
//                                                            Intent intent = new Intent(activity, PWBAuthActivity.class);
//                                                            startActivity(intent);
//                                                        }
//                                                    } catch (JSONException e) {
//                                                        e.printStackTrace();
//                                                    }
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onWeiboException(WeiboException e) {
//                                                ToastUtils.showToast(activity, "退出异常", Toast.LENGTH_SHORT);
//                                            }
//                                        }
//                                );
                        /** TODO 还未完成,需要将参数转为JSONObject */
                        JSONObject jsonRequest = new JSONObject();
                        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, Uri.revokeoauth2, jsonRequest, new Response.Listener<JSONObject>() {
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

                            }
                        });
                        requestQueue.add(stringRequest);
                    }
                });
        listView = (ListView) view.findViewById(R.id.listview_home);
    }

    public void loadData() {
        pd = ProgressDialog.show(activity, "正在加载数据...", "加载ing...");

        /**
         * TODO 请求的参数未设置
         */
        String uri = Uri.home_timeline;
        uri += "?access_token=" + Oauth2AccessToken.KEY_ACCESS_TOKEN;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Uri.home_timeline, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                HomeTimelineResponse homeTimelineResponse = new Gson().fromJson(response.toString(), HomeTimelineResponse.class);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }


}
