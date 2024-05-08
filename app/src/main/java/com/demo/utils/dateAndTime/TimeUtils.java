package com.demo.utils.dateAndTime;

import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class TimeUtils {

    private static final String TAG = TimeUnit.class.getSimpleName();
    public static final String DATE_FORMATE = "yyyy-MM-dd HH:mm:ss";

    // return time ago format from milliseconds
    // https://stackoverflow.com/questions/3859288/how-to-calculate-time-ago-in-java
    private static final List<Long> times = Arrays.asList(
            TimeUnit.DAYS.toMillis(365),
            TimeUnit.DAYS.toMillis(30),
            TimeUnit.DAYS.toMillis(1),
            TimeUnit.HOURS.toMillis(1),
            TimeUnit.MINUTES.toMillis(1),
            TimeUnit.SECONDS.toMillis(1));
    private static final List<String> timesString = Arrays.asList("year", "month", "day", "hour", "minute", "second");

    public static String toDuration(long duration) {

        long now = System.currentTimeMillis();
        long diff = now - duration;

        StringBuffer res = new StringBuffer();
        for (int i = 0; i < TimeUtils.times.size(); i++) {
            Long current = TimeUtils.times.get(i);
            long temp = diff / current;
            if (temp > 0) {
                res.append(temp).append(" ").append(TimeUtils.timesString.get(i)).append(temp != 1 ? "s" : "").append(" ago");
                break;
            }
        }
//        Utils.printLog(TAG, "duration:: " + res);

        if ("".equals(res.toString()))
            return "Just now";
//            return "0 seconds ago";
        else
            return res.toString();
    }


    // convert utc time to local time
    // https://stackoverflow.com/questions/14853389/how-to-convert-utc-timestamp-to-device-local-time-in-android
    public static String getDate(String OurDate) {
//        Utils.printLog(TAG, "Date in UTC :: " + OurDate);
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMATE, Locale.US);
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date value = formatter.parse(OurDate);

            SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMATE, Locale.US); //this format changeable MM-dd-yyyy HH:mm
            dateFormatter.setTimeZone(TimeZone.getDefault());
            OurDate = dateFormatter.format(value);
//            Utils.printLog(TAG, "Date getDate local :: " + OurDate);

        } catch (Exception e) {
            OurDate = "0000-00-00 00:00";
        }
        return OurDate;
    }

    public static long getTimeInMilliSeconds(String givenDateString) {
        long timeInMilliseconds = 0;
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMATE, Locale.US);
        try {
            Date mDate = sdf.parse(getDate(givenDateString));  // convert utc to local date
            timeInMilliseconds = mDate.getTime();
//            Utils.printLog(TAG, "Date in milli :: " + timeInMilliseconds);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timeInMilliseconds;
    }

    // convert date string into milliseconds
    public static long timeInMilliSeconds(String givenDateString) {
        long timeInMilliseconds = 0;
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMATE, Locale.US);
        try {
            Date mDate = sdf.parse(getDate(givenDateString));
            timeInMilliseconds = mDate.getTime();
//            Utils.printLog(TAG, "Date in milli :: " + timeInMilliseconds);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        timeInMilliseconds = Calendar.getInstance(TimeZone.getDefault()).getTimeInMillis() - timeInMilliseconds;

        return timeInMilliseconds;
    }


    // convert utc date_time into time ago formate (ex. 1 days ago )
    //https://stackoverflow.com/questions/35858608/how-to-convert-time-to-time-ago-in-android
    public static String convertDateToTimeAgo(String UTCdatetime) {
        Date date = null;
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        try {
            date = inputFormat.parse(TimeUtils.getDate(UTCdatetime));  // convert utc datetime into local datetime
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date != null) {
            // pass milliseconds with current system milliseconds
            return String.valueOf(DateUtils.getRelativeTimeSpanString(date.getTime(), Calendar.getInstance().getTimeInMillis(), DateUtils.SECOND_IN_MILLIS));
        }
        return "";
    }
}
