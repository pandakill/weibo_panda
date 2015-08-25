package com.panda.pweibo.listener;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.panda.pweibo.activity.PWBAuthActivity;
import com.panda.pweibo.constants.Uri;
import com.panda.pweibo.utils.ToastUtils;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2015/8/25:10:58.
 */
public class LogoutListener implements View.OnClickListener {

    private Context             context;
    private Oauth2AccessToken   mAccesssToken;
    private RequestQueue        requestQueue;
    private Fragment            fragment;

    public LogoutListener (Context context, Oauth2AccessToken mAccessToken,
                           RequestQueue requestQueue, Fragment fragment) {
        this.context        = context;
        this.mAccesssToken  = mAccessToken;
        this.requestQueue   = requestQueue;
        this.fragment       = fragment;
    }

    @Override
    public void onClick(View v) {

        String uri = Uri.revokeoauth2 + "?access_token=" + mAccesssToken.getToken();
        Log.i("tag", "uri=" + uri);
        ToastUtils.showToast(context, "退出按钮被点击", Toast.LENGTH_SHORT);
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, uri, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (null != response) {
                    try {
                        String value = response.getString("result");

                        if (value.equals("true")) {
                            ToastUtils.showToast(context, "退出成功", Toast.LENGTH_SHORT);
                            Intent intent = new Intent(context, PWBAuthActivity.class);
                            fragment.startActivity(intent);
                        } else {
                            ToastUtils.showToast(context, "退出发生异常", Toast.LENGTH_SHORT);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtils.showToast(context, "退出发生异常", Toast.LENGTH_SHORT);
            }
        });
        requestQueue.add(stringRequest);
    }
}
