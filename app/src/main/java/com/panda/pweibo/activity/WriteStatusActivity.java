package com.panda.pweibo.activity;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.panda.pweibo.R;
import com.panda.pweibo.WriteStatusRequest;
import com.panda.pweibo.adapter.WriteStatusGridImgsAdapter;
import com.panda.pweibo.constants.Constants;
import com.panda.pweibo.utils.ImageUtils;
import com.panda.pweibo.utils.TitlebarUtils;
import com.panda.pweibo.utils.ToastUtils;
import com.panda.pweibo.widget.WrapHeightGridView;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 *
 * Created by Administrator on 2015/8/26:19:45.
 */
public class WriteStatusActivity extends BaseActivity implements OnClickListener, OnItemClickListener {

    private WriteStatusGridImgsAdapter mStatusImgsAdapter;
    private ArrayList<Uri>      mImgUris;
    private Uri                 mUri;
    private ProgressDialog      mPd;

    /** 内容编辑框 */
    private TextView            pwb_et_write_status;

    /** 添加的九宫格图片 */
    private WrapHeightGridView  pwb_gv_status_image;

    /** 转发的微博部分 */
    private LinearLayout        include_retweeted_card;
    private NetworkImageView    pwb_iv_small_status_image;
    private TextView            pwb_tv_small_status_name;
    private TextView            pwb_tv_small_status_content;

