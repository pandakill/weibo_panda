package com.panda.pweibo.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.panda.pweibo.R;
import com.panda.pweibo.fragment.FragmentController;
import com.panda.pweibo.utils.ToastUtils;


public class MainActivity extends FragmentActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    private     RadioGroup          radioGroup;
    private     ImageButton         imageButton;
    private     FragmentController  controller;

    @Override
    protected void onCreate(Bundle saveInstanceState) {

        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        controller = FragmentController.getInstance(MainActivity.this, R.id.fl_content);
        //默认展示第一个
        controller.showFragment(0);

        initView();
    }

    private void initView() {

        radioGroup = (RadioGroup) findViewById(R.id.pwb_radiogroup_fragment_tab);
        imageButton = (ImageButton) findViewById(R.id.pwb_imagebutton_add);

        radioGroup.setOnCheckedChangeListener(MainActivity.this);
        imageButton.setOnClickListener(MainActivity.this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case  R.id.pwb_radiobutton_home:
                controller.showFragment(0);
                break;

            case R.id.pwb_radiobutton_message:
                controller.showFragment(1);
                break;

            case R.id.pwb_radiobutton_search:
                controller.showFragment(2);
                break;

            case R.id.pwb_radiobutton_pesonal:
                controller.showFragment(3);
                break;

            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pwb_imagebutton_add:
                ToastUtils.showToast(MainActivity.this,"添加按钮",Toast.LENGTH_LONG);
                break;

            default:
                break;
        }
    }
}
