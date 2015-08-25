package com.panda.pweibo.fragment;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.LruCache;
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
public class HomeFragment extends Fragment {

    private     View                        view;
    private     PullToRefreshListView       listView;
    private     ProgressDialog              pd;
    private     int                         curPage;
    private     StatusAdapter               adapter;
    private     List<Status>                listStatus;
    private     View                        footer_loading;
    private     int                         totalNum;
    private     Oauth2AccessToken           mAccesssToken;

    protected   MainActivity                activity;

    private     int flag = 1;

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

        activity = (MainActivity) getActivity();
        mAccesssToken = AccessTokenKeeper.readAccessToken(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        initView();
        loadData(1);
        return view;
    }

    private void initView() {

        view = View.inflate(activity, R.layout.fragment_home, null);
        footer_loading = View.inflate(activity, R.layout.footer_loading, null);
        listView = (PullToRefreshListView) view.findViewById(R.id.listview_home);

        listStatus = new ArrayList<>();

        new TitlebarUtils(view)
                .setTitlebarTvLeft("LEFT")
                .setTitleContent("首页")
                .setTitlebarIvRight(R.drawable.pwb_icon_share_black)
                .setLeftOnClickListner(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastUtils.showToast(activity, "LEFT被点击", Toast.LENGTH_LONG);
                    }
                })
                .setRightOnClickListner(new LogoutListener(activity, mAccesssToken,
                        activity.requestQueue, this));

        /** 初始化listView控件,实例适配器，设置listView的适配器 */
        adapter = new StatusAdapter(activity, listStatus, activity.requestQueue);
        listView.setAdapter(adapter);
        listView.setOnRefreshListener(new OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                loadData(1);
                listView.onRefreshComplete();
            }
        });
        listView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                loadData(curPage + 1);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public void loadData(final int page) {

        if (flag == 1) {
            pd = ProgressDialog.show(activity, "获取微博", "正在加载中");
        }

        /**
         * TODO 测试的token有效期为一天，所以必须每天重新授权获取token
         */
        String uri = Uri.home_timeline;
        uri += "?access_token=" + mAccesssToken.getToken();
        uri += "&since_id=0&max_id=0&count=25&base_app=0&feature=0&trim_user=0&page=" + page;

        /** volley发送JsonObjectRequest请求,请求成功后将json数据处理并设置listview的适配器 */
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, uri,
                null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    /** 如果页码为1,则清空listStatus的数据, */
                    if (page == 1) {
                        listStatus.clear();
                    }

                    JSONArray statuses = response.getJSONArray("statuses");
                    for (int i = 0; i < statuses.length(); i ++) {
                        // 将jsonObjec转换为status对象,并添加至listStatus当中
                        Status status = new Status().parseJson(statuses.getJSONObject(i));
                        listStatus.add(status);
                    }

                    // json返回的微博总数
                    totalNum = response.getInt("total_number");
                    curPage = page;
                    addData(listStatus, totalNum);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                pd.dismiss();
                flag ++;

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                ToastUtils.showToast(activity, "加载失败", Toast.LENGTH_SHORT);
            }
        });
        /** 将请求加入请求队列当中 */
        activity.requestQueue.add(jsonObjectRequest);
    }

    /** 往listStatus中加数据,将其加入缓存,在listview中输出 */
    private void addData(List<Status> listStatus, int totalNumber) {
        for (Status status : listStatus) {
            if (!listStatus.contains(status)) {
                listStatus.add(status);
            }
        }

        adapter.notifyDataSetChanged();

        if (curPage < totalNumber) {
            addFootView(listView, footer_loading);
        } else {
            removeFootView(listView, footer_loading);
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