    /** 底部图片按钮栏 */
    private ImageView           pwb_iv_image;
    private ImageView           pwb_iv_at;
    private ImageView           pwb_iv_topic;
    private ImageView           pwb_iv_emoji;
    private ImageView           pwb_iv_add;

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_write_status);

        mImgUris = new ArrayList<>();

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
                        try {
                            sendStatus();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

        /** 初始化编辑框 */
        pwb_et_write_status = (TextView) findViewById(R.id.pwb_et_write_status);
        pwb_et_write_status.setHint("分享新鲜事...");

        /** 初始化底部添加栏 */
        pwb_iv_image        = (ImageView) findViewById(R.id.pwb_iv_image);
        pwb_iv_at           = (ImageView) findViewById(R.id.pwb_iv_at);
        pwb_iv_topic        = (ImageView) findViewById(R.id.pwb_iv_topic);
        pwb_iv_emoji        = (ImageView) findViewById(R.id.pwb_iv_emoji);
        pwb_iv_add          = (ImageView) findViewById(R.id.pwb_iv_add);

        pwb_iv_image.setOnClickListener(this);
        pwb_iv_at.setOnClickListener(this);
        pwb_iv_topic.setOnClickListener(this);
        pwb_iv_emoji.setOnClickListener(this);
        pwb_iv_add.setOnClickListener(this);

        initRetweet();
        initGridViewImage();
    }

    /** 初始化转发微博部分的控件 */
    private void initRetweet() {
        include_retweeted_card      =   (LinearLayout) findViewById(R.id.include_retweeted_card);
        pwb_iv_small_status_image   =   (NetworkImageView) findViewById(R.id.pwb_iv_small_status_image);
        pwb_tv_small_status_name    =   (TextView) findViewById(R.id.pwb_tv_small_status_name);
        pwb_tv_small_status_content =   (TextView) findViewById(R.id.pwb_tv_small_status_content);
    }

    /** 初始化gridview */
    private void initGridViewImage() {
        pwb_gv_status_image         =   (WrapHeightGridView) findViewById(R.id.pwb_gv_status_image);

        mStatusImgsAdapter = new WriteStatusGridImgsAdapter(this, mImgUris, pwb_gv_status_image);
        pwb_gv_status_image.setAdapter(mStatusImgsAdapter);
        pwb_gv_status_image.setOnItemClickListener(this);
    }

    /** 发送纯文字微博的实现 */
    private void sendStatus() throws JSONException {
        final String content = pwb_et_write_status.getText().toString();

        if (TextUtils.isEmpty(content)) {
            ToastUtils.showToast(this, "微博内容不能为空", Toast.LENGTH_LONG);
            return;
        }

        /** 新浪微博官方的api */
        StatusesAPI statusesAPI = new StatusesAPI(this, Constants.APP_KEY, mAccessToken);

        mPd = ProgressDialog.show(WriteStatusActivity.this, "发送微博", "正在发送...");

        if (mImgUris.size() > 0) {

            // 微博API中只支持上传一张图片
            Uri uri = mImgUris.get(0);

            // 通过uri生成bitmap （防止踩坑、做个标记）
            Bitmap bitmap = ImageUtils.getBitmapFromUri(WriteStatusActivity.this, uri);

            // 通过以下方法生成不了bitmap
//            String imgFilePath = ImageUtils.getImageAbsolutePath19(this, uri);
//            BitmapFactory.Options opts = new BitmapFactory.Options();
//            opts.inJustDecodeBounds = true;
//            Bitmap bitmap = BitmapFactory.decodeFile(imgFilePath, opts);

            if (null != bitmap || bitmap.getWidth() != 0 || bitmap.getHeight() != 0) {
                statusesAPI.upload(content, bitmap, null, null, new RequestListener() {
                    @Override
                    public void onComplete(String s) {
                        mPd.dismiss();
                        ToastUtils.showToast(WriteStatusActivity.this, "发送成功", Toast.LENGTH_SHORT);
                        Intent data = new Intent();
                        data.putExtra("sendStatusSuccess", true);
                        setResult(RESULT_OK, data);
                        WriteStatusActivity.this.finish();
                    }

                    @Override
                    public void onWeiboException(WeiboException e) {
                        e.printStackTrace();
                        mPd.dismiss();
                        ToastUtils.showToast(WriteStatusActivity.this, "发送失败", Toast.LENGTH_SHORT);
                    }
                });
            } else {
                mPd.dismiss();
                ToastUtils.showToast(WriteStatusActivity.this, "找不到图片", Toast.LENGTH_SHORT);
            }
        } else {

            mPd = ProgressDialog.show(WriteStatusActivity.this, "发送微博", "正在发送...");
            statusesAPI.update(content, null, null, new RequestListener() {
                @Override
                public void onComplete(String s) {
                    mPd.dismiss();
                    ToastUtils.showToast(WriteStatusActivity.this, "发送成功", Toast.LENGTH_SHORT);
                    Intent data = new Intent();
                    data.putExtra("sendStatusSuccess", true);
                    setResult(RESULT_OK, data);
                    WriteStatusActivity.this.finish();
                }

                @Override
                public void onWeiboException(WeiboException e) {
                    e.printStackTrace();
                    mPd.dismiss();
                    ToastUtils.showToast(WriteStatusActivity.this, "发送失败", Toast.LENGTH_SHORT);
                }
            });
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pwb_iv_image:
                ImageUtils.showImagePickDialog(this);
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

    /**
     * 更新图片显示
     */
    private void updateImgs() {
        if(mImgUris.size() > 0) {
            // 如果有图片则显示GridView,同时更新内容
            pwb_gv_status_image.setVisibility(View.VISIBLE);
            mStatusImgsAdapter.notifyDataSetChanged();
        } else {
            // 无图则不显示GridView
            pwb_gv_status_image.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object itemAdapter = parent.getAdapter();

        if (itemAdapter instanceof WriteStatusGridImgsAdapter) {
            // 点击的是添加的图片
            if (position == mStatusImgsAdapter.getCount() - 1) {
                // 如果点击了最后一个加号图标,则显示选择图片对话框
                ImageUtils.showImagePickDialog(this);
                updateImgs();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case ImageUtils.REQUEST_CODE_FROM_ALBUM:
                if(resultCode == RESULT_CANCELED) {
                    return;
                }
                Uri imageUri = data.getData();
                mImgUris.add(imageUri);
                updateImgs();
                break;

            case ImageUtils.REQUEST_CODE_FROM_CAMERA:
                // 本地相册选择完后将图片添加到页面上
                if(resultCode == RESULT_CANCELED) {
                    ImageUtils.deleteImageUri(this, ImageUtils.imageUriFromCamera);
                } else {
                    Uri imageUriCamera = ImageUtils.imageUriFromCamera;

                    mImgUris.add(imageUriCamera);
                    updateImgs();
                }
                break;

            default:
                break;
        }
    }
}