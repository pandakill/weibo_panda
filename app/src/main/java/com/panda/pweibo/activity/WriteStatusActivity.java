package com.panda.pweibo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.panda.pweibo.R;
import com.panda.pweibo.constants.Constants;
import com.panda.pweibo.utils.TitlebarUtils;
import com.panda.pweibo.utils.ToastUtils;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;

/**
 *
 * Created by Administrator on 2015/8/26:19:45.
 */
public class WriteStatusActivity extends BaseActivity {

    private TextView        pwb_et_write_status;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_write_status);

        initView();
    }

    public void initView() {
        new TitlebarUtils(this).setTitleContent("发微博")
                .setTitlebarTvLeft("取消")
                .setLeftOnClickListner(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        WriteStatusActivity.this.finish();
                    }
                })
                .setTitlebarTvRight("发送")
                .setRightOnClickListner(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendStatus();
                    }
                });

        pwb_et_write_status = (TextView) findViewById(R.id.pwb_et_write_status);
        pwb_et_write_status.setHint("分享新鲜事...");
    }

    /** 发送微博的实现 */
    private void sendStatus() {
        final String content = pwb_et_write_status.getText().toString();

        if (TextUtils.isEmpty(content)) {
            ToastUtils.showToast(this, "微博内容不能为空", Toast.LENGTH_LONG);
            return;
        }

        // 填写参数和uri地址
//        String uri = Uri.comments_create;
//        JSONObject requestParams = new JSONObject();
//        try {
//            requestParams.put("access_token", mAccessToken.getToken());
//            requestParams.put("comment", comment);
//            requestParams.put("id", status.getId());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

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
        StatusesAPI statusesAPI = new StatusesAPI(this, Constants.APP_KEY, mAccessToken);
        statusesAPI.update(content, "0.0", "0.0", new RequestListener() {
            @Override
            public void onComplete(String s) {
                ToastUtils.showToast(WriteStatusActivity.this, "发送成功", Toast.LENGTH_SHORT);
                Intent data = new Intent();
                data.putExtra("sendStatusSuccess", true);
                setResult(RESULT_OK, data);
                WriteStatusActivity.this.finish();
            }

            @Override
            public void onWeiboException(WeiboException e) {
                ToastUtils.showToast(WriteStatusActivity.this, "发送失败", Toast.LENGTH_SHORT);
            }
        });
    }
}