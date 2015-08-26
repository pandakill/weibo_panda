package com.panda.pweibo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.panda.pweibo.R;
import com.panda.pweibo.constants.Uri;
import com.panda.pweibo.models.Status;
import com.panda.pweibo.utils.TitlebarUtils;
import com.panda.pweibo.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

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
        String comment = pwb_et_write_status.getText().toString();

        if (TextUtils.isEmpty(comment)) {
            ToastUtils.showToast(this, "评论不能为空", Toast.LENGTH_LONG);
            return;
        }

        // 填写参数和uri地址
        String uri = Uri.comments_create;
        JSONObject requestParams = new JSONObject();
        try {
            requestParams.put("access_token", mAccessToken.getToken());
            requestParams.put("comment", comment);
            requestParams.put("id", status.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                uri, requestParams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                ToastUtils.showToast(WriteCommentActivity.this, "评论发送成功", Toast.LENGTH_SHORT);

                // 评论发送成功后,设置result结果数据，并关闭本页面
                Intent data = new Intent();
                data.putExtra("sendCommentSuccess", true);
                setResult(RESULT_OK, data);

                WriteCommentActivity.this.finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ToastUtils.showToast(WriteCommentActivity.this, "网络发生错误,发送评论失败", Toast.LENGTH_SHORT);
            }
        });


    }
}