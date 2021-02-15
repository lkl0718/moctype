package com.md.moktype.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Formatter {

    public static final String NOW = "yy/MM/dd hh:mm:ss";
    public static final String NOW_24 = "yy/MM/dd kk:mm:ss";
    public static final String DAY_WITH_DOT = "yyyy.MM.dd";
    public static final String DAY_WITH_HYPHEN = "yyyy-MM-dd";
    public static final String DAY_WITH_KR = "yyyy년 MM월 dd일";

    /**
     * 이메일 포맷 검증
     * @param email
     * @return
     */
    public static boolean isValidEmail(String email) {
        String regex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);

        if(m.matches()) return true;
        else            return false;
    }

    /**
     * 현재시간
     * @return yyyy.MM.dd hh:mm
     */
    public static String getNow() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd kk:mm", Locale.KOREA);
        Date currentTime = new Date();
        return dateFormat.format(currentTime);
    }

    /**
     * 날짜, 시간 포맷변경
     */
    public static String getDate(Date data, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.KOREA);
        return dateFormat.format(data);
    }

    /**
     * 오늘 날짜
     */
    public static String getToday() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
        Date currentTime = new Date();
        return dateFormat.format(currentTime);
    }

}
