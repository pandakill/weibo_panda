package com.panda.pweibo.view;

import android.app.ProgressDialog;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.panda.pweibo.R;
import com.panda.pweibo.activity.StatusDetailActivity;
import com.panda.pweibo.adapter.StatusCommentAdapter;
import com.panda.pweibo.constants.Uri;
import com.panda.pweibo.models.Comment;
import com.panda.pweibo.models.Status;
import com.panda.pweibo.models.User;
import com.panda.pweibo.utils.ToastUtils;
import com.panda.pweibo.widget.Pull2RefreshListView;
import com.panda.pweibo.widget.Underline;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 评论的下拉刷新listview
 *
 * Created by Administrator on 2015/9/12:11:52.
 */
public class CommentListView implements View.OnClickListener {

    // 下拉刷新的评论列表
    public  Pull2RefreshListView    listView;

    private StatusDetailActivity    mActivity;
    private StatusCommentAdapter    mAdapter;
    private List<Comment>           mCommentList;
    private ImageLoader             mImageLoader;
    private Status                  mStatus;
    private long                    mCurPage = 1;
    public  Boolean                 mScroll2Comment = false;
    private Boolean                 IS_REFRESH = false;

    private final int flag = 0;

    private View mStatusDetailView;

    /** 加载完毕标记 */
    private boolean IS_REFLASHING = false;

    /** 加载更多的view */
    private View foot_view;

    /** 中部viewgroup控件 */
    private View            include_status_detail_tab;
    private LinearLayout    pwb_ll_status_detail;
    private TextView        pwb_tv_share;
    private TextView        pwb_tv_comment;
    private TextView        pwb_tv_praise;
    private Underline       pwb_ul_status_detail;

    /** 顶部悬浮的菜单栏控件 */
    private View            shadow_status_detail_tab;
    private LinearLayout    shadow_ll_status_detail;
    private TextView        shadow_tv_share;
    private TextView        shadow_tv_comment;
    private TextView        shadow_tv_praise;
    private Underline       shadow_ul_status_detail;


    /**
     * 构造函数,调用初始化函数
     * @param activity 指定的StatusDetailActivity
     * @param loader 单例的imageloader
     */
    public CommentListView (StatusDetailActivity activity,
                            ImageLoader loader, Status status, View statusView) {
        init(activity, loader, status, statusView);
    }

    /**
     * 初始化函数,初始该类的变量,并设置listview的适配器
     * @param activity 指定的StatusDetailActivity
     * @param loader 单例的imageloader
     */
    private void init(StatusDetailActivity activity,
                      ImageLoader loader, Status status, View statusView) {
        mActivity = activity;
        listView = (Pull2RefreshListView) View.inflate(mActivity, R.layout.item_status_detail, null);
        mCommentList = new ArrayList<>();
        mImageLoader = loader;
        mStatus = status;
        mStatusDetailView = statusView;

        loadData(1);
        initTab();
        initLstView();

        mAdapter = new StatusCommentAdapter(mActivity, mCommentList, mImageLoader);
        listView.setAdapter(mAdapter);
    }

