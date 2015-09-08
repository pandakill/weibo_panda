package com.panda.pweibo.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.panda.pweibo.R;
import com.panda.pweibo.adapter.StatusAdapter;
import com.panda.pweibo.constants.Uri;
import com.panda.pweibo.models.Status;
import com.panda.pweibo.models.User;
import com.panda.pweibo.utils.TitlebarUtils;
import com.panda.pweibo.utils.ToastUtils;
import com.panda.pweibo.widget.Pull2RefreshListView;
import com.panda.pweibo.widget.UnderlineIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class UserInfoActivity extends BaseActivity implements
        OnClickListener, OnItemClickListener, OnCheckedChangeListener {



    private boolean         mIsCurrentUser;
    private User            mUser;
    private String          mUserName;

    private List<Status>    mListStatus;
    private StatusAdapter   mStatusAdapter;
    private long            mCurPage = 1;
    private boolean         mIsLoadingMore;
    private int             mCurScrollY;

    /** 下拉刷新标记 */
    private boolean         IS_REFLASHING = false;

    private int minImageHeight = -1;
    private int maxImageHeight = -1;

    private View title_bar;
    private TextView titlebar_tv;

    private View user_info_head;
    private NetworkImageView iv_avatar;
    private TextView tv_name;
    private TextView tv_follows;
    private TextView tv_fans;
    private TextView tv_sign;

    private View shadow_user_info_tab;
    private RadioGroup shadow_rg_user_info;
    private UnderlineIndicatorView shadow_uliv_user_info;
    private View user_info_tab;
    private RadioGroup rg_user_info;
    private UnderlineIndicatorView uliv_user_info;

    private ImageView iv_user_info_head;
    private Pull2RefreshListView plv_user_info;
    private View footView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_info);
        Intent intent = getIntent();

        mUserName = intent.getStringExtra("userName");
        mListStatus = new ArrayList<>();

        if(TextUtils.isEmpty(mUserName)) {
            mIsCurrentUser = true;
            mUser = new User();
        }

        initView();

        loadData();
    }

    private void initView() {
        title_bar = new TitlebarUtils(this)
                .setTitlebarTvLeft("返回")
                .setLeftOnClickListner(this)
                .build();
        titlebar_tv = (TextView) title_bar.findViewById(R.id.titlebar_textview_left);
        initInfoHead();
        initTab();
        initListView();
    }

    private void initInfoHead() {
        iv_user_info_head = (ImageView) findViewById(R.id.iv_user_info_head);
        user_info_head = View.inflate(this, R.layout.user_info_head, null);
        iv_avatar = (NetworkImageView) user_info_head.findViewById(R.id.iv_avatar);
        tv_name = (TextView) user_info_head.findViewById(R.id.tv_name);
        tv_follows = (TextView) user_info_head.findViewById(R.id.tv_follows);
        tv_fans = (TextView) user_info_head.findViewById(R.id.tv_fans);
        tv_sign = (TextView) user_info_head.findViewById(R.id.tv_sign);
        iv_avatar.setOnClickListener(this);
    }

    private void initTab() {
        shadow_user_info_tab = findViewById(R.id.user_info_tab);
        shadow_rg_user_info = (RadioGroup) findViewById(R.id.rg_user_info);
        shadow_uliv_user_info = (UnderlineIndicatorView) findViewById(R.id.uliv_user_info);

        shadow_rg_user_info.setOnCheckedChangeListener(this);
        shadow_uliv_user_info.setCurrentItemWithoutAnim(1);

        user_info_tab = View.inflate(this, R.layout.user_info_tab, null);
        rg_user_info = (RadioGroup) user_info_tab.findViewById(R.id.rg_user_info);
        uliv_user_info = (UnderlineIndicatorView) user_info_tab.findViewById(R.id.uliv_user_info);

        rg_user_info.setOnCheckedChangeListener(this);
        uliv_user_info.setCurrentItemWithoutAnim(1);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void initListView() {
        plv_user_info = (Pull2RefreshListView) findViewById(R.id.plv_user_info);
        initLoadingLayout(plv_user_info.getLoadingLayoutProxy());
        footView = View.inflate(this, R.layout.footer_loading, null);
        final ListView lv = plv_user_info.getRefreshableView();
        mStatusAdapter = new StatusAdapter(this, mListStatus, mImageLoader);
        plv_user_info.setAdapter(mStatusAdapter);
        lv.addHeaderView(user_info_head);
        lv.addHeaderView(user_info_tab);
        plv_user_info.setOnRefreshListener(new OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                IS_REFLASHING = true;
                loadStatuses(1);
            }
        });
        plv_user_info.setOnLastItemVisibleListener(
                new OnLastItemVisibleListener() {
                    @Override
                    public void onLastItemVisible() {
                        loadStatuses(mCurPage + 1);
                    }
                });

        plv_user_info.setOnPlvScrollListener(new Pull2RefreshListView.OnPlvScrollListener() {

            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {
                int scrollY = mCurScrollY = t;

                if (minImageHeight == -1) {
                    minImageHeight = iv_user_info_head.getHeight();
                }

                if (maxImageHeight == -1) {
                    Rect rect = iv_user_info_head.getDrawable().getBounds();
                    maxImageHeight = rect.bottom - rect.top;
                }
//              minImageHeight = DisplayUtils.dp2px(UserInfoActivity.this, 244);
//              maxImageHeight = DisplayUtils.dp2px(UserInfoActivity.this, 360);

                int scaleImageDistance = maxImageHeight - minImageHeight;

                if (-scrollY < scaleImageDistance) {
                    iv_user_info_head.layout(0, 0,
                            iv_user_info_head.getWidth(),
                            minImageHeight - scrollY);
                } else {
                    iv_user_info_head.layout(0, -scaleImageDistance - scrollY,
                            iv_user_info_head.getWidth(),
                            -scaleImageDistance - scrollY + iv_user_info_head.getHeight());
                }
            }
        });
        iv_user_info_head.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (mCurScrollY == bottom - oldBottom) {
                    iv_user_info_head.layout(0, 0,
                            iv_user_info_head.getWidth(),
                            oldBottom);
                }
            }
        });
        plv_user_info.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                iv_user_info_head.layout(0,
                        user_info_head.getTop(),
                        iv_user_info_head.getWidth(),
                        user_info_head.getTop() + iv_user_info_head.getHeight());

                if (user_info_head.getBottom() < title_bar.getBottom()) {
                    shadow_user_info_tab.setVisibility(View.VISIBLE);
                    title_bar.setBackgroundResource(R.drawable.navigationbar_background);
//                    titlebar_iv_left.setImageResource(R.drawable.navigationbar_back_sel);
                    titlebar_tv.setVisibility(View.VISIBLE);
                } else {
                    shadow_user_info_tab.setVisibility(View.GONE);
                    title_bar.setBackgroundResource(R.drawable.userinfo_navigationbar_background);
//                    titlebar_iv_left.setImageResource(R.drawable.userinfo_navigationbar_back_sel);
                    titlebar_tv.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initLoadingLayout(ILoadingLayout loadingLayout) {
        loadingLayout.setLoadingDrawable(new ColorDrawable(R.color.transparent));
        loadingLayout.setPullLabel("");
        loadingLayout.setReleaseLabel("");
        loadingLayout.setRefreshingLabel("");
    }

    private void loadData() {
        if(mIsCurrentUser) {
            setUserInfo();
        } else {
            loadUserInfo();
        }
        loadStatuses(1);
    }

    /** 填充数据 */
    private void setUserInfo() {
        if(mUser == null) {
            return;
        }
        // 填充数据
        tv_name.setText(mUser.getName());
        titlebar_tv.setText("返回");
        tv_follows.setText("关注 " + mUser.getFriends_count());
        tv_fans.setText("粉丝 " + mUser.getFollowers_count());
        tv_sign.setText("简介:" + mUser.getDescription());
        ImageListener imageListener = ImageLoader.getImageListener(iv_avatar,
                R.drawable.ic_com_sina_weibo_sdk_logo,
                R.drawable.ic_com_sina_weibo_sdk_logo);
        mImageLoader.get(mUser.getProfile_image_url(), imageListener);
    }

    /** 读取用户信息 */
    private void loadUserInfo() {
        String uri = Uri.USER_SHOW;
        uri += "?access_token=" + mAccessToken.getToken();
        mUserName = URLEncoder.encode(mUserName);
        uri += "&screen_name=" + mUserName;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, uri, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.has("error")) {
                                int errorCode = response.getInt("error_code");
                                String error = response.getString("error");
                                ToastUtils.showToast(UserInfoActivity.this,
                                        "错误码：" + errorCode + "-" + error,
                                        Toast.LENGTH_SHORT);
                            } else {
                                mUser = new User().parseJson(response);
                                setUserInfo();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ToastUtils.showToast(UserInfoActivity.this, "网络发生异常", Toast.LENGTH_SHORT);
            }
        });

        mRequestQueue.add(jsonObjectRequest);
    }

    /** 加载用户最近的微博 */
    private void loadStatuses(final long requestPage) {

        if(mIsLoadingMore) {
            return;
        }

        mIsLoadingMore = true;
        String uri = Uri.STATUS_USER_TIMELINE;
        uri += "?access_token=" + mAccessToken.getToken();
        uri += "&screen_name=" + mUserName;
        uri += "&since_id=0&max_id=0&count=25&base_app=0&feature=0&trim_user=0&page=" + requestPage;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, uri, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.has("error")) {
                                int errorCode = response.getInt("error_code");
                                String error = response.getString("error");
                                if ( errorCode == 21335) {
                                    ToastUtils.showToast(UserInfoActivity.this,
                                            "应用未添加该测试用户",
                                            Toast.LENGTH_SHORT);
                                } else {
                                    ToastUtils.showToast(UserInfoActivity.this,
                                            "错误码：" + errorCode + "-" + error,
                                            Toast.LENGTH_SHORT);
                                }
                            } else {
                                JSONArray jsonArray = response.getJSONArray("statuses");

                                if (requestPage == 1) {
                                    mListStatus.clear();
                                }

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    Status status = new Status().parseJson(jsonArray.getJSONObject(i));
                                    mListStatus.add(status);
                                }
                                long totalNum = response.getLong("total_number");

                                addStatus(mListStatus, totalNum);
                                if (IS_REFLASHING) {
                                    plv_user_info.onRefreshComplete();
                                }

                                mIsLoadingMore = false;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ToastUtils.showToast(UserInfoActivity.this, "网络发生异常", Toast.LENGTH_SHORT);
            }
        });

        mRequestQueue.add(jsonObjectRequest);
    }

    private void addStatus(List<Status> listStatus, long totalNumber) {
        for(Status status : listStatus) {
            if(!mListStatus.contains(status)) {
                mListStatus.add(status);
            }
        }
        mStatusAdapter.notifyDataSetChanged();

        if(mCurPage < totalNumber) {
            addFootView(plv_user_info, footView);
        } else {
            removeFootView(plv_user_info, footView);
        }
    }

    private void addFootView(PullToRefreshListView plv, View footView) {
        ListView lv = plv.getRefreshableView();
        if(lv.getFooterViewsCount() == 1) {
            lv.addFooterView(footView);
        }
    }

    private void removeFootView(PullToRefreshListView plv, View footView) {
        ListView lv = plv.getRefreshableView();
        if(lv.getFooterViewsCount() > 1) {
            lv.removeFooterView(footView);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titlebar_textview_left:
                UserInfoActivity.this.finish();
                break;

            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ToastUtils.showToast(this, "onItemClick", Toast.LENGTH_SHORT);
//        Intent intent = new Intent(this, ImageBrowserActivity.class);
//    	intent.putExtra("status", status);
//        intent.putExtra("position", position);
//        startActivity(intent);
    }

    /** tab和shadow_tab保持一致性 */
    private void syncRadioButton(RadioGroup group, int checkedId) {
        int index = group.indexOfChild(group.findViewById(checkedId));

        if(shadow_user_info_tab.getVisibility() == View.VISIBLE) {
            shadow_uliv_user_info.setCurrentItem(index);

            ((RadioButton) rg_user_info.findViewById(checkedId)).setChecked(true);
            uliv_user_info.setCurrentItemWithoutAnim(index);
        } else {
            uliv_user_info.setCurrentItem(index);

            ((RadioButton) shadow_rg_user_info.findViewById(checkedId)).setChecked(true);
            shadow_uliv_user_info.setCurrentItemWithoutAnim(index);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        syncRadioButton(group, checkedId);
    }

}
