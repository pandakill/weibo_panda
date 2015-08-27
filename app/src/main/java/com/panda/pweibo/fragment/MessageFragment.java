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
import com.panda.pweibo.models.Message;
import com.panda.pweibo.utils.TitlebarUtils;
import com.panda.pweibo.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class MessageFragment extends Fragment {

    private     View                    mView;
    private     MainActivity            mActivity;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

        mActivity        = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initView();
        return mView;
    }

    /** 初始化view */
    public void initView(){

        mView = View.inflate(mActivity, R.layout.fragment_message, null);

        // 视图控件
        PullToRefreshListView pwb_plv_message_cneter;
        pwb_plv_message_cneter = (PullToRefreshListView) mView.findViewById(R.id.pwb_plv_message_cneter);

        new TitlebarUtils(mView)
                .setTitleContent("消息")
                .setTitlebarTvRight("发起聊天")
                .setRightOnClickListner(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastUtils.showToast(mActivity, "发起聊天", Toast.LENGTH_LONG);
                    }
                });

        // 设置适配器
        List<Message> messageList = messageData();
        MessageCenterAdapter adapter = new MessageCenterAdapter(mActivity, messageList);
        pwb_plv_message_cneter.setAdapter(adapter);
    }

    /** 初始化消息盒子 */
    protected List<Message> messageData() {
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
