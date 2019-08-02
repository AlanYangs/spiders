package com.alany.spider.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yangangui on 2018/11/21.
 */
public class StringUtils {

    public static String subNumber(String str){
        Pattern p = Pattern.compile("[^0-9]");
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    public static boolean isEmpty(String str) {
        return str == null || "".equals(str);
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static boolean isBlank(String str) {
        if (isNotEmpty(str)) {
            return str.trim().length() == 0;
        }
        return true;
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }
}
