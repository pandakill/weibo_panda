package com.panda.pweibo.listener;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.panda.pweibo.models.Status;
import com.panda.pweibo.utils.ToastUtils;

/**
 * Created by Administrator on 2015/8/25:10:35.
 */
public class ControlbarClickListener implements OnClickListener {

    private Context         context;
    private LinearLayout    barContainer;
    private Status          status;

    public ControlbarClickListener(Context context, LinearLayout barContainer, Status status) {
        this.context        = context;
        this.barContainer   = barContainer;
        this.status         = status;
    }

    @Override
    public void onClick(View v) {
        ToastUtils.showToast(context, "被点击啦~", Toast.LENGTH_LONG);
    }
}
