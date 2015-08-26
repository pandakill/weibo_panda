package com.panda.pweibo.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.panda.pweibo.widget.Pull2RefreshListView;
import com.panda.pweibo.R;
import com.panda.pweibo.adapter.StatusCommentAdapter;
import com.panda.pweibo.adapter.StatusGridViewAdapter;
import com.panda.pweibo.constants.AccessTokenKeeper;
import com.panda.pweibo.constants.Uri;
import com.panda.pweibo.models.Comment;
import com.panda.pweibo.models.PicUrls;
import com.panda.pweibo.models.Status;
import com.panda.pweibo.models.User;
import com.panda.pweibo.utils.DateUtils;
import com.panda.pweibo.utils.StringUtils;
import com.panda.pweibo.utils.TitlebarUtils;
import com.panda.pweibo.utils.ToastUtils;
import com.panda.pweibo.widget.WrapHeightGridView;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/8/26:9:52.
 */
public class StatusDetailActivity extends Activity implements OnClickListener {

    private Status              status;
    private ImageLoader         imageLoader;
    private RequestQueue        requestQueue;
    private ImageCache          imageCache;
    private LruCache<String, Bitmap> lruCache;
    private StatusCommentAdapter adapter;
    private List<Comment>       listComments;
    private Oauth2AccessToken   mAccessToken;
    private long                curPage = 1;
    private long                totalNum;

    /** 微博信息的控件 */
    private View                status_detail_info;
    private ImageView           pwb_imageview_item_status_avatar;
    private TextView            pwb_textview_sender;
    private TextView            pwb_textview_item_status_from_and_when;
    private TextView            pwb_textview_content;
    private FrameLayout         include_status_image;
    private WrapHeightGridView  pwb_gridview_status_image;
    private NetworkImageView    pwb_imageview_status_image;
    private View                include_retweeted_status;
    private TextView            pwb_textview_retweeted_content;
    private FrameLayout         include_retweeted_status_image;
    private WrapHeightGridView  pwb_gridview_retweeted_status_image;
    private NetworkImageView    pwb_imageview_retweeted_status_image;

    /** 中部viewgroup控件 */
    private View            include_status_detail_tab;
    private RadioGroup      pwb_radiogroup_status_detail;
    private RadioButton     pwb_radiobutton_share;
    private RadioButton     pwb_radiobutton_comment;
    private RadioButton     pwb_radiobutton_praise;

    /** 顶部悬浮的菜单栏控件 */
    private View            shadow_status_detail_tab;
    private RadioGroup      shadow_radiogroup_status_detail;
    private RadioButton     shadow_radiobutton_share;
    private RadioButton     shadow_radiobutton_comment;
    private RadioButton     shadow_radiobutton_praise;


    /** 下拉刷新控件 */
    private Pull2RefreshListView   pwb_plv_status_detail;

    /** 加载更多的view */
    private View footView;

