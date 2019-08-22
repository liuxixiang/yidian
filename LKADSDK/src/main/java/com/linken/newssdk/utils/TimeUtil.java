package com.linken.newssdk.utils;

import android.content.Context;
import android.support.annotation.NonNull;

import com.linken.newssdk.R;
import com.linken.newssdk.data.pref.GlobalConfig;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;


public final class TimeUtil {
    public static final long SECOND = 1000L;
    public static final long MINUTE = 60 * SECOND;
    public static final long HOUR = 60 * MINUTE;
    public static final long DAY = 24 * HOUR;
    public static final long WEEK = 7 * DAY;
    public static final long MONTH = 30 * DAY;
    public static final long YEAR = 365 * DAY;

    private static final String TAG = "TimeUtil";

    private static final ThreadLocal<SimpleDateFormat> LOG_DATE_FORMAT_HOLDER
            = new ThreadLocalDateFormat("yyyy-MM-dd HH:mm:ss Z", TimeZone.getTimeZone("UTC"));

    private static final ThreadLocal<SimpleDateFormat> DATE_TIME_FORMAT_HOLDER
            = new ThreadLocalDateFormat("yyyy-MM-dd HH:mm:ss", TimeZone.getTimeZone("PRC"));

    private static final ThreadLocal<SimpleDateFormat> DATE_FORMAT_HOLDER
            = new ThreadLocalDateFormat("yyyy-MM-dd", TimeZone.getTimeZone("PRC"));

    private TimeUtil() {
    }

    /**
     * This function change the time string to xx minutes ago/xx days ago input
     * format: Mon Feb 04 09:43:33 +0800 2013
     */
    public static String convertWeiboTime(String time, Context ctx, long serverOffset) {
        if (time == null) {
            return null;
        }
        StringTokenizer st = new StringTokenizer(time);
        if (st.countTokens() != 6) {
            return null;
        }
        st.nextToken(); // skip the week day info
        String month = st.nextToken();
        String day = st.nextToken();
        String tm = st.nextToken();
        String timezone = st.nextToken();
        String year = st.nextToken();

        // parse the time values
        st = new StringTokenizer(tm, ":");
        if (st.countTokens() != 3) {
            return null;
        }
        String hour = st.nextToken();
        String minute = st.nextToken();
        String second = st.nextToken();

        int mm = getMonth(month.toLowerCase());
        if (mm < 0) {
            return null;
        }

        int zone = Integer.valueOf(timezone.substring(2, 3));
        if (timezone.indexOf('+') != 0) {
            zone = -zone;
        }

        // build the time, don't have the zone info
        int y = Integer.valueOf(year);
        int d = Integer.valueOf(day);
        int h = Integer.valueOf(hour);
        int m = Integer.valueOf(minute);
        int s = Integer.valueOf(second);
        GregorianCalendar calendera = new GregorianCalendar(y, mm, d, h, m, s);
        Date old = calendera.getTime();

        GregorianCalendar currentCalendar = new GregorianCalendar();
        Date current = currentCalendar.getTime();
        long localTimeZone = (currentCalendar.get(Calendar.ZONE_OFFSET) + calendera
                .get(Calendar.DST_OFFSET)) / HOUR;

        // standard server utc time //take the time zone into consideration
        long offset = current.getTime() - serverOffset - (old
                .getTime() - (zone - localTimeZone) * HOUR);

        if (offset > MONTH) {
            DateFormat formater = DATE_TIME_FORMAT_HOLDER.get();
            return formater.format(old);

        }

        return getString(offset, ctx);

    }

    public static Date timeFromStringToDate(String time) {
        if (time == null || time.length() != 19) {
            return null;
        }
        String yy = time.substring(0, 4);

        String mm = time.substring(5, 7);
        String dd = time.substring(8, 10);
        String hh = time.substring(11, 13);
        String min = time.substring(14, 16);
        String sec = time.substring(17, 19);

        // build the time
        GregorianCalendar calendera = new GregorianCalendar(Integer.valueOf(yy),
                Integer.valueOf(mm) - 1,
                Integer.valueOf(dd), Integer.valueOf(hh), Integer.valueOf(min),
                Integer.valueOf(sec));

        return calendera.getTime();
    }

