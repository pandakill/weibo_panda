package com.panda.pweibo.activity;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.panda.pweibo.adapter.StatusTabAdapter;
import com.panda.pweibo.constants.Code;
import com.panda.pweibo.utils.ImageFileCacheUtils;
import com.panda.pweibo.view.CommentListView;
import com.panda.pweibo.widget.Pull2RefreshListView;
import com.panda.pweibo.R;
import com.panda.pweibo.adapter.StatusCommentAdapter;
import com.panda.pweibo.adapter.StatusGridViewAdapter;
import com.panda.pweibo.constants.Uri;
import com.panda.pweibo.models.Comment;
import com.panda.pweibo.models.PicUrls;
import com.panda.pweibo.models.Status;
import com.panda.pweibo.models.User;
import com.panda.pweibo.utils.DateUtils;
import com.panda.pweibo.utils.StringUtils;
import com.panda.pweibo.utils.TitlebarUtils;
import com.panda.pweibo.utils.ToastUtils;
import com.panda.pweibo.widget.Underline;
import com.panda.pweibo.widget.WrapHeightGridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 微博详情页的activity
 *
 * Created by Administrator on 2015/8/26:9:52.
 */
@SuppressWarnings("ResourceType")
public class StatusDetailActivity extends BaseActivity implements OnClickListener {

    private Status              mStatus;
    private StatusTabAdapter    mAdapter;
    private List<View>          mViewList;      // 存放listveiw的对象

    private CommentListView     mCommentView;

    /** 微博详情页 */
    private RelativeLayout      pwb_rl_body;


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

    /** 评论、转发、点赞内容的viewpager */
    private ViewPager pwb_vp_status_detail;

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

        /** 获取intent传入的内容 */
        mStatus = (Status) getIntent().getSerializableExtra("status");

        /** 初始化view */
        initView();

