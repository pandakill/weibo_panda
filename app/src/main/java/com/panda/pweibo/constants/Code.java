package com.panda.pweibo.constants;

/**
 * 存放一些静态常量码
 *
 * Created by Administrator on 2015/8/28:11:58.
 */
public interface Code {

    /** 消息的类型 */
    int MESSAGE_AT        = 1;      /* @我的评论 */
    int MESSAGE_COMMENT   = 2;      /* 我的所有评论 */
    int MESSAGE_GOOD      = 3;      /* 赞 */
    int MESSAGE_BOX       = 4;      /* 消息盒子 */

    /** 打开写评论activity时,写评论的类型 */
    int REPLY_COMMENT     = 1;     /* 类型为回复评论 */
    int CREATE_COMMENT    = 2;     /* 创建评论 */

    /** 写评论activity的返回码 */
    int REQUEST_CODE_WRITE_COMMENT_BACK_TO_COMMENT = 22;  /* 跳转至所有评论的activity */

    /** 跳转至写评论页面code */
    int REQUEST_CODE_WRITE_COMMENT_BACK_TO_DETAIL = 2333;
}