    /** 底部控件 */
    private LinearLayout    inlude_status_control;
    private LinearLayout    pwb_ll_share_tottom;
    private TextView        textview_share_bottom;
    private LinearLayout    pwb_ll_comment_tottom;
    private TextView        textview_comment_bottom;
    private LinearLayout    pwb_ll_praise_tottom;
    private TextView        textview_praise_bottom;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_status_detail);

        footView = View.inflate(this, R.layout.footer_loading, null);
        listComments = new ArrayList<>();

        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        requestQueue = Volley.newRequestQueue(this);


        lruCache = new LruCache<>(20);
        imageCache = new ImageCache() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
            @Override
            public Bitmap getBitmap(String key) {
                return lruCache.get(key);
            }

            @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
            @Override
            public void putBitmap(String key, Bitmap value) {
                lruCache.put(key, value);
            }
        };

        imageLoader = new ImageLoader(requestQueue, imageCache);

        /** 获取intent传入的内容 */
        status = (Status) getIntent().getSerializableExtra("status");

        /** 读取评论列表 */
        loadData(1);

        /** 初始化view */
        initView();

        /** 填充数据 */
        setData();
    }

    private void initView() {
        initTitleBar();
        initDetailHead();
        initTab();
        initLstView();
        initControlBar();
    }

    /** 传入页码,加载评论 */
    private void loadData(final long page) {
        String uri = Uri.comments_show;
        uri += "?access_token=" + mAccessToken.getToken() + "&id=" + status.getId();
        uri += "&page="+page;
        Log.i("tag", uri);

        final ProgressDialog pd = ProgressDialog.show(this, "加载评论", "正在加载ing...");

        /** 发送volley的JsonObjectRequest请求 */
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, uri,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    /** 如果页码为1,则将评论listComments清空 */
                    if (page == 1) {
                        listComments.clear();
                    }

                    JSONArray jsonArray = response.getJSONArray("comments");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Comment comment = new Comment();
                        comment = comment.parseJson(jsonArray.getJSONObject(i));
                        listComments.add(comment);
                    }

                    totalNum = response.getLong("total_number");
                    curPage = page;
                    addData(listComments, totalNum);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ToastUtils.showToast(StatusDetailActivity.this, "加载评论失败", Toast.LENGTH_SHORT);
            }
        });

        pd.dismiss();

        requestQueue.add(jsonObjectRequest);
    }

    /** 对控件进行数据填充 */
    private void setData() {
        User user = status.getUser();

        /** 设置用户头像 */
        ImageListener listener;
        listener = ImageLoader.getImageListener(pwb_imageview_item_status_avatar,
                R.drawable.ic_com_sina_weibo_sdk_logo, R.drawable.pwb_avatar);
        imageLoader.get(user.getProfile_image_url(), listener);

        /** 设置发布者昵称 */
        pwb_textview_sender.setText(user.getName());

        /** 设置发布日期和来源 */
        pwb_textview_item_status_from_and_when.setText(
                new DateUtils().String2Date(status.getCreated_at())
                        + " 来自 " + Html.fromHtml(status.getSource()));

        /** 设置微博内容 */
        pwb_textview_content.setText(
                StringUtils.getWeiboContent(this, pwb_textview_content, status.getText()));

        /** 如果微博非转发且有图片 */
        if (status.getPic_ids() != null && status.getRetweeted_status() == null) {
            setImages(include_status_image, pwb_gridview_status_image,
                    pwb_imageview_status_image, status);
        }

        /** 如果微博有转发内容,显示转发的内容 */
        if (status.getRetweeted_status() != null) {
            Status retweeted_status = status.getRetweeted_status();
            include_retweeted_status.setVisibility(View.VISIBLE);

            String content = "@" + retweeted_status.getUser().getName() + ":"
                    + retweeted_status.getText();

            pwb_textview_retweeted_content.setText(StringUtils.getWeiboContent(this,
                    pwb_textview_retweeted_content, content));

            setImages(include_retweeted_status_image, pwb_gridview_retweeted_status_image,
                    pwb_imageview_retweeted_status_image, retweeted_status);
        }

        /** 设置tab的内容 */
        pwb_radiobutton_share.setText("转发 " + status.getReposts_count());
        pwb_radiobutton_comment.setText("评论 " + status.getComments_count());
        pwb_radiobutton_praise.setText("赞 " + status.getAttitudes_count());
        shadow_radiobutton_share.setText("转发 " + status.getReposts_count());
        shadow_radiobutton_comment.setText("评论 " + status.getComments_count());
        shadow_radiobutton_praise.setText("赞 " + status.getAttitudes_count());

        /** 设置底部control的内容 */
        textview_share_bottom.setText(
                (status.getReposts_count() == 0) ? "转发" : status.getReposts_count() + "");
        textview_comment_bottom.setText(
                (status.getComments_count() == 0) ? "评论" : status.getComments_count() + "");
        textview_praise_bottom.setText(
                (status.getAttitudes_count() == 0) ? "赞" : status.getAttitudes_count() + "");

        /** 设置底部control的监听器 */
        pwb_ll_comment_tottom.setOnClickListener(this);
        pwb_ll_share_tottom.setOnClickListener(this);
        pwb_ll_praise_tottom.setOnClickListener(this);

    }

    /** 加载微博的图片 */
    public void setImages(ViewGroup vgContainer, GridView gv, NetworkImageView iv, Status status) {
        ArrayList<PicUrls> list = status.getPic_ids();

        if (list != null && list.size() > 1) {
            vgContainer.setVisibility(View.VISIBLE);
            gv.setVisibility(View.VISIBLE);
            iv.setVisibility(View.GONE);

            StatusGridViewAdapter gridViewAdapter;
            gridViewAdapter = new StatusGridViewAdapter(this, list, imageLoader, vgContainer);
            gv.setAdapter(gridViewAdapter);

        } else if (list != null && list.size() == 1) {
            vgContainer.setVisibility(View.VISIBLE);
            gv.setVisibility(View.GONE);
            iv.setVisibility(View.VISIBLE);

            iv.setTag(list.get(0).getThumbnail_pic());
            iv.setImageUrl(list.get(0).getThumbnail_pic(), imageLoader);
        } else {
            vgContainer.setVisibility(View.GONE);
        }
    }

    /** 往listView中加item */
    public void addData(List<Comment> listComments, long totalNum) {
        for (Comment comment : listComments) {
            if (!listComments.contains(comment)) {
                listComments.add(comment);
            }
        }

        adapter.notifyDataSetChanged();

        if (listComments.size() < totalNum) {
            addFootView(pwb_plv_status_detail, footView);
        } else {
            removeFootView(pwb_plv_status_detail, footView);
        }
    }

    /** 初始化标题栏 */
    private void initTitleBar() {
        new TitlebarUtils(this)
                .setTitleContent("微博正文")
                .setTitlebarTvLeft("返回")
                .setTitlebarTvRight("更多")
                .setLeftOnClickListner(this);
    }

    /** 初始化微博信息 */
    private void initDetailHead() {
        status_detail_info = View.inflate(this, R.layout.item_status, null);
        status_detail_info.setBackgroundResource(R.color.white);
        status_detail_info.findViewById(R.id.include_status_control).setVisibility(View.GONE);
        pwb_imageview_item_status_avatar = (ImageView) status_detail_info.findViewById(R.id.pwb_imageview_item_status_avatar);
        pwb_textview_sender = (TextView) status_detail_info.findViewById(R.id.pwb_textview_sender);
        pwb_textview_item_status_from_and_when = (TextView) status_detail_info.findViewById(R.id.pwb_textview_item_status_from_and_when);
        pwb_textview_content = (TextView) status_detail_info.findViewById(R.id.pwb_textview_content);
        include_status_image = (FrameLayout) status_detail_info.findViewById(R.id.include_status_image);
        pwb_gridview_status_image = (WrapHeightGridView) include_status_image.findViewById(R.id.pwb_gridview_status_image);
        pwb_imageview_status_image = (NetworkImageView) include_status_image.findViewById(R.id.pwb_imageview_status_image);
        include_retweeted_status = status_detail_info.findViewById(R.id.include_retweeted_status);
        pwb_textview_retweeted_content = (TextView) status_detail_info.findViewById(R.id.pwb_textview_retweeted_content);
        include_retweeted_status_image = (FrameLayout) status_detail_info.findViewById(R.id.include_retweeted_status_image);
        pwb_gridview_retweeted_status_image = (WrapHeightGridView) include_retweeted_status_image.findViewById(R.id.pwb_gridview_status_image);
        pwb_imageview_retweeted_status_image = (NetworkImageView) include_retweeted_status_image.findViewById(R.id.pwb_imageview_status_image);
    }

    /** 初始化中部radiogroup */
    private void initTab() {
        include_status_detail_tab    = View.inflate(this,R.layout.include_status_detail_tab, null);
        pwb_radiogroup_status_detail = (RadioGroup)   include_status_detail_tab.findViewById(R.id.pwb_radiogroup_status_detail);
        pwb_radiobutton_share        = (RadioButton)  include_status_detail_tab.findViewById(R.id.pwb_radiobutton_share);
        pwb_radiobutton_comment      = (RadioButton)  include_status_detail_tab.findViewById(R.id.pwb_radiobutton_comment);
        pwb_radiobutton_praise       = (RadioButton)  include_status_detail_tab.findViewById(R.id.pwb_radiobutton_praise);
        pwb_radiogroup_status_detail.setOnClickListener(this);

        /** 悬浮的菜单栏 */
        shadow_status_detail_tab    = findViewById(R.id.include_status_detail_tab);
        shadow_radiogroup_status_detail = (RadioGroup)   shadow_status_detail_tab.findViewById(R.id.pwb_radiogroup_status_detail);
        shadow_radiobutton_share        = (RadioButton)  shadow_status_detail_tab.findViewById(R.id.pwb_radiobutton_share);
        shadow_radiobutton_comment      = (RadioButton)  shadow_status_detail_tab.findViewById(R.id.pwb_radiobutton_comment);
        shadow_radiobutton_praise       = (RadioButton)  shadow_status_detail_tab.findViewById(R.id.pwb_radiobutton_praise);
        shadow_radiogroup_status_detail.setOnClickListener(this);
    }

    /** 初始化listview */
    private void initLstView() {
        pwb_plv_status_detail = (Pull2RefreshListView) findViewById(R.id.pwb_plv_status_detail);
        adapter = new StatusCommentAdapter(this, listComments, requestQueue, imageLoader);

        pwb_plv_status_detail.setAdapter(adapter);

        final ListView lv = pwb_plv_status_detail.getRefreshableView();
        lv.addHeaderView(status_detail_info);
        lv.addHeaderView(include_status_detail_tab);

        // 下拉刷新监听
        pwb_plv_status_detail.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                loadData(1);
            }
        });

        // 滑动到最后一个item的监听
        pwb_plv_status_detail.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                loadData(curPage + 1);
            }
        });

        // 设置滚动监听器
        pwb_plv_status_detail.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // 如果滚动到tab第一个item时,则显示顶部隐藏的shadow_tab,作为悬浮菜单栏
                shadow_status_detail_tab.setVisibility(firstVisibleItem >= 2 ? View.VISIBLE : View.GONE);
            }
        });
    }

    /** 初始化底部控件 */
    private void initControlBar() {
        inlude_status_control   = (LinearLayout) findViewById(R.id.inlude_status_control);
        pwb_ll_share_tottom     = (LinearLayout) inlude_status_control.findViewById(R.id.pwb_ll_share_tottom);
        textview_share_bottom   = (TextView)     inlude_status_control.findViewById(R.id.textview_share_bottom);
        pwb_ll_comment_tottom   = (LinearLayout) inlude_status_control.findViewById(R.id.pwb_ll_comment_tottom);
        textview_comment_bottom = (TextView)     inlude_status_control.findViewById(R.id.textview_comment_bottom);
        pwb_ll_praise_tottom    = (LinearLayout) inlude_status_control.findViewById(R.id.pwb_ll_praise_tottom);
        textview_praise_bottom  = (TextView)     inlude_status_control.findViewById(R.id.textview_praise_bottom);
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

    /** 本activity的监听事件 */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titlebar_textview_left:
                StatusDetailActivity.this.finish();
                break;

            case R.id.pwb_ll_share_tottom:
                ToastUtils.showToast(this, "分享", Toast.LENGTH_SHORT);
                break;

            case R.id.pwb_ll_comment_tottom:
                ToastUtils.showToast(this, "评论", Toast.LENGTH_SHORT);
                break;

            case R.id.pwb_ll_praise_tottom:
                ToastUtils.showToast(this, "点赞", Toast.LENGTH_SHORT);
            default:
                break;
        }
    }
}