    /**
     * This function change the time string to xx minutes ago/xx days ago input
     * format: 2013-02-26 15:25:00
     */
    public static String convertNewsTime(String time, Context ctx, long serverOffset) {
        if (time == null || time.length() != 19) {
            return null;
        }
        String yy = time.substring(0, 4);

        String mm = time.substring(5, 7);
        String dd = time.substring(8, 10);
        String hh = time.substring(11, 13);
        String min = time.substring(14, 16);
        String sec = time.substring(17, 19);

        // build the time
        GregorianCalendar calendera = new GregorianCalendar(Integer.valueOf(yy),
                Integer.valueOf(mm) - 1,
                Integer.valueOf(dd), Integer.valueOf(hh), Integer.valueOf(min),
                Integer.valueOf(sec));
        Date old = calendera.getTime();
        Date current = new Date();

        long newsTimeZoneOffset = 8 * HOUR; // Beijing Timezone
        int localTimeZoneOffset = calendera.get(Calendar.ZONE_OFFSET);
        int dstOffset = calendera.get(Calendar.DST_OFFSET);

        // standard time
        long offset = current.getTime() - serverOffset - old.getTime()
                + newsTimeZoneOffset - localTimeZoneOffset - dstOffset;

//        if (offset > (365 * 24 * 3600 * 1000L)) {
//            DateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            return formater.format(old);
//        }else if(offset > (30 * 24 * 3600 * 1000L)){
//            DateFormat formater = new SimpleDateFormat("MM-dd HH:mm:ss");
//            return formater.format(old);
//        }

        if (offset < 4 * DAY) {
            return getString(offset, ctx);
        }

        int year = Calendar.getInstance().get(Calendar.YEAR);
        if (Integer.valueOf(yy) < year) {
            //其他的显示2010-03-11格式
            return yy + '-' + mm + '-' + dd;
        } else {
            //当年的显示03-10格式
            return mm + '-' + dd;
        }
    }

    public static String convertVideoListTime(String time, Context ctx, long serverOffset) {
        if (time == null || time.length() != 19) {
            return null;
        }
        String yy = time.substring(0, 4);

        String mm = time.substring(5, 7);
        String dd = time.substring(8, 10);
        String hh = time.substring(11, 13);
        String min = time.substring(14, 16);
        String sec = time.substring(17, 19);

        // build the time
        GregorianCalendar calendera = new GregorianCalendar(Integer.valueOf(yy),
                Integer.valueOf(mm) - 1,
                Integer.valueOf(dd), Integer.valueOf(hh), Integer.valueOf(min),
                Integer.valueOf(sec));
        Date old = calendera.getTime();
        Date current = new Date();

        long newsTimeZoneOffset = 8 * HOUR; // Beijing Timezone
        int localTimeZoneOffset = calendera.get(Calendar.ZONE_OFFSET);
        int dstOffset = calendera.get(Calendar.DST_OFFSET);

        // standard time
        long offset = current.getTime() - serverOffset - old.getTime()
                + newsTimeZoneOffset - localTimeZoneOffset - dstOffset;
        if (offset < MINUTE) { // 1 minutes ago
            return ctx.getString(R.string.ydsdk_one_minute_ago);
        }

        if (offset < HOUR) { // in 1 hour
            long n = offset / MINUTE;
            return ctx.getString(R.string.ydsdk_minutes_ago, n);
        }

        return ctx.getString(R.string.ydsdk_one_hour_ago);
    }

