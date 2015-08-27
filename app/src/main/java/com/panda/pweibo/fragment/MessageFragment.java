package com.panda.pweibo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.panda.pweibo.R;
import com.panda.pweibo.activity.MainActivity;
import com.panda.pweibo.adapter.MessageCenterAdapter;
import com.panda.pweibo.constants.AccessTokenKeeper;
import com.panda.pweibo.models.Message;
import com.panda.pweibo.utils.TitlebarUtils;
import com.panda.pweibo.utils.ToastUtils;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;

import java.util.ArrayList;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class MessageFragment extends Fragment {

    private     View                    view;
    private     MainActivity            activity;
    private     Oauth2AccessToken       mAccessToken;
    private     MessageCenterAdapter    adapter;
    private     List<Message>           listMessage;
    private     PullToRefreshListView   pwb_plv_message_cneter;

    public MessageFragment() {
    }

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

        activity        = (MainActivity) getActivity();
        mAccessToken    = AccessTokenKeeper.readAccessToken(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initView();
        return view;
    }

    /** 初始化view */
    public void initView(){

        view = View.inflate(activity, R.layout.fragment_message, null);

        pwb_plv_message_cneter =
                (PullToRefreshListView) view.findViewById(R.id.pwb_plv_message_cneter);

        new TitlebarUtils(view)
                .setTitleContent("消息")
                .setTitlebarTvRight("发起聊天")
                .setRightOnClickListner(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastUtils.showToast(activity, "发起聊天", Toast.LENGTH_LONG);
                    }
                });

        listMessage = messageData();
        adapter = new MessageCenterAdapter(activity, listMessage);
        pwb_plv_message_cneter.setAdapter(adapter);
    }

    /** 初始化消息盒子 */
    private List<Message> messageData() {
        List<Message> listMessage = new ArrayList<>();

        Message at = new Message();
        at.setImage(R.drawable.pwb_messagescenter_at);
        at.setMessage("@我的");
        listMessage.add(at);

        Message comment = new Message();
        comment.setImage(R.drawable.pwb_messagescenter_comments);
        comment.setMessage("评论");
        listMessage.add(comment);

        Message praise = new Message();
        praise.setImage(R.drawable.pwb_messagescenter_good);
        praise.setMessage("赞");
        listMessage.add(praise);

        Message messageBox = new Message();
        messageBox.setImage(R.drawable.pwb_messagescenter_messagebox);
        messageBox.setMessage("订阅消息");
        listMessage.add(messageBox);

        return listMessage;
    }
}
