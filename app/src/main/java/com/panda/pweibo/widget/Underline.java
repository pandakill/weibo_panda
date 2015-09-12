package com.panda.pweibo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import com.panda.pweibo.R;

/**
 * radioGroup底部的橙色导航栏
 *
 * Created by Administrator on 2015/9/8:15:29.
 */
public class Underline extends LinearLayout {

    private int mCurrentItem;

    public Underline(Context context) {
        super(context);
    }

    public Underline(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 设置控件布局为横向
        setOrientation(HORIZONTAL);

        // 初始化底部线性橙色条,等分成三份
        int count = 3;
        for (int i = 0; i < count; i++) {
            View view = new View(context);
            LayoutParams params = new LayoutParams(0, LayoutParams.MATCH_PARENT);
            params.weight = 1;
            view.setLayoutParams(params);
            view.setBackgroundResource(R.color.transparent);
            addView(view);
        }
    }

    /**
     * 无动画效果的切换底部橙色导航栏
     *
     * @param item 需要显示橙色的下标
     */
    public void setCurrentItemWithoutAni(int item) {
        final View oldView = getChildAt(mCurrentItem);
        final View newView = getChildAt(item);

        oldView.setBackgroundResource(R.color.transparent);
        newView.setBackgroundResource(R.color.orange);

        mCurrentItem = item;

        invalidate();
    }

    /**
     * 有动画的切换底部橙色导航栏
     *
     * @param item 需要显示橙色的下标
     */
    public void setCurrentItem(int item) {

        // 选中之前的橙色下标位置
        final View oldView = getChildAt(mCurrentItem);

        // 新选中的橙色下标位置
        final View newView = getChildAt(item);

        // 设置橙色下标动画
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, item - mCurrentItem,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0);

        translateAnimation.setDuration(200);
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                oldView.setBackgroundResource(R.color.transparent);
                newView.setBackgroundResource(R.color.orange);
            }
        });

        oldView.setAnimation(translateAnimation);
        mCurrentItem = item;

        invalidate();
    }
}