    public static String convertNewsListTime(String time, Context ctx, long serverOffset) {
        if (time == null || time.length() != 19) {
            return null;
        }
        String yy = time.substring(0, 4);

        String mm = time.substring(5, 7);
        String dd = time.substring(8, 10);
        String hh = time.substring(11, 13);
        String min = time.substring(14, 16);
        String sec = time.substring(17, 19);

        // build the time
        GregorianCalendar calendera = new GregorianCalendar(Integer.valueOf(yy),
                Integer.valueOf(mm) - 1,
                Integer.valueOf(dd), Integer.valueOf(hh), Integer.valueOf(min),
                Integer.valueOf(sec));
        Date old = calendera.getTime();
        Date current = new Date();

        long newsTimeZoneOffset = 8 * HOUR; // Beijing Timezone
        int localTimeZoneOffset = calendera.get(Calendar.ZONE_OFFSET);
        int dstOffset = calendera.get(Calendar.DST_OFFSET);

        // standard time
        long offset = current.getTime() - serverOffset - old.getTime()
                + newsTimeZoneOffset - localTimeZoneOffset - dstOffset;

        if (offset < DAY) {//一天内文章
            return getString(offset, ctx);
        }

        int year = Calendar.getInstance().get(Calendar.YEAR);
        if (Integer.valueOf(yy) < year) {
            //其他的显示2010-03-11格式
            return yy + '-' + mm + '-' + dd;
        } else {
            //当年的一天前文章显示MM-DD hh:mm格式
//            return mm + "-" + dd + " " +hh + ":" +min;
            return mm + '-' + dd;
        }

    }

    public static String getCurrentSysHourAndSec() {
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = dateformat.format(convertToServerTimeMillis(System.currentTimeMillis()));
        if (time == null || time.length() != 19) {
            return null;
        }
        String yy = time.substring(0, 4);
        String mm = time.substring(5, 7);
        String dd = time.substring(8, 10);
        String hh = time.substring(11, 13);
        String min = time.substring(14, 16);
        String sec = time.substring(17, 19);
        return hh + ':' + min;
    }

    /*
     * <string name="one_minute_ago">一分钟前</string> <string
     * name="minutes_ago">%s分钟前</string> <string
     * name="half_hour_ago">半小时前</string> <string
     * name="one_hour_ago">一小时前</string> <string name="hours_ago">%s小时前</string>
     * <string name="one_day_ago">一天前</string> <string
     * name="days_ago">%s前</string> <string name="one_week_ago">一周前</string>
     * <string name="weeks_ago">%s周前</string> <string
     * name="one_month_ago">一月前</string> <string name="months_ago">%s月前</string>
     * <string name="one_year_ago">一年前</string> <string
     * name="years_ago">%s年前</string>
     */
    private static String getString(long offset, Context ctx) {

        if (offset < MINUTE) { // 1 minutes ago
            return ctx.getString(R.string.ydsdk_one_minute_ago);
        }

        if (offset < HOUR) { // in 1 hour
            long n = offset / MINUTE;
            return ctx.getString(R.string.ydsdk_minutes_ago, n);
        }

        if (offset < 2 * HOUR) { // in 2 hours
            return ctx.getString(R.string.ydsdk_one_hour_ago);
        }

        if (offset < DAY) { // in 24 hours
            long n = offset / (HOUR);
            return ctx.getString(R.string.ydsdk_hours_ago, n);
        }

        if (offset < 2 * DAY) { // in 2 day
            return ctx.getString(R.string.ydsdk_one_day_ago);
        }

        if (offset < WEEK) { // in 1 week
            long n = offset / DAY;
            return ctx.getString(R.string.ydsdk_days_ago, n);
        }

        if (offset < 2 * WEEK) { // in 2 week
            return ctx.getString(R.string.ydsdk_one_week_ago);
        }

        if (offset < MONTH) { // in 1 month
            long n = offset / WEEK;
            return ctx.getString(R.string.ydsdk_weeks_ago, n);
        }

        if (offset < 2 * MONTH) { // in 2 month
            return ctx.getString(R.string.ydsdk_one_month_ago);
        }

        if (offset < YEAR) { // in 1 year
            long n = offset / MONTH;
            return ctx.getString(R.string.ydsdk_months_ago, n);
        }

        if (offset < 2 * YEAR) { // in 2 year
            return ctx.getString(R.string.ydsdk_one_year_ago);
        }

        // above 2 year
        long n = offset / YEAR;
        return ctx.getString(R.string.ydsdk_years_ago, n);
    }

