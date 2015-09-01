package com.panda.pweibo.constants;

/**
 * URI地址
 *
 * Created by Administrator on 2015/8/22:14:01.
 */
public interface Uri {

    /** uri服务器 */
    String BASE_URI = "https://api.weibo.com/2/";

    /** 中等质量图片的url前缀 */
    String BMIDDLE_URL = "http://ww3.sinaimg.cn/bmiddle/";

    /** 原质量图片url的前缀 */
    String ORIGINAL_URL = "http://ww3.sinaimg.cn/large/";

    /** 授权回收接口，帮助开发者主动取消用户的授权 */
    String OAUTH2_REVOKE_OAUTH2 = "https://api.weibo.com/oauth2/revokeoauth2";

    /** 退出登录 */
    String ACCOUNT_END_SESSION = BASE_URI + "account/end_session.json";

    /** 获取当前登录用户及其所关注用户的最新微博  */
    String STATUS_HOME_TIMELINE = BASE_URI + "statuses/home_timeline.json";

    /** 上传图片并发布一条新微博  */
    String STATUS_UPLOAD = BASE_URI + "statuses/upload.json";

    /** 发布一条纯文字新微博  */
    String STATUS_UPDATE = BASE_URI + "statuses/update.json";

    /** 根据微博ID获取单条微博内容   */
    String STATUS_SHOW = BASE_URI + "statuses/show.json";

    /** 获取某个用户最新发表的微博列表  */
    String STATUS_USER_TIMELINE = BASE_URI + "statuses/user_timeline.json";

    /** 根据微博ID返回某条微博的评论列表  */
    String COMMENTS_SHOW = BASE_URI + "comments/show.json";

    /** 对一条微博进行评论  */
    String COMMENTS_CREATE = BASE_URI + "comments/create.json";

    /** 获取当前登录用户的最新评论包括接收到的与发出的 */
    String COMMENTS_TO_ME = BASE_URI + "comments/to_me.json";

    /** 获取最新的提到当前登录用户的评论，即@我的评论  */
    String COMMENTS_MESSION = BASE_URI + "comments/mentions.json";

    /** 根据用户ID获取用户信息  */
    String USER_SHOW = BASE_URI + "users/show.json";
}
