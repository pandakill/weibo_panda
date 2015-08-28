package com.panda.pweibo.fragment;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.panda.pweibo.MyFragment;
import com.panda.pweibo.R;
import com.panda.pweibo.activity.MainActivity;
import com.panda.pweibo.adapter.StatusAdapter;
import com.panda.pweibo.constants.AccessTokenKeeper;
import com.panda.pweibo.constants.Uri;
import com.panda.pweibo.listener.LogoutListener;
import com.panda.pweibo.models.Status;
import com.panda.pweibo.utils.TitlebarUtils;
import com.panda.pweibo.utils.ToastUtils;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class HomeFragment extends MyFragment {

    private     View                        mView;
    private     PullToRefreshListView       mPlv;
    private     ProgressDialog              mPd;
    private     int                         mCurPage;
    private     StatusAdapter               mAdapter;
    private     List<Status>                mStatusList;
    private     int                         mTotalNum;
    private     Oauth2AccessToken           mAccesssToken;

    protected   MainActivity                mActivity;

    /** 视图控件 */
    private     View                        footer_loading;

    private int FLAG = 1;

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

        mActivity = (MainActivity) getActivity();
        mAccesssToken = AccessTokenKeeper.readAccessToken(mActivity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        initView();
        loadData(1);
        return mView;
    }

    private void initView() {

        mView = View.inflate(mActivity, R.layout.fragment_home, null);
        footer_loading = View.inflate(mActivity, R.layout.footer_loading, null);
        mPlv = (PullToRefreshListView) mView.findViewById(R.id.listview_home);

        mStatusList = new ArrayList<>();

        new TitlebarUtils(mView)
                .setTitlebarTvLeft("LEFT")
                .setTitleContent("首页")
                .setTitlebarIvRight(R.drawable.pwb_icon_share_black)
                .setLeftOnClickListner(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastUtils.showToast(mActivity, "LEFT被点击", Toast.LENGTH_LONG);
                    }
                })
                .setRightOnClickListner(new LogoutListener(mActivity, mAccesssToken,
                        mActivity.mRequestQueue, this));

        // 初始化listView控件,实例适配器，设置listView的适配器
        mAdapter = new StatusAdapter(mActivity, mStatusList, mActivity.mImageLoader);
        mPlv.setAdapter(mAdapter);
        mPlv.setOnRefreshListener(new OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                loadData(1);
                mPlv.onRefreshComplete();
            }
        });
        mPlv.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                loadData(mCurPage + 1);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public void loadData(final int page) {

        if (FLAG == 1) {
            mPd = ProgressDialog.show(mActivity, "获取微博", "正在加载中");
        }

        // TODO 测试的token有效期为一天，所以必须每天重新授权获取token
        String uri = Uri.STATUS_HOME_TIMELINE;
        uri += "?access_token=" + mAccesssToken.getToken();
        uri += "&since_id=0&max_id=0&count=25&base_app=0&feature=0&trim_user=0&page=" + page;

        // volley发送JsonObjectRequest请求,请求成功后将json数据处理并设置listview的适配器
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, uri,
                null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    // 如果页码为1,则清空listStatus的数据
                    if (page == 1) {
                        mStatusList.clear();
                    }

                    JSONArray statuses = response.getJSONArray("statuses");
                    for (int i = 0; i < statuses.length(); i ++) {
                        // 将jsonObjec转换为status对象,并添加至listStatus当中
                        Status status = new Status().parseJson(statuses.getJSONObject(i));
                        mStatusList.add(status);
                    }

                    // json返回的微博总数
                    mTotalNum = response.getInt("total_number");
                    mCurPage = page;
                    addData(mStatusList, mTotalNum);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mPd.dismiss();
                FLAG ++;

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                ToastUtils.showToast(mActivity, "网络发生异常,加载失败", Toast.LENGTH_SHORT);
                mPd.dismiss();
                FLAG ++;
            }
        });
        // 将请求加入请求队列当中
        mActivity.mRequestQueue.add(jsonObjectRequest);
    }

    /** 往listStatus中加数据,将其加入缓存,在listview中输出 */
    private void addData(List<Status> listStatus, int totalNumber) {
        for (Status status : listStatus) {
            if (!listStatus.contains(status)) {
                listStatus.add(status);
            }
        }

        mAdapter.notifyDataSetChanged();

        if (mCurPage < totalNumber) {
            addFootView(mPlv, footer_loading);
        } else {
            removeFootView(mPlv, footer_loading);
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
