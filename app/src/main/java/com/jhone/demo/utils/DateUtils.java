package com.jhone.demo.utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by jhone on 2016/3/14.
 */
public class DateUtils {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat();

    /***
     * 得到yyyy-MM的字符串
     *
     * @param date
     * @return
     */
    public static String getFormatDate_yyyy_MM(Date date) {
        dateFormat.applyPattern("yyyy-MM");
        return dateFormat.format(date);
    }

    /***
     * 得到yyyy-MM-dd的字符串
     *
     * @param date
     * @return
     */
    public static String getFormatDate_yyyy_MM_dd(Date date) {
        dateFormat.applyPattern("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    /***
     * 得到yyyy年MM月dd的字符串
     *
     * @param date
     * @return
     */
    public static String getFormatDate_yyyyMMdd(Date date) {
        dateFormat.applyPattern("yyyy年MM月dd日");
        return dateFormat.format(date);
    }

    /***
     * 得到yyyy-MM-dd HH:mm:ss的字符串
     *
     * @param date
     * @return
     */
    public static String getFormatDate_yyyy_MM_dd_HH_mm_ss(Date date) {
        dateFormat.applyPattern("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }

    /***
     * 得到yyyy-MM-dd HH:mm的字符串
     *
     * @param date
     * @return
     */
    public static String getFormatDate_yyyy_MM_dd_HH_mm(Date date) {
        if (date == null) {
            return "";
        }
        dateFormat.applyPattern("yyyy-MM-dd HH:mm");
        return dateFormat.format(date);
    }

    /***
     * 得到MM-dd HH:mm的字符串
     *
     * @param date
     * @return
     */
    public static String getFormatDate_MM_dd_HH_mm(Date date) {
        dateFormat.applyPattern("MM-dd HH:mm");
        return dateFormat.format(date);
    }

    /***
     * 得到HH:mm的字符串
     *
     * @param date
     * @return
     */
    public static String getFormatDate_HH_mm(Date date) {
        dateFormat.applyPattern("HH:mm");
        return dateFormat.format(date);
    }

    /**
     * 根据字串获得Date对象
     *
     * @param timeStr yyyy-MM-dd HH:mm:ss或者yyyy-MM-dd类型字串或者yyyy-MM-dd HH:mm
     * @return
     */
    public static Date getDateByString(String timeStr) {
        try {
            if (timeStr.length() == "yyyy-MM-dd HH:mm:ss".length()) {
                dateFormat.applyPattern("yyyy-MM-dd HH:mm:ss");
            } else if (timeStr.length() == "yyyy-MM-dd HH:mm".length()) {
                dateFormat.applyPattern("yyyy-MM-dd HH:mm");
            } else if (timeStr.length() == "yyyy-MM-dd".length()) {
                dateFormat.applyPattern("yyyy-MM-dd");
            }
            return dateFormat.parse(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将星期的数字标识转换为星期类型
     *
     * @param numberStr 数字表示：例如1，2，3，4，5
     * @return 星期一，星期二，星期三，星期四，星期五
     */
    public static String number2Week(String numberStr) {
        StringBuilder builder = new StringBuilder();
        if (numberStr == null || numberStr.length() == 0) {
            return null;
        }
        String[] nums = null;
        if (numberStr.contains(",")) {
            nums = numberStr.split(",");
        } else {
            nums = new String[]{numberStr};
        }

        for (int i = 0; i < nums.length; i++) {
            switch (Integer.valueOf(nums[i])) {
                case 1:
                    builder.append("星期一");
                    break;
                case 2:
                    builder.append("星期二");
                    break;
                case 3:
                    builder.append("星期三");
                    break;
                case 4:
                    builder.append("星期四");
                    break;
                case 5:
                    builder.append("星期五");
                    break;
                case 6:
                    builder.append("星期六");
                    break;
                case 7:
                    builder.append("星期日");
                    break;
                default:
                    break;
            }
            if (i != nums.length - 1) {
                builder.append(",");
            }
        }

        return builder.toString();
    }

    /**
     * 将星期的数字标识转换为星期类型
     *
     * @param number 数字表示：例如1，2，3，4，5
     * @return 星期一，星期二，星期三，星期四，星期五
     */
    public static String getWeek(int number) {
        String week = null;
        switch (number) {
            case 1:
                week = "星期日";
                break;
            case 2:
                week = "星期一";
                break;
            case 3:
                week = "星期二";
                break;
            case 4:
                week = "星期三";
                break;
            case 5:
                week = "星期四";
                break;
            case 6:
                week = "星期五";
                break;
            case 7:
                week = "星期六";
                break;
            default:
                break;

        }

        return week;
    }


    /**
     * unix时间戳转换为dateFormat
     *
     * @return
     */
    @SuppressWarnings("deprecation")
    @SuppressLint("SimpleDateFormat")
    public static String getDate(String unixDate) {
        SimpleDateFormat fm1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        SimpleDateFormat fm2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        long unixLong = 0;
        String date = "";
        try {
            unixLong = Long.parseLong(unixDate) * 1000;
        } catch (Exception ex) {
            new RuntimeException("String转换Date错误，请确认数据可以转换！");
        }

        try {
            date = fm1.format(unixLong);
            date = fm2.format(new Date(date));
        } catch (Exception ex) {
            new RuntimeException("String转换Date错误，请确认数据可以转换！");
        }
        return date;
    }

    /**
     * 自定义格式时间戳转换
     *
     * @return
     */
    public static String timestampToDate(String unixDate, String format) {
        SimpleDateFormat fm1 = new SimpleDateFormat(format);
        SimpleDateFormat fm2 = new SimpleDateFormat(format);
        long unixLong = 0;
        String date = "";
        try {
            unixLong = Long.parseLong(unixDate) * 1000;
        } catch (Exception ex) {
            new RuntimeException("String转换Date错误，请确认数据可以转换！");
        }

        try {
            date = fm1.format(unixLong);
            date = fm2.format(new Date(date));
        } catch (Exception ex) {
            new RuntimeException("String转换Date错误，请确认数据可以转换！");
        }
        return date;
    }

    /**
     * 获取时间差
     * @param startTime
     * @param endTime
     * @return
     */
    public static String getTimePoor(String startTime, String endTime) {
        SimpleDateFormat dfs = new SimpleDateFormat("MM-dd HH:mm");
        long between = 0;
        try {
            Date begin = dfs.parse(startTime);
            Date end = dfs.parse(endTime); // 20:41
            between = (end.getTime() - begin.getTime());// 得到两者的毫秒数
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        long day = between / (24 * 60 * 60 * 1000);
        long hour = (between / (60 * 60 * 1000) - day * 24);
        long min = ((between / (60 * 1000)) - day * 24 * 60 - hour * 60);
        // long s = (between / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min
        // * 60);
        String res = null;
        if (day == 0) {
            res = hour + "小时" + min + "分";
            return res;
        }
        if (hour == 0) {
            res = min + "分";
            return res;
        }
        return (day + "天" + hour + "小时" + min + "分");

    }

    /**
     * 将字符串转为时间戳
     *
     * @param user_time
     * @return
     */
    public static String dateToTimestamp(String user_time) {
        String re_time = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date d;
        try {
            d = sdf.parse(user_time);
            long l = d.getTime();
            String str = String.valueOf(l);
            re_time = str.substring(0, 10);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return re_time;
    }

    //获取当前日期
    public static String getToday() {
        Date d = new Date();
        String date = new SimpleDateFormat("yyyy-MM-dd").format(d);
        return date;
    }

    //截取本月
    public static String getCurrentMonth() {
        Date d = new Date();
        String t = new SimpleDateFormat("yyyyMMdd").format(d);
        String m = t.substring(4, 6);
        return m;
    }

    //截取本年
    public static String getCurrentYear() {
        Date d = new Date();
        String t = new SimpleDateFormat("yyyyMMdd").format(d);
        String y = t.substring(0, 4);
        return y;
    }


    //获取昨天的日期
    public static String getDateOfYesterday() {
        Calendar c = Calendar.getInstance();
        long t = c.getTimeInMillis();
        long l = t - 24 * 3600 * 1000;
        Date d = new Date(l);
        String s = new SimpleDateFormat("yyyyMMdd").format(d);
        return s;
    }

    //获取上个月的第一天
    public static String getFirstDayOfLastMonth() {
//        if (TextUtils.isEmpty(type)){
//            type="yyyy-MM-dd";
//        }
        String str = "";
        Calendar lastDate = Calendar.getInstance();
        lastDate.set(Calendar.DATE,1); //set the date to be 1
        lastDate.add(Calendar.MONTH,-1);//reduce a month to be last month
//		lastDate.add(Calendar.DATE,-1);//reduce one day to be the first day of last month

        str=new SimpleDateFormat("yyyy-MM-dd").format(lastDate.getTime());
        return str;
    }

    // 获取上个月的最后一天
    public static String getLastDayOfLastMonth() {
        String str = "";
        Calendar lastDate = Calendar.getInstance();
        lastDate.set(Calendar.DATE, 1);//
        lastDate.add(Calendar.MONTH, -1);//
        lastDate.roll(Calendar.DATE, -1);//
        str = new SimpleDateFormat("yyyyMMdd").format(lastDate.getTime());
        return str;
    }

    //获取本月第一天
    public static String getFirstDayOfThisMonth() {
        String str = "";
        Calendar lastDate = Calendar.getInstance();
        lastDate.set(Calendar.DATE,1);//
//		lastDate.add(Calendar.MONTH,-1);//
//		lastDate.add(Calendar.DATE,-1);//

        str=new SimpleDateFormat("yyyyMMdd").format(lastDate.getTime());
        return str;
    }

    //获取本月最后一天
    public static String getLastDayOfThisMonth() {
        String str = "";
        Calendar lastDate = Calendar.getInstance();
        lastDate.set(Calendar.DATE,1);//
        lastDate.add(Calendar.MONTH,1);//
        lastDate.add(Calendar.DATE,-1);//

        str = new SimpleDateFormat("yyyy-MM-dd").format(lastDate.getTime());
        return str;
    }

    //判断闰年
    public static boolean isLeapYear(int year) {
        if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
            return true;
        }
        return false;
    }
}
