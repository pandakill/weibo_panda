package com.panda.pweibo.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 2015/8/24:11:37.
 */
public class DateUtils {
    private String dateStr;

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public String getDateStr() {
        return dateStr;
    }

    public String String2Date(String dateStr) {
        if (dateStr != null) {
            SimpleDateFormat s = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
            try {
                Date date = s.parse(dateStr);
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
                String result = sdf.format(date);

                return result;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