    /** 初始化listview */
    private void initLstView() {

        foot_view = View.inflate(mActivity, R.layout.footer_loading, null);

        final ListView lv = listView.getRefreshableView();
//        lv.addHeaderView(mStatusDetailView);
        lv.addHeaderView(include_status_detail_tab);

        // 下拉刷新监听
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                IS_REFLASHING = true;
                loadData(1);
            }
        });

        // 滑动到最后一个item的监听
        listView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                loadData(mCurPage + 1);
            }
        });

        // 设置滚动监听器
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // 如果滚动到tab第一个item时,则显示顶部隐藏的shadow_tab,作为悬浮菜单栏
                shadow_ll_status_detail.setVisibility(firstVisibleItem >= 2 ? View.VISIBLE : View.GONE);
            }
        });
    }

    /** 传入页码,加载评论 */
    public void loadData(final long page) {
        String uri = Uri.COMMENTS_SHOW;
        uri += "?access_token=" + mActivity.getmAccessToken().getToken() + "&id=" + mStatus.getId();
        uri += "&page="+page;

        final ProgressDialog pd = ProgressDialog.show(mActivity, "加载评论", "正在加载ing...");

        /** 发送volley的JsonObjectRequest请求 */
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, uri,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    /** 如果页码为1,则将评论listComments清空 */
                    if (page == 1) {
                        mCommentList.clear();
                    }

                    JSONArray jsonArray = response.getJSONArray("comments");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Comment comment = new Comment();
                        comment = comment.parseJson(jsonArray.getJSONObject(i));
                        mCommentList.add(comment);
                    }

                    long totalNum = response.getLong("total_number");

                    mCurPage = page;

                    if (!IS_REFRESH) {
                        addData(mCommentList, totalNum);
                        if (IS_REFLASHING) {
                            IS_REFLASHING = false;
                            listView.onRefreshComplete();
                        }

                        /** 判断是否需要滚动至评论部分 */
                        if (mScroll2Comment) {
                            listView.getRefreshableView().setSelection(2);
                            mScroll2Comment = false;
                        }
                        IS_REFRESH = true;
                    } else {
                        listView.onRefreshComplete();
                        IS_REFRESH = false;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ToastUtils.showToast(mActivity, "加载评论失败", Toast.LENGTH_SHORT);
            }
        });

        pd.dismiss();

        mActivity.mRequestQueue.add(jsonObjectRequest);
    }

    /** 往listView中加item */
    private void addData(List<Comment> listComments, long totalNum) {
        for (Comment comment : listComments) {
            if (!listComments.contains(comment)) {
                listComments.add(comment);
            }
        }

        mAdapter.notifyDataSetChanged();

        if (listComments.size() < totalNum) {
            addFootView(listView, foot_view);
        } else {
            removeFootView(listView, foot_view);
        }
    }

    /** 添加底部的刷新view */
    private void addFootView(Pull2RefreshListView plv, View footView) {
        ListView lv = plv.getRefreshableView();
        if (lv.getFooterViewsCount() == 1) {
            lv.addFooterView(footView);
        }
    }

    /** 去除底部的刷新view */
    private void removeFootView(Pull2RefreshListView plv, View footView) {
        ListView lv = plv.getRefreshableView();
        if (lv.getFooterViewsCount() > 1) {
            lv.removeFooterView(footView);
        }
    }

    /** 初始化中部菜单栏（导航栏） */
    private void initTab() {
        /** 位于微博详细下面的菜单栏 */
        include_status_detail_tab    = View.inflate(mActivity,R.layout.include_status_detail_tab, null);
        pwb_ll_status_detail = (LinearLayout)   include_status_detail_tab.findViewById(R.id.pwb_ll_status_detail);
        pwb_tv_share        = (TextView)  pwb_ll_status_detail.findViewById(R.id.pwb_tv_share);
        pwb_tv_comment      = (TextView)  pwb_ll_status_detail.findViewById(R.id.pwb_tv_comment);
        pwb_tv_praise       = (TextView)  pwb_ll_status_detail.findViewById(R.id.pwb_tv_praise);
        pwb_ul_status_detail           = (Underline)  include_status_detail_tab.findViewById(R.id.pwb_ul_status_detail);
        pwb_tv_share.setOnClickListener(this);
        pwb_tv_comment.setOnClickListener(this);
        pwb_tv_praise.setOnClickListener(this);
        pwb_ul_status_detail.setCurrentItemWithoutAni(1);

        /** 悬浮的菜单栏 */
        shadow_status_detail_tab    = mActivity.findViewById(R.id.include_status_detail_tab);
        shadow_ll_status_detail = (LinearLayout)   shadow_status_detail_tab.findViewById(R.id.pwb_ll_status_detail);
        shadow_tv_share        = (TextView)  shadow_ll_status_detail.findViewById(R.id.pwb_tv_share);
        shadow_tv_comment      = (TextView)  shadow_ll_status_detail.findViewById(R.id.pwb_tv_comment);
        shadow_tv_praise       = (TextView)  shadow_ll_status_detail.findViewById(R.id.pwb_tv_praise);
        shadow_ul_status_detail           = (Underline)  shadow_status_detail_tab.findViewById(R.id.pwb_ul_status_detail);
        shadow_ul_status_detail.setCurrentItemWithoutAni(1);

        /** 设置tab的内容 */
        pwb_tv_share.setText("转发 " + mStatus.getReposts_count());
        pwb_tv_comment.setText("评论 " + mStatus.getComments_count());
        pwb_tv_praise.setText("赞 " + mStatus.getAttitudes_count());
        shadow_tv_share.setText("转发 " + mStatus.getReposts_count());
        shadow_tv_comment.setText("评论 " + mStatus.getComments_count());
        shadow_tv_praise.setText("赞 " + mStatus.getAttitudes_count());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pwb_tv_share:
                syncTextView(0);
                break;

            case R.id.pwb_tv_comment:
                syncTextView(1);
                break;

            case R.id.pwb_tv_praise:
                syncTextView(2);
                break;
        }
    }

    /** tab和shadow_tab保持一致性 */
    private void syncTextView(int index) {
        // 如果隐藏的tab显示出来、则将隐藏的tab部分的radioGroup的底部橙色高亮部分进行滑动效果
        if(shadow_status_detail_tab.getVisibility() == View.VISIBLE) {
            // 将radioGroup的底部橙色高亮进行滑动效果
            shadow_ul_status_detail.setCurrentItem(index);
            pwb_ul_status_detail.setCurrentItemWithoutAni(index);
        } else {
            // 将非隐藏的tab部分的radioGroup的底部橙色高亮部分进行滑动效果
            pwb_ul_status_detail.setCurrentItem(index);
            shadow_ul_status_detail.setCurrentItemWithoutAni(index);
        }
    }
}
