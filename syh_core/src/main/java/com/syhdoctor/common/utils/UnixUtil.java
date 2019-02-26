
     package com.syhdoctor.common.utils;

import org.apache.commons.lang3.RandomStringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

public class UnixUtil {
    /**
     * 取得当前时间戳（精确到秒）
     *
     * @return long
     */
    public static long getNowTimeStamp() {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }


    /**
     * 通过id获取编号，自动补位
     *
     * @param type 1:用户 2：医生
     * @return
     */
    public static String addZeroForNum(long Id, int type) {
        StringBuffer noTemplate;
        String idstr = String.valueOf(Id) + String.valueOf(type);
        String no = idstr;
        if (idstr.length() < 4) {
            noTemplate = new StringBuffer("0000");
            no = noTemplate.replace(noTemplate.length() - idstr.length(), noTemplate.length(), idstr).toString();
        } else if (idstr.length() == 5) {
            noTemplate = new StringBuffer("000000");
            no = noTemplate.replace(noTemplate.length() - idstr.length(), noTemplate.length(), idstr).toString();
        } else if (idstr.length() == 7) {
            noTemplate = new StringBuffer("00000000");
            no = noTemplate.replace(noTemplate.length() - idstr.length(), noTemplate.length(), idstr).toString();
        }
        return no;
    }


    /**
     * 当前时间戳增加多少分钟
     *
     * @param minute
     * @return
     */
    public static long getNowTimeAddMinute(int minute) {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        c.set(Calendar.MILLISECOND, 0);
        c.add(Calendar.MINUTE, minute);
        return c.getTimeInMillis();
    }


    /**
     * 将Date转化成时间戳（精确到秒）
     *
     * @return long
     */
    public static long getTime(Date date) {
        return date.getTime();
    }

    /**
     * 日期格式字符串转换成时间戳
     *
     * @param time   时间戳
     * @param format 如：yyyy-MM-dd HH:mm:ss
     * @return String
     */
    public static String getDate(long time, String format) {
        if (format == null || "".equals(format)) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        c.setTimeInMillis(time);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(c.getTime());
    }


    /**
     * 日期格式字符串转换成时间戳
     *
     * @param date   时间
     * @param format 如：yyyy-MM-dd HH:mm:ss
     * @return String
     */
    public static String getDate(Date date, String format) {
        if (date == null) {
            return "";
        } else {
            return getDate(getTime(date), format);
        }
    }

    public static Date getDate(long time) {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        c.setTimeInMillis(time);
        return c.getTime();
    }