    private static int getMonth(String month) {
        switch (month) {
            case "jan":
                return 0;
            case "feb":
                return 1;
            case "mar":
                return 2;
            case "apr":
                return 3;
            case "may":
                return 4;
            case "jun":
                return 5;
            case "jul":
                return 6;
            case "aug":
                return 7;
            case "sep":
                return 8;
            case "oct":
                return 9;
            case "nov":
                return 10;
            case "dec":
                return 11;
            default:
                return -1;
        }
    }

    public static long calculateTimeOffset(long serverTime) {
        Calendar calendar = Calendar.getInstance();
        // server time is UTC time
        Date d = calendar.getTime(); // this will get UTC time
        long offset = d.getTime() - serverTime * 1000L;

        return offset;
    }

    /**
     * 存在静态变量中，防止频繁sp操作
     */
    private static long sServerDiffTime = 0;

    public static long getServerDiffTime() {
        if (sServerDiffTime == 0) {
            sServerDiffTime = GlobalConfig.getServerDiffTime();
        }

        return sServerDiffTime;
    }

    public static String getCurrentTimeForLog() {
        SimpleDateFormat formatter = LOG_DATE_FORMAT_HOLDER.get();
        return formatter.format(convertToServerTimeMillis(System.currentTimeMillis()));
    }

    public static String getCurrentTimeString() {
        SimpleDateFormat formatter = DATE_TIME_FORMAT_HOLDER.get();
        return formatter.format(convertToServerTimeMillis(System.currentTimeMillis()));
    }

    public static String convertLongToTimeString(long timeStamp) {
        SimpleDateFormat format = DATE_TIME_FORMAT_HOLDER.get();
        return format.format(convertToServerTimeMillis(timeStamp));
    }

    public static String convertLongToDateString(long timeStamp) {
        SimpleDateFormat formatter = DATE_FORMAT_HOLDER.get();
        return formatter.format(convertToServerTimeMillis(timeStamp));
    }

    public static int getOfflineStartHour(String autoStartTime) {
        int hour = 7;
        if (autoStartTime != null && autoStartTime.contains(":")) {
            try {
                int idx = autoStartTime.indexOf(':');
                String h = autoStartTime.substring(0, idx);
                hour = Integer.valueOf(h);
            } catch (Exception ignored) {
            }
        }
        return hour;
    }

    public static int getOfflineStartMinutes(String autoStartTime) {
        int minutes = 30;
        if (autoStartTime != null && autoStartTime.contains(":")) {
            try {
                int idx = autoStartTime.indexOf(':');
                String m = autoStartTime.substring(idx + 1);
                minutes = Integer.valueOf(m);
            } catch (Exception ignored) {
            }
        }
        return minutes;
    }

    /**
     * This function change the time string format "yyyy-mm-dd" <- UTF+8 time to Date(UTC).
     *
     * @return date, null if there is any errors.
     */
    public static Date convertYearMonthDate(String dateString) {
        Date date = null;
        try {
            SimpleDateFormat dateFormat = DATE_FORMAT_HOLDER.get();
            date = dateFormat.parse(dateString);
        } catch (ParseException ignored) {
        }
        return date;
    }

