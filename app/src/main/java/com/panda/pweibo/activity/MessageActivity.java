package com.panda.pweibo.activity;

import android.os.Bundle;

import com.panda.pweibo.R;
import com.panda.pweibo.adapter.MessageAdapter;

/**
 * 打开消息列表的activity
 *
 * Created by Administrator on 2015/8/27:14:23.
 */
public class MessageActivity extends BaseActivity {

    private MessageAdapter mAdapter;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_message);

    }
}