        /** 填充数据 */
        setData();
    }

    private void initView() {
        initTitleBar();
        initDetailHead();
        initViewPager();
        initControlBar();
    }


    /** 对控件进行数据填充 */
    private void setData() {
        User user = mStatus.getUser();

        /** 设置用户头像 */
        ImageListener listener;
        listener = ImageLoader.getImageListener(pwb_imageview_item_status_avatar,
                R.drawable.ic_com_sina_weibo_sdk_logo, R.drawable.pwb_avatar);
        mImageLoader.get(user.getProfile_image_url(), listener);

        /** 设置发布者昵称 */
        pwb_textview_sender.setText(user.getName());

        /** 设置发布日期和来源 */
        pwb_textview_item_status_from_and_when.setText(
                new DateUtils().String2Date(mStatus.getCreated_at())
                        + " 来自 " + Html.fromHtml(mStatus.getSource()));

        /** 设置微博内容 */
        pwb_textview_content.setText(
                StringUtils.getWeiboContent(this, pwb_textview_content, mStatus.getText()));

        /** 如果微博非转发且有图片 */
        if (mStatus.getPic_ids() != null && mStatus.getRetweeted_status() == null) {
            setImages(include_status_image, pwb_gridview_status_image,
                    pwb_imageview_status_image, mStatus);
        }

        /** 如果微博有转发内容,显示转发的内容 */
        if (mStatus.getRetweeted_status() != null) {
            Status retweeted_status = mStatus.getRetweeted_status();
            include_retweeted_status.setVisibility(View.VISIBLE);

            String content = "@" + retweeted_status.getUser().getName() + ":"
                    + retweeted_status.getText();

            pwb_textview_retweeted_content.setText(StringUtils.getWeiboContent(this,
                    pwb_textview_retweeted_content, content));

            setImages(include_retweeted_status_image, pwb_gridview_retweeted_status_image,
                    pwb_imageview_retweeted_status_image, retweeted_status);
        }

        /** 设置底部control的内容 */
        textview_share_bottom.setText(
                (mStatus.getReposts_count() == 0) ? "转发" : mStatus.getReposts_count() + "");
        textview_comment_bottom.setText(
                (mStatus.getComments_count() == 0) ? "评论" : mStatus.getComments_count() + "");
        textview_praise_bottom.setText(
                (mStatus.getAttitudes_count() == 0) ? "赞" : mStatus.getAttitudes_count() + "");

        /** 设置底部control的监听器 */
        pwb_ll_comment_tottom.setOnClickListener(this);
        pwb_ll_share_tottom.setOnClickListener(this);
        pwb_ll_praise_tottom.setOnClickListener(this);

    }

    /** 加载微博的图片 */
    public void setImages(ViewGroup vgContainer, GridView gv, NetworkImageView iv, final Status status) {
        ArrayList<PicUrls> list = status.getPic_ids();

        if (list != null && list.size() > 1) {
            vgContainer.setVisibility(View.VISIBLE);
            gv.setVisibility(View.VISIBLE);
            iv.setVisibility(View.GONE);

            StatusGridViewAdapter gridViewAdapter;
            gridViewAdapter = new StatusGridViewAdapter(this, list, mImageLoader);
            gv.setAdapter(gridViewAdapter);

            /** 增加图片点击的监听器 */
            gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(StatusDetailActivity.this, ImageBrowserActivity.class);
                    intent.putExtra("status", status);
                    intent.putExtra("position", position);
                    startActivity(intent);
                }
            });

        } else if (list != null && list.size() == 1) {
            vgContainer.setVisibility(View.VISIBLE);
            gv.setVisibility(View.GONE);
            iv.setVisibility(View.VISIBLE);

            iv.setTag(list.get(0).getThumbnail_pic());
            // 判断缓存中是否存在图片高清图片,有则加载高清图片,否则请求缩略图
            Bitmap origBitmap = ImageFileCacheUtils.getInstance().getImage(list.get(0).getOriginal_pic());
            Bitmap bmidBitmap = ImageFileCacheUtils.getInstance().getImage(list.get(0).getBmiddle_pic());
            if (origBitmap != null) {
                iv.setImageUrl(list.get(0).getOriginal_pic(), mImageLoader);
            } else if (bmidBitmap != null) {
                iv.setImageUrl(list.get(0).getBmiddle_pic(), mImageLoader);
            } else {
                iv.setImageUrl(list.get(0).getThumbnail_pic(), mImageLoader);
            }

            /** 增加图片点击的监听器 */
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(StatusDetailActivity.this, ImageBrowserActivity.class);
                    intent.putExtra("status", status);
                    startActivity(intent);
                }
            });
        } else {
            vgContainer.setVisibility(View.GONE);
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
        status_detail_info = findViewById(R.id.pwb_status_card);
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

        // 设置转发微博部分的监听器
        include_retweeted_status.setOnClickListener(this);
        pwb_textview_retweeted_content.setOnClickListener(this);
    }
    /**
     * 初始化viewpager,设置为三个listview
     */
    private void initViewPager() {

        mViewList = new ArrayList<>();
        mCommentView = new CommentListView(StatusDetailActivity.this, mImageLoader, mStatus, status_detail_info);
        // 设置viewpager中第一个为转发列表、第二个为评论列表、第三个为赞列表
        mViewList.add(mCommentView.listView);
        mViewList.add(new CommentListView(StatusDetailActivity.this, mImageLoader, mStatus, status_detail_info).listView);
        mViewList.add(new CommentListView(StatusDetailActivity.this, mImageLoader, mStatus, status_detail_info).listView);

        pwb_vp_status_detail = (ViewPager) findViewById(R.id.pwb_vp_status_detail);
        mAdapter = new StatusTabAdapter(StatusDetailActivity.this, mViewList);
        pwb_vp_status_detail.setAdapter(mAdapter);
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

    /** 本activity的监听事件 */
    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()) {

            case R.id.titlebar_textview_left:
                StatusDetailActivity.this.finish();
                break;

            case R.id.pwb_ll_share_tottom:
                ToastUtils.showToast(this, "分享", Toast.LENGTH_SHORT);
                break;

            /** 跳转至评论页面 */
            case R.id.pwb_ll_comment_tottom:
                intent = new Intent(this, WriteCommentActivity.class);
                intent.putExtra("status", mStatus);
                intent.putExtra("type", Code.CREATE_COMMENT);
                startActivityForResult(intent, Code.REQUEST_CODE_WRITE_COMMENT_BACK_TO_DETAIL);
                break;

            case R.id.pwb_ll_praise_tottom:
                ToastUtils.showToast(this, "点赞", Toast.LENGTH_SHORT);
                break;

            case R.id.pwb_textview_retweeted_content:
                intent = new Intent(this, StatusDetailActivity.class);
                intent.putExtra("status", mStatus.getRetweeted_status());
                startActivity(intent);
                break;

            case R.id.include_retweeted_status:
                intent = new Intent(this, StatusDetailActivity.class);
                intent.putExtra("status", mStatus.getRetweeted_status());
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    /** 评论页面跳转回来的数据处理 */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 如果点击返回或者取消发评论等情况,则直接return,不做后续处理
        if (resultCode == RESULT_CANCELED) {
            return;
        }

        // 如果有发生请求动作,则对返回码进行判断处理
        switch (requestCode) {
            case Code.REQUEST_CODE_WRITE_COMMENT_BACK_TO_DETAIL:
                boolean sendCommentSuccess = data.getBooleanExtra("sendCommentSuccess", false);
                if (sendCommentSuccess) {
                    ToastUtils.showToast(StatusDetailActivity.this, "发送成功", Toast.LENGTH_SHORT);
                    mCommentView.mScroll2Comment = true;
                    mCommentView.loadData(1);
                }
                break;

            default:
                break;
        }
    }

}