    public static String dateToWeek(String datetime) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        String temp;
        try {
            temp = dateToWeek(f.parse(datetime).getTime());
        } catch (Exception e) {
            e.printStackTrace();
            temp = "";
        }
        return temp;
    }

    public static String dateToWeek(long datetime) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        cal.setTimeInMillis(datetime);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    /**
     * 根据传入时间获取当前日期的周一到周日数据
     *
     * @param dateStr   输入日期
     * @param startTime 开始时间段
     * @param endTime   结束时间段
     * @return
     */
    public static Map<Integer, Map<String, Long>> weekDays(Date dateStr, String startTime, String endTime) {
        Map<Integer, Map<String, Long>> dateMap = new HashMap<>();
        try {
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateStr);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
                calendar.add(Calendar.DATE, -1);
            }
            for (int i = 0; i < 7; i++) {
                String dayTemp = dateFormat2.format(calendar.getTime());
                Map<String, Long> timeMap = new HashMap<>();
                long sTime = UnixUtil.dateTimeStamp(dayTemp + " " + startTime, "yyyy-MM-dd HH:mm:ss");
                long eTime = UnixUtil.dateTimeStamp(dayTemp + " " + endTime, "yyyy-MM-dd HH:mm:ss");
                if (sTime >=eTime) {
                    eTime = eTime + 24*60*60*1000;
                }
                timeMap.put("starttime", sTime);
                timeMap.put("endtime", eTime);
                if (i == 6) {
                    dateMap.put(0, timeMap);
                } else {
                    dateMap.put(i + 1, timeMap);
                }
                calendar.add(Calendar.DATE, 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateMap;
    }


    /**
     * 日期格式字符串转换成时间戳
     *
     * @param dateStr 字符串日期
     * @param format  如：yyyy-MM-dd HH:mm:ss
     * @return long
     */
    public static long dateTimeStamp(String dateStr, String format) {
        if (format == null || "".equals(format)) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        long temp = 0;
        try {

            SimpleDateFormat sdf = new SimpleDateFormat(format);
            if (StrUtil.isEmpty(dateStr.replaceAll("\\s*", ""))) {
                temp = 0;
            } else {
                temp = sdf.parse(dateStr).getTime();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return temp;
    }


    /**
     * Java将Unix时间戳转换成指定格式日期字符串
     *
     * @param timestamp 时间戳 如："1473048265";
     * @param format    要格式化的格式 默认："yyyy-MM-dd HH:mm:ss";
     * @return java.lang.String 返回结果 如："2018-01-24 16:06:42";
     */
    public static String timeStampDate(long timestamp, String format) {
        if (format == null || "".equals(format)) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        c.setTimeInMillis(timestamp);
        return new SimpleDateFormat(format).format(c.getTime());
    }

    /**
     * 获取查询的开始时间
     *
     * @param timestamp 时间戳 如："1473048265";
     * @return long 返回结果 如："2018-01-24 0:0:0" ;
     */
    public static long getStarttime(long timestamp) {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        c.setTimeInMillis(timestamp);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    /**
     * 获取查询的开始时间
     *
     * @param timestamp 时间戳 如："1473048265";
     * @return long 返回结果 如："2018-01-24 0:0:0" ;
     */
    public static Date getStartDate(long timestamp) {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        c.setTimeInMillis(timestamp);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * 获取查询的结束时间
     *
     * @param timestamp 时间戳 如："1473048265";
     * @return long 返回结果 如："2018-01-24 23:59:59" ;
     */
    public static long getEndtime(long timestamp) {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        c.setTimeInMillis(timestamp);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    /**
     * 获取查询的结束时间
     *
     * @param timestamp 时间戳 如："1473048265";
     * @return long 返回结果 如："2018-01-24 23:59:59" ;
     */
    public static Date getEndDate(long timestamp) {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        c.setTimeInMillis(timestamp);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    //秘钥生成的0到zZ
    private static class RANDOMSTRING {
        public static final String ALLCHAR = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    }

    //随机数生成的0到9
    private static class RANDOMNUMBER {
        public static final String ALLCHAR = "0123456789";
    }

    /**
     * 返回一个定长的随机字符串(包含大小写字母、数字)
     *
     * @param length 随机字符串长度
     * @return java.lang.String 随机字符串
     */
    public static String generateString(int length) {
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(RANDOMSTRING.ALLCHAR.charAt(random.nextInt(RANDOMSTRING.ALLCHAR.length())));
        }
        return sb.toString();

    }

    /**
     * 返回一个定长的随机数字
     *
     * @param length 随机字符串长度
     * @return java.lang.String 随机字符串
     */
    public static String generateNumber(int length) {
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(RANDOMSTRING.ALLCHAR.charAt(random.nextInt(RANDOMNUMBER.ALLCHAR.length())));
        }
        return sb.toString();

    }


    /**
     * 生成验证码
     *
     * @return
     */
    public static String getCode() {
        return RandomStringUtils.random(4, "0123456789");
    }

    /**
     * 根据时间生成随机数
     *
     * @return java.lang.String
     */
    public static String getCustomRandomString() {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        return new SimpleDateFormat("yyyyMMddHHmmssSSS").format(c.getTime()) + new Random().nextInt(999);
    }

    /**
     * 获取两个时间相差的值
     *
     * @param value 时间戳
     * @return java.lang.String
     */
    public static String getDownTime(long value) {
        return getDownTime(value, "yy年前mm月前dd天前hh小时前MM分前ss秒前");
    }

    /**
     * 获取两个时间相差的值
     *
     * @param value  时间戳
     * @param matStr 输出的格式
     * @return java.lang.String
     */
    public static String getDownTime(long value, String matStr) {
        Calendar startC = Calendar.getInstance();
        startC.setTimeInMillis(value);
        Calendar endC = Calendar.getInstance();
        endC.setTimeInMillis(getNowTimeStamp());
        int yy = endC.get(Calendar.YEAR) - startC.get(Calendar.YEAR);
        int yindex = matStr.indexOf("yy");
        int mon = endC.get(Calendar.MONTH) - startC.get(Calendar.MONTH);
        int monindex = matStr.indexOf("mm");
        int dd = endC.get(Calendar.DAY_OF_MONTH) - startC.get(Calendar.DAY_OF_MONTH);
        int dindex = matStr.indexOf("dd");
        int hh = endC.get(Calendar.HOUR_OF_DAY) - startC.get(Calendar.HOUR_OF_DAY);
        int hindex = matStr.indexOf("hh");
        int min = endC.get(Calendar.MINUTE) - startC.get(Calendar.MINUTE);
        int minindex = matStr.indexOf("MM");
        int ss = endC.get(Calendar.SECOND) - startC.get(Calendar.SECOND);
        int sindex = matStr.indexOf("ss");

        String temp = "刚刚";
        if (yindex >= 0 && monindex >= 0 && dindex >= 0 && hindex >= 0 && minindex >= 0 && sindex >= 0) {
            if (yy > 0) {
                matStr = matStr.substring(yindex, monindex);
                temp = matStr.replace("yy", yy + "");
            } else if (mon > 0) {
                matStr = matStr.substring(monindex, dindex);
                temp = matStr.replace("mm", mon + "");
            } else if (dd > 0) {
                matStr = matStr.substring(dindex, hindex);
                temp = matStr.replace("dd", dd + "");
            } else if (hh > 0) {
                matStr = matStr.substring(hindex, minindex);
                temp = matStr.replace("hh", hh + "");
            } else if (min > 0) {
                matStr = matStr.substring(minindex, sindex);
                temp = matStr.replace("MM", min + "");
            } else if (ss > 0) {
                matStr = matStr.substring(sindex);
                temp = matStr.replace("ss", ss + "");
            }
        }
        return temp;
    }

    public static long getMonthFirstDate(int amount) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        //获取当前月第一天：
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, amount);
        c.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
        String first = format.format(c.getTime());
        String time = String.format("%s %s", first, "00:00:00");
        System.out.println("===============last:" + dateTimeStamp(time, "yyyy-MM-dd hh:mm:ss"));
        return dateTimeStamp(time, "yyyy-MM-dd hh:mm:ss");
    }

    public static long getMonthLastDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        //获取当前月最后一天
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
        String last = format.format(ca.getTime());
        String time = String.format("%s %s", last, "23:59:59");
        System.out.println("===============last:" + dateTimeStamp(time, "yyyy-MM-dd hh:mm:ss"));
        return dateTimeStamp(time, "yyyy-MM-dd hh:mm:ss");
    }

    //获取当天开始时间戳
    public static long getStart() {
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTimeInMillis();
    }

    //取当前时间的前一天
    public static long getYesterday(){
        Date date=new Date();//取时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DATE,-1);//把日期往前减少一天，若想把日期向后推一天则将负数改为正数
        return calendar.getTimeInMillis();
    }

    public static Date getStartDate() {
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime();
    }

    //获取当天结束时间戳
    public static long getEndTime(long time) {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.setTimeInMillis(time);
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        return todayEnd.getTimeInMillis();
    }

    //获取当天结束时间戳
    public static long getEndTime() {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        return todayEnd.getTimeInMillis();

    }

    //取明天时间挫
    public static long getBeginDayOfTomorrow(Date getStartDate) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(getStartDate);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        return cal.getTimeInMillis();
    }

    //取当前时间的前七天
    public static long getBeginDaySeven(){
        Date date=new Date();//取时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DATE,-6);//把日期往前减少一天，若想把日期向后推一天则将负数改为正数
        return calendar.getTimeInMillis();
    }

    public static long getBeginDaySeven(int i){
        Date date=new Date();//取时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DATE,-i);//把日期往前减少一天，若想把日期向后推一天则将负数改为正数
        return calendar.getTimeInMillis();
    }


    //取昨天开始时间
    public static long getYesterdayStart() {
        Calendar cal = new GregorianCalendar();
        cal.setTime(getStartDate());
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return cal.getTimeInMillis();
    }

    public static void main(String[] args) {
        Map<Integer, Map<String, Long>> l=weekDays(new Date(),"08:00:00","08:00:00");
        System.out.println(getBeginDaySeven());
    }

}
