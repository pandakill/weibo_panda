package com.panda.pweibo.utils;

import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 日期工具类
 *
 * Created by Administrator on 2015/8/24:11:37.
 */
public class DateUtils {

    public static final long ONE_MINUTE_MILLIONS = 60 * 1000;
    public static final long ONE_HOUR_MILLIONS = 60 * ONE_MINUTE_MILLIONS;
    public static final long ONE_DAY_MILLIONS = 24 * ONE_HOUR_MILLIONS;

    /**
     * 将string的日期转换为一定格式的日期
     * 如果传入的时间在当前时间十分钟以内,则返回“刚刚”
     * 如果传入的时间在当前时间一小时以内,返回“几分钟前”
     * 如果传入的时间在当前时间一天以内,返回“几个小时前”
     * 如果传入的时间在当前时间同一年,返回“MM-dd”格式
     * 如果传入的时间在当前时间的不同年份,返回“yyyy-MM”格式
     */
    public String String2Date(String dateStr) {
        String str = "";

        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.US);
        try {
            Date date = sdf.parse(dateStr);
            Date curDate = new Date();

            long durTime = curDate.getTime() - date.getTime();
            int dayStatus = calculateDayStatus(date, curDate);

            if(durTime <= 10 * ONE_MINUTE_MILLIONS) {
                str = "刚刚";
            } else if(durTime < ONE_HOUR_MILLIONS) {
                str = durTime / ONE_MINUTE_MILLIONS + "分钟前";
            } else if(dayStatus == 0) {
                str = durTime / ONE_HOUR_MILLIONS + "小时前";
            } else if(dayStatus == -1) {
                str = "昨天" + DateFormat.format("HH:mm", date);
            } else if(isSameYear(date, curDate) && dayStatus < -1) {
                str = DateFormat.format("MM-dd", date).toString();
            } else {
                str = DateFormat.format("yyyy-MM", date).toString();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return str;
    }

    /**
     * 判断targetTime是否与compareTime在同一年
     *
     * @param targetTime
     *          要比较的时间
     * @param compareTime
     *          被比较的时间
     * @return true
     *           在同一年
     */
    public static boolean isSameYear(Date targetTime, Date compareTime) {
        Calendar tarCalendar = Calendar.getInstance();
        tarCalendar.setTime(targetTime);
        int tarYear = tarCalendar.get(Calendar.YEAR);

        Calendar compareCalendar = Calendar.getInstance();
        compareCalendar.setTime(compareTime);
        int comYear = compareCalendar.get(Calendar.YEAR);

        return tarYear == comYear;
    }

    /**
     * 判断targetTime在compareTime的哪一天
     * @param targetTime
     *          要比较的日期
     * @param compareTime
     *          被比较的日期
     * @return
     *          >0  在compareTime的未来
     *          <0  在compareTime的之前
     *          ==0 和compareTime同一天
     */
    public static int calculateDayStatus(Date targetTime, Date compareTime) {
        Calendar tarCalendar = Calendar.getInstance();
        tarCalendar.setTime(targetTime);
        int tarDayOfYear = tarCalendar.get(Calendar.DAY_OF_YEAR);

        Calendar compareCalendar = Calendar.getInstance();
        compareCalendar.setTime(compareTime);
        int comDayOfYear = compareCalendar.get(Calendar.DAY_OF_YEAR);

        return tarDayOfYear - comDayOfYear;
    }
}
