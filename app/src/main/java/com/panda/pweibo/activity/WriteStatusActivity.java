package com.panda.pweibo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
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
public class WriteStatusActivity extends BaseActivity implements OnClickListener {

    private TextView        pwb_et_write_status;
    private ImageView       pwb_iv_image;
    private ImageView       pwb_iv_at;
    private ImageView       pwb_iv_topic;
    private ImageView       pwb_iv_emoji;
    private ImageView       pwb_iv_add;

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
        pwb_iv_image        = (ImageView) findViewById(R.id.pwb_iv_image);
        pwb_iv_at           = (ImageView) findViewById(R.id.pwb_iv_at);
        pwb_iv_topic        = (ImageView) findViewById(R.id.pwb_iv_topic);
        pwb_iv_emoji        = (ImageView) findViewById(R.id.pwb_iv_emoji);
        pwb_iv_add          = (ImageView) findViewById(R.id.pwb_iv_add);

        pwb_et_write_status.setHint("分享新鲜事...");
        pwb_iv_image.setOnClickListener(this);
        pwb_iv_at.setOnClickListener(this);
        pwb_iv_topic.setOnClickListener(this);
        pwb_iv_emoji.setOnClickListener(this);
        pwb_iv_add.setOnClickListener(this);
    }

    /** 发送纯文字微博的实现 */
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pwb_iv_image:
                ToastUtils.showToast(this, "加入图片", Toast.LENGTH_SHORT);
                break;

            case R.id.pwb_iv_at:
                ToastUtils.showToast(this, "@被点击", Toast.LENGTH_SHORT);
                break;

            case R.id.pwb_iv_topic:
                ToastUtils.showToast(this, "添加话题", Toast.LENGTH_SHORT);
                break;

            case R.id.pwb_iv_emoji:
                ToastUtils.showToast(this, "添加表情", Toast.LENGTH_SHORT);
                break;

            case R.id.pwb_iv_add:
                ToastUtils.showToast(this, "添加其他", Toast.LENGTH_SHORT);
                break;

            default:
                break;
        }
    }

    /** 发带有图片的微博 */
//    private void sendStatusWithPic() {
//        final String content = pwb_et_write_status.getText().toString();
//
//        if (TextUtils.isEmpty(content)) {
//            ToastUtils.showToast(this, "微博内容不能为空", Toast.LENGTH_LONG);
//            return;
//        }
//        StatusesAPI statusesAPI = new StatusesAPI(this, Constants.APP_KEY, mAccessToken);
//        statusesAPI.upload(content, bitmap, "0.0", "0.0", new RequestListener() {
//            @Override
//            public void onComplete(String s) {
//
//            }
//
//            @Override
//            public void onWeiboException(WeiboException e) {
//
//            }
//        });
//    }
}