    /**
     * This function change the time string format "yyyy-mm-dd kk:mm" <- UTF+8 time to Date(UTC).
     *
     * @return date, null if there is any errors.
     */
    public static Date convertDateTime(String dateString) {
        Date date = null;
        try {
            SimpleDateFormat dateTimeFormat = DATE_TIME_FORMAT_HOLDER.get();
            date = dateTimeFormat.parse(dateString);
        } catch (ParseException ignored) {
        }
        return date;
    }

    public static long convertToServerTimeMillis(long currentTime) {
        return currentTime - TimeUtil.getServerDiffTime();
    }

    public static String getTimeZone() {
        Calendar c = Calendar.getInstance();
        int zoneOffset = c.get(Calendar.ZONE_OFFSET);
        int dstOffset = c.get(Calendar.DST_OFFSET);
        int timezone = (zoneOffset + dstOffset) / (int) HOUR;
        String tz = '+' + String.format("%04d", timezone);
        if (timezone < 0) {
            tz = '-' + String.format("%04d", -timezone);
        }
        return tz;
    }

    public static String getDayOfWeek(Calendar cal) {
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        return weekDays[w];
    }

    public static boolean isSameDayInCST(long a, long b) {
        long startTime;
        long endTime;
        if (a > b) {
            startTime = b;
            endTime = a;
        } else {
            startTime = a;
            endTime = b;
        }

        long delta = endTime - startTime;
        if (delta / DAY <= 1) {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(startTime);

            int startDay = calendar.get(Calendar.DAY_OF_MONTH);
            calendar.setTimeInMillis(endTime);
            int endDay = calendar.get(Calendar.DAY_OF_MONTH);
            if (startDay == endDay) {
                return true;
            }
        }
        return false;
    }

    private static long mLastActionTime;

    public static boolean isFastClick(long threshold) {
        long time = System.currentTimeMillis();
        long timeD = time - mLastActionTime;
        mLastActionTime = time;
        return 0 < timeD && timeD < threshold;
    }

