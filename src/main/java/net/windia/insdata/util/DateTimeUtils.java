package net.windia.insdata.util;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateTimeUtils {

    public static int getHourInTimeZone(String timeZoneId) {
        return Calendar.getInstance(TimeZone.getTimeZone(timeZoneId)).get(Calendar.HOUR_OF_DAY);
    }

    public static int getHourInTimeZone(String timeZoneId, Date time) {
        Calendar instance = Calendar.getInstance(TimeZone.getTimeZone(timeZoneId));
        instance.setTime(time);
        return instance.get(Calendar.HOUR_OF_DAY);
    }

    public static boolean passedWithinOneHour(Date since, Date now) {
        long diff = now.getTime() - since.getTime();
        return 0 < diff && diff < 3600000;
    }

    public static Date getWeekStartingDate(Date time) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(time);

        instance.set(Calendar.HOUR_OF_DAY, 0);
        instance.clear(Calendar.MINUTE);
        instance.clear(Calendar.SECOND);
        instance.clear(Calendar.MILLISECOND);

        instance.set(Calendar.DAY_OF_WEEK, instance.getFirstDayOfWeek());

        return instance.getTime();
    }

    public static int hourInTimeZone(String timeZoneId) {
        return Calendar.getInstance(TimeZone.getTimeZone(timeZoneId)).get(Calendar.HOUR_OF_DAY);
    }
}
