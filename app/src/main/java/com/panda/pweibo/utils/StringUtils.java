package com.panda.pweibo.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.panda.pweibo.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 *
 * Created by Administrator on 2015/8/25:12:32.
 */
public class StringUtils {

    /** 传入微博内容,将话题和作者字体改变样式 */
    public static SpannableString getWeiboContent(final Context context, final TextView tv,
                                                  String source) {
        String regexAx    = "@[\u4e00-\u9fa5\\w]+";       // 微博作者的正则表达式 (例如:@panda潘达潘达)
        String regexTopic = "#[\u4e00-\u9fa5\\w]+#";      // 微博话题的正则表达式
        String regexEmoji = "\\[[\u4e00-\u9fa5\\w]+\\]";  // 表情的正则表达式

        String regex = "(" + regexAx + ")|(" + regexTopic
                + ")|(" + regexEmoji + ")";

        SpannableString     spannableString = new SpannableString(source);  // 微博内容String

        Pattern             pattern         = Pattern.compile(regex);
        Matcher             matcher         = pattern.matcher(spannableString);

        if (matcher.find()) {
            tv.setMovementMethod(LinkMovementMethod.getInstance());
            matcher.reset();
        }

        while (matcher.find()) {
            final String atStr      = matcher.group(1);
            final String topicStr   = matcher.group(2);
            String emojiStr         = matcher.group(3);

            if (atStr != null) {
                int start = matcher.start(1);

                PandaClickableSpan clickableSpan = new PandaClickableSpan(context) {

                    @Override
                    public void onClick(View v) {
                        ToastUtils.showToast(context, "用户:" + atStr, Toast.LENGTH_SHORT);
                    }
                };

                spannableString.setSpan(clickableSpan, start, start + atStr.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            if (topicStr != null) {
                int start = matcher.start(2);

                PandaClickableSpan clickableSpan = new PandaClickableSpan(context) {

                    @Override
                    public void onClick(View v) {
                        ToastUtils.showToast(context, "话题:" + topicStr, Toast.LENGTH_SHORT);
                    }
                };

                spannableString.setSpan(clickableSpan, start, start + topicStr.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        return spannableString;
    }

    protected static class PandaClickableSpan extends ClickableSpan {

        private Context context;

        public PandaClickableSpan (Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View widget) {
            // TODO
        }

        @Override
        public void updateDrawState(@NonNull TextPaint ds) {
            ds.setColor(context.getResources().getColor(R.color.txt_at_blue));
            ds.setUnderlineText(false);
        }
    }
}