    //srcDate: yyyy-MM-dd HH:mm:ss
    //HH:mm是00:00 返回值: MM月dd日(E)
    //HH:mm不是00:00 返回值: MM月dd日(E) HH:mm
    public static String convertToItineraryCardDate(String srcDate) {
        SimpleDateFormat displayDate;
        if (srcDate.contains("00:00:00")) {
            displayDate = new SimpleDateFormat("MM月dd日(E)", Locale.CHINA);
        } else {
            displayDate = new SimpleDateFormat("MM月dd日(E) HH:mm", Locale.CHINA);
        }

        DateFormat dateformat = DATE_TIME_FORMAT_HOLDER.get();
        String concertsBegin = null;
        try {
            concertsBegin = displayDate.format(dateformat.parse(srcDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return concertsBegin;
    }

    //计算当前时间距离end的日期差,以天为单位
    //end表示未来的一个时间 yyyy-MM-dd HH:mm:ss
    //如果end小于当前日期,返回-1
    public static long dateDiffInDays(String end) {
        SimpleDateFormat dateFormat = DATE_FORMAT_HOLDER.get();
        long diffDays = 0;
        try {
            Calendar today = Calendar.getInstance(TimeZone.getTimeZone("PRC"), Locale.CHINA);
            String todayStr = dateFormat.format(today.getTime());
            Date startDate = dateFormat.parse(todayStr);
            Date endDate = dateFormat.parse(end);

            //in milliseconds
            long diff = endDate.getTime() - startDate.getTime();

            if (diff < 0) {
                return -1;
            }

            long remainingTime = diff;
            diffDays = remainingTime / DAY;

//            remainingTime = remainingTime - diffDays * oneDay;
//            long diffHours = remainingTime / oneHour;
//
//            remainingTime = remainingTime - diffHours * oneHour;
//            long diffMinutes = remainingTime / oneMinute;
//
//            remainingTime = remainingTime - diffMinutes * oneMinute;
//            long diffSeconds = remainingTime / oneSecond;

//            System.out.print( diffDays + " days, " );
//            System.out.print( diffHours + " hours, " );
//            System.out.print( diffMinutes + " minutes, " );
//            System.out.print( diffSeconds + " seconds." );

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return diffDays;
    }

    /**
     * 转换秒为分钟秒的形式，万一秒最后是 0，也是需要写的
     */
    public static String ConvertSecondsToMinutesAndSeconds(int seconds) {
        int oneMinute = 60;
        int strMinute = (int) (seconds * 1.0 / oneMinute);
        int strSecond = seconds - strMinute * oneMinute;
        return strMinute > 0 ? strMinute + "\'" + strSecond + '"' : strSecond + "\"";
    }

    /**
     * Thread safe SimpleDateFormat class Read more: http://javarevisited.blogspot
     * .com/2012/05/how-to-use-threadlocal-in-java-benefits.html#ixzz42ImQh3lY
     */
    private static class ThreadLocalDateFormat extends ThreadLocal<SimpleDateFormat> {
        private final String mDateFormat;
        private final TimeZone mTimeZone;

        ThreadLocalDateFormat(
                @NonNull
                        String dateFormat,
                @NonNull
                        TimeZone timeZone) {
            mDateFormat = dateFormat;
            mTimeZone = timeZone;
        }

        @Override
        protected SimpleDateFormat initialValue() {

            SimpleDateFormat formatter = new SimpleDateFormat(mDateFormat, Locale.CHINA);
            formatter.setTimeZone(mTimeZone);
            return formatter;
        }
    }

    public static String getFormatVideoTime(long time) {
        if (time < 0) {
            return "";
        }
        long seconds = time % 60;
        long minutes = time / 60;
        if (minutes < 60) {
            return String.format("%02d:%02d", minutes, seconds);
        } else {
            long hours = minutes / 60;
            minutes %= 60;
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
    }

//    public static long getCurrentTimeInMillsForLog(){
//        Calendar c = Calendar.getInstance();
//        int zoneOffset = c.get(java.util.Calendar.ZONE_OFFSET);
//        int dstOffset = c.get(java.util.Calendar.DST_OFFSET);
//        Date d = c.getTime();
//
//        long totalMillis = d.getTime() - GlobalDataCache.getInstance().mServerTimeOffset -
// (zoneOffset + dstOffset);
//        c.setTimeInMillis(totalMillis);
//        return c.getTimeInMillis();
//    }

    public static String timeConvertFromIntToString(int time) {
        // 125 seconds convert to 02:05, 若大于一小时，则前面加小时数, 大于23，不管。

        if (time <= 0) {
            return "00:00";
        }

        int minute = time % 3600 / 60;
        int second = time % 60;

        String result = String.format("%02d", minute) + ':' + String.format("%02d", second);
        int hour = time / 3600;
        if (hour != 0) {
            if (hour > 23) {
                hour = 23;
            }
            result = String.format("%02d", hour) + ':' + result;
        }

        return result;
    }

//    public static long getHttpSendTime(Response response) {
//        try {
//            String timeStr = response.header("Date");
//            SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
//            Date date = format.parse(timeStr);
//            return date.getTime();
//        } catch (Exception ignored) {
//            return 0L;
//        }
//    }

    /**
     * 日期转时间戳（秒）
     *
     * @param _data
     * @return
     */
    public static long Date2s(String _data) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(_data);
            return date.getTime() / 1000;
        } catch (Exception e) {
            return 0;
        }
    }

    //判断选择的日期是否是今天
    public static boolean isToday(long time) {
        return isThisTime(time, "yyyy-MM-dd");
    }

    //判断选择的日期是否是本月
    public static boolean isThisMonth(long time) {
        return isThisTime(time, "yyyy-MM");
    }

    private static boolean isThisTime(long time, String pattern) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String param = sdf.format(date);//参数时间
        String now = sdf.format(new Date());//当前时间
        if (param.equals(now)) {
            return true;
        }
        return false;
    }
}
