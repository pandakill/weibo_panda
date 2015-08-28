package com.panda.pweibo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.panda.pweibo.R;
import com.panda.pweibo.constants.Constants;
import com.panda.pweibo.models.Comment;
import com.panda.pweibo.models.Status;
import com.panda.pweibo.utils.TitlebarUtils;
import com.panda.pweibo.utils.ToastUtils;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.sina.weibo.sdk.openapi.CommentsAPI;

/**
 * 写评论的activity
 *
 * Created by Administrator on 2015/8/26:19:45.
 */
public class WriteCommentActivity extends BaseActivity {

    private Status          mStatus;
    private Comment         mComment;
    private int             mType;
    private String          mUri;
    private Intent          mIntent;
    private CommentsAPI     mCommentsAPI;
    private WeiboParameters mParameters;

    private TextView        pwb_et_write_status;

    final private int REPLY_COMMENT     = 1;
    final private int CREATE_COMMENT    = 2;

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

        mStatus = (Status) getIntent().getSerializableExtra("status");
        mComment = (Comment) getIntent().getSerializableExtra("comment");
        mType = getIntent().getIntExtra("type", 0);

        mCommentsAPI = new CommentsAPI(this, Constants.APP_KEY, mAccessToken);
        mParameters = new WeiboParameters(Constants.APP_KEY);
    }

    /** 发送评论的实现 */
    private void sendComment() {
        final String comment = pwb_et_write_status.getText().toString();

        if (TextUtils.isEmpty(comment)) {
            ToastUtils.showToast(this, "评论不能为空,请输入内容", Toast.LENGTH_LONG);
            return;
        }

        request(mType, comment);

    }

    /** 发送网络请求,添加评论 */
    private void request(int type, String comment) {
        switch (type) {
            case CREATE_COMMENT:
                mCommentsAPI.create(comment, mStatus.getId(), false, new RequestListener() {
                    @Override
                    public void onComplete(String s) {
                        mIntent = new Intent();
                        mIntent.putExtra("sendCommentSuccess", true);
                        setResult(RESULT_OK, mIntent);
                        WriteCommentActivity.this.finish();
                    }

                    @Override
                    public void onWeiboException(WeiboException e) {
                        ToastUtils.showToast(WriteCommentActivity.this, "网络异常,请再次提交", Toast.LENGTH_SHORT);
                    }
                });
                break;

            case REPLY_COMMENT:
                mCommentsAPI.reply(mComment.getId(), mComment.getStatus().getId(),comment, false, false, new RequestListener() {
                    @Override
                    public void onComplete(String s) {
                        mIntent = new Intent();
                        mIntent.putExtra("sendCommentSuccess", true);
                        setResult(RESULT_OK, mIntent);
                        WriteCommentActivity.this.finish();
                    }
                    @Override
                    public void onWeiboException(WeiboException e) {
                        ToastUtils.showToast(WriteCommentActivity.this, "网络异常,请再次提交", Toast.LENGTH_SHORT);
                    }
                });
                break;

            case 0:
                mIntent = new Intent();
                mIntent.putExtra("sendCommentSuccess", false);
                setResult(RESULT_CANCELED, mIntent);
                WriteCommentActivity.this.finish();
                break;

            default:
                break;
        }
    }
}