package com.panda.pweibo.constants;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;

/**
 * URI地址
 *
 * Created by Administrator on 2015/8/22:14:01.
 */
public interface Uri {

    /** uri服务器 */
    String baseUri = "https://api.weibo.com/2/";

    /** 授权回收接口，帮助开发者主动取消用户的授权 */
    String revokeoauth2 = "https://api.weibo.com/oauth2/revokeoauth2";

    /** 获取当前登录用户及其所关注用户的最新微博  */
    String home_timeline = baseUri + "statuses/home_timeline";
}
