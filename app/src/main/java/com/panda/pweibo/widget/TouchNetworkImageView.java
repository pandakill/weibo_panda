package com.panda.pweibo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.android.volley.toolbox.NetworkImageView;

/**
 * 多点触控实现图片的缩放和拖动
 *
 * Created by Administrator on 2015/9/9:16:31.
 */
public class TouchNetworkImageView extends NetworkImageView {

    /**
     * 触摸引发的动作状态类型
     */
    final static int NONE = 0;      // 无动作
    final static int DRAG = 1;      // 拖动
    final static int ZOOM = 2;      // 缩放
    final static int BIGGER = 3;    // 放大
    final static int SMALLER = 4;   // 缩小

    private int mMode = NONE;        // 当前的事件状态

    private float mBeforeLenght;     // 两触点的距离
    private float mAfterLenght;      // 两触点的距离
    private float mScale = 0.04f;    //缩放的比例,xy方向都是这个值,越大缩放的速度越快

    /**
     * 图片的高宽
     */
    private int mScreenW;
    private int mScreenH;

    /**
     * 处理拖动的变量
     */
    private int mFromX;
    private int mFromY;
    private int mToX;
    private int mToY;

    /**
     * 处理超出边界的动画
     */
    private TranslateAnimation translateAnimation;

    /**
     * 默认的构造函数
     */
    public TouchNetworkImageView(Context context) {
        super(context);
    }

    public TouchNetworkImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 在动态创建时，制定图片的初始高宽
     * @param context
     * @param w
     * @param h
     */
    public TouchNetworkImageView(Context context, int w, int h) {
        super(context);
        this.setPadding(0, 0, 0, 0);
        mScreenW = w;
        mScreenH = h;
    }

    /**
     * 利用勾股定理，计算两点间的距离
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);

        return FloatMath.sqrt(x * x + y * y);
    }

    /**
     * 处理触碰事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                // 设置当前为拖动模式
                mMode = DRAG;

                // 获取开始的坐标和结束的坐标
                mToX = (int) event.getRawX();
                mToY = (int) event.getRawY();
                mFromX = (int) event.getX();
                mFromY = mToY - this.getTop();
                // 如果有两个点触碰,则计算触摸点之间的距离
                if (event.getPointerCount() == 2) {
                    mBeforeLenght = spacing(event);
                }
                break;

            // 如果有两个点触碰,则为缩放模式
            case MotionEvent.ACTION_POINTER_DOWN:
                if (spacing(event) > 10f) {
                    // 设置当前为缩放模式
                    mMode = ZOOM;
                    mBeforeLenght = spacing(event);
                }
                break;

            case MotionEvent.ACTION_UP:
                int disX = 0;
                int disY = 0;
                if (getHeight() <= mScreenH || this.getTop() < 0) {
                    if (this.getTop() < 0) {
                        int dis = getTop();
                        this.layout(this.getLeft(), 0, this.getRight(), 0 + this.getHeight());
                        disY = dis - getTop();
                    } else if(this.getBottom() > mScreenH) {
                        disY = getHeight()- mScreenH + getTop();
                        this.layout(this.getLeft(), mScreenH - getHeight(), this.getRight(), mScreenH);
                    }
                }
                if(getWidth() <= mScreenW)
                {
                    if(this.getLeft()<0)
                    {
                        disX = getLeft();
                        this.layout(0, this.getTop(), 0 + getWidth(), this.getBottom());
                    }
                    else if(this.getRight()>mScreenW)
                    {
                        disX = getWidth() - mScreenW + getLeft();
                        this.layout(mScreenW - getWidth(), this.getTop(), mScreenW, this.getBottom());
                    }
                }
                if(disX!=0 || disY!=0)
                {
                    translateAnimation = new TranslateAnimation(disX, 0, disY, 0);
                    translateAnimation.setDuration(500);
                    this.startAnimation(translateAnimation);
                }
                mMode = NONE;
                break;

            case MotionEvent.ACTION_POINTER_UP:
                mMode = NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                /*处理拖动*/
                if (mMode == DRAG) {
                    if(Math.abs(mToX - mFromX - getLeft())<88 && Math.abs(mToY - mFromY - getTop())<85)
                    {
                        this.setPosition(mToX - mFromX, mToY - mFromY, mToX + this.getWidth() - mFromX, mToY - mFromY + this.getHeight());
                        mToX = (int) event.getRawX();
                        mToY = (int) event.getRawY();
                    }
                }
                /*处理缩放*/
                else if (mMode == ZOOM) {
                    if(spacing(event)>10f)
                    {
                        mAfterLenght = spacing(event);
                        float gapLenght = mAfterLenght - mBeforeLenght;
                        if(gapLenght == 0) {
                            break;
                        }
                        else if(Math.abs(gapLenght)>5f)
                        {
                            if(gapLenght>0) {

                                this.setScale(mScale, BIGGER);
                            }else {
                                this.setScale(mScale, SMALLER);
                            }
                            mBeforeLenght = mAfterLenght;
                        }
                    }
                }
                break;
        }
        return true;
    }

    /**
     * 实现图片的缩放
     * @param temp  缩放倍数
     * @param flag  缩放标记
     */
    private void setScale(float temp, int flag) {
        // 如果是放大,则将图片整体往外偏移
        if (flag == BIGGER) {
            this.setFrame(this.getLeft()-(int)(temp*this.getWidth()),
                    this.getTop()-(int)(temp*this.getHeight()),
                    this.getRight()+(int)(temp*this.getWidth()),
                    this.getBottom()+(int)(temp*this.getHeight()));
        } else { // 如果是缩小,则图片整体往中间偏移
            this.setFrame(this.getLeft()+(int)(temp*this.getWidth()),
                    this.getTop()+(int)(temp*this.getHeight()),
                    this.getRight()-(int)(temp*this.getWidth()),
                    this.getBottom()+(int)(temp*this.getHeight()));
        }
    }

    /**
     * 实现图片的拖动
     * @param left      左边距
     * @param top       上边距
     * @param right     右边距
     * @param bottom    下边距
     */
    private void setPosition(int left, int top, int right, int bottom) {
        this.layout(left, top, right, bottom);
    }
}
