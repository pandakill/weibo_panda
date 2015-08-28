package com.panda.pweibo.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.panda.pweibo.R;
import com.panda.pweibo.adapter.MessageAdapter;
import com.panda.pweibo.constants.Uri;
import com.panda.pweibo.models.Comment;
import com.panda.pweibo.utils.TitlebarUtils;
import com.panda.pweibo.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 打开消息列表的activity
 *
 * Created by Administrator on 2015/8/27:14:23.
 */
public class MessageActivity extends BaseActivity implements OnClickListener {

    private MessageAdapter          mAdapter;
    private List<Comment>           mCommentList;
    private long                    mTotalNum;
    private ProgressDialog          mPd;
    private Intent                  mIntent;
    private int                     mMessageType;
    private String                  mUri;

    private PullToRefreshListView    pwb_plv_messages;
    private LinearLayout             foot_view;

    private long                     mCurPage = 1;
    private int                      FLAG = 1;

    final private int MESSAGE_AT        = 1;
    final private int MESSAGE_COMMENT   = 2;
    final private int MESSAGE_GOOD      = 3;
    final private int MESSAGE_BOX       = 4;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_message);

        mMessageType = getIntent().getIntExtra("messageType", 0);

        initView();
        loadData(1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titlebar_textview_left:
                MessageActivity.this.finish();
                break;

            case R.id.titlebar_textview_right:
                ToastUtils.showToast(MessageActivity.this, "设置", Toast.LENGTH_SHORT);
                break;

            default:
                break;
        }
    }

    private void initView() {

        String titleBarContent = "";

        switch (mMessageType) {
            case MESSAGE_AT:
                mUri = Uri.COMMENTS_MESSION;
                titleBarContent = "@我的的评论";
                break;
            case MESSAGE_COMMENT:
                mUri = Uri.COMMENTS_TO_ME;
                titleBarContent = "所有评论";
                break;
            case MESSAGE_GOOD:
                break;
            case MESSAGE_BOX:
                break;
            default:
                break;
        }

        new TitlebarUtils(this)
                .setTitleContent(titleBarContent)
                .setTitlebarTvLeft("返回")
                .setTitlebarTvRight("设置")
                .setLeftOnClickListner(this)
                .setRightOnClickListner(this);

        mCommentList = new ArrayList<>();
        pwb_plv_messages = (PullToRefreshListView) this.findViewById(R.id.pwb_plv_messages);
        foot_view = (LinearLayout) View.inflate(this, R.layout.footer_loading, null);

        mAdapter = new MessageAdapter(this, mCommentList, mImageLoader);

        pwb_plv_messages.setAdapter(mAdapter);

        pwb_plv_messages.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                loadData(1);
            }
        });

        pwb_plv_messages.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                loadData(mCurPage + 1);
            }
        });
    }

    /** 读取我的评论列表 */
    private void loadData(final long page) {

        if (page == 1) {
            mCommentList.clear();
        }

        mUri += "?access_token=" + mAccessToken.getToken();
        mUri += "&page=" + page;

        if (FLAG == 1) {
            mPd = ProgressDialog.show(this, "获取数据", "加载ing");
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, mUri, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("comments");
                            for (int i = 0; i < jsonArray.length(); i ++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Comment comment = new Comment().parseJson(jsonObject);
                                mCommentList.add(comment);
                            }

                            mTotalNum = response.getLong("total_number");
                            mCurPage = page;
                            addData(mCommentList, mTotalNum);

                            mPd.dismiss();
                            FLAG ++;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        ToastUtils.showToast(MessageActivity.this, "网络发生错误,加载失败", Toast.LENGTH_SHORT);
                        mPd.dismiss();
                        FLAG ++;
                        MessageActivity.this.finish();
                    }
                });

        // 将请求加入volley队列中
        mRequestQueue.add(jsonObjectRequest);
    }

    private void requesetData() {

    }

    private void addData(List<Comment> listComments, long totalNum) {
        for (Comment comment : listComments) {
            if (!listComments.contains(comment)) {
                listComments.add(comment);
            }
        }

        mAdapter.notifyDataSetChanged();

        if (listComments.size() < totalNum) {
            addFootView(pwb_plv_messages, foot_view);
        } else {
            removeFootView(pwb_plv_messages, foot_view);
        }
    }



    /** 添加底部的刷新view */
    private void addFootView(PullToRefreshListView plv, View footView) {
        ListView lv = plv.getRefreshableView();
        if (lv.getFooterViewsCount() == 1) {
            lv.addFooterView(footView);
        }
    }

    /** 去除底部的刷新view */
    private void removeFootView(PullToRefreshListView plv, View footView) {
        ListView lv = plv.getRefreshableView();
        if (lv.getFooterViewsCount() > 1) {
            lv.removeFooterView(footView);
        }
    }
}
