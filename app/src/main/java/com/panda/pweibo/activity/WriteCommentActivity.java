package com.panda.pweibo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.panda.pweibo.NormalPostRequest;
import com.panda.pweibo.R;
import com.panda.pweibo.constants.Constants;
import com.panda.pweibo.constants.Uri;
import com.panda.pweibo.models.Status;
import com.panda.pweibo.utils.TitlebarUtils;
import com.panda.pweibo.utils.ToastUtils;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.sina.weibo.sdk.net.openapi.ShareWeiboApi;
import com.sina.weibo.sdk.openapi.CommentsAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Administrator on 2015/8/26:19:45.
 */
public class WriteCommentActivity extends BaseActivity {

    private TextView        pwb_et_write_status;
    private Status          status;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_write_status);

        initView();
    }

    public void initView() {
        new TitlebarUtils(this).setTitleContent("发评论")
                .setTitlebarTvLeft("取消")
                .setLeftOnClickListner(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        WriteCommentActivity.this.finish();
                    }
                })
                .setTitlebarTvRight("发送")
                .setRightOnClickListner(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendComment();
                    }
                });

        pwb_et_write_status = (TextView) findViewById(R.id.pwb_et_write_status);

        status = (Status) getIntent().getSerializableExtra("status");
    }

    /** 发送评论的实现 */
    private void sendComment() {
        final String comment = pwb_et_write_status.getText().toString();

        if (TextUtils.isEmpty(comment)) {
            ToastUtils.showToast(this, "评论不能为空", Toast.LENGTH_LONG);
            return;
        }

        // 填写参数和uri地址
        String uri = Uri.comments_create;
//        JSONObject requestParams = new JSONObject();
//        try {
//            requestParams.put("access_token", mAccessToken.getToken());
//            requestParams.put("comment", comment);
//            requestParams.put("id", status.getId());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        Map map = new HashMap();
        map.put("access_token", mAccessToken.getToken());
        map.put("comment", comment);
        map.put("id", status.getId());

        JSONObject requestParams = new JSONObject(map);

        /** 请求头不能为json，如果为json，微博接口会403拒绝访问
         *
         * TODO 这里的请求头有必要对其重写
         */
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
//                uri, requestParams, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                ToastUtils.showToast(WriteCommentActivity.this, "评论发送成功", Toast.LENGTH_SHORT);
//
//                // 评论发送成功后,设置result结果数据，并关闭本页面
//                Intent data = new Intent();
//                data.putExtra("sendCommentSuccess", true);
//                setResult(RESULT_OK, data);
//
//                WriteCommentActivity.this.finish();
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                ToastUtils.showToast(WriteCommentActivity.this, "网络发生错误,发送评论失败", Toast.LENGTH_SHORT);
//            }
//        });

//        Request request = new NormalPostRequest(uri, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                ToastUtils.showToast(WriteCommentActivity.this, "评论发送成功", Toast.LENGTH_SHORT);
//
//                // 评论发送成功后,设置result结果数据，并关闭本页面
//                Intent data = new Intent();
//                data.putExtra("sendCommentSuccess", true);
//                setResult(RESULT_OK, data);
//
//                WriteCommentActivity.this.finish();
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                ToastUtils.showToast(WriteCommentActivity.this, "网络发生错误,发送评论失败", Toast.LENGTH_SHORT);
//            }
//        }, map);

//        requestQueue.add(jsonObjectRequest);

        /** 新浪微博官方的api */
        CommentsAPI commentsAPI = new CommentsAPI(this, Constants.APP_KEY, mAccessToken);
        WeiboParameters parameters = new WeiboParameters(Constants.APP_KEY);
        parameters.put("comment", comment);
        parameters.put("access_token", mAccessToken.getToken());
        parameters.put("id", status.getId());
        commentsAPI.create(comment, status.getId(), false, new RequestListener() {
            @Override
            public void onComplete(String s) {
                Intent data = new Intent();
                data.putExtra("sendCommentSuccess", true);
                setResult(RESULT_OK, data);
                WriteCommentActivity.this.finish();
            }
            @Override
            public void onWeiboException(WeiboException e) {
                ToastUtils.showToast(WriteCommentActivity.this, "网络异常,请再次提交", Toast.LENGTH_SHORT);
            }
        });
    }
}