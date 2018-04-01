package net.windia.insdata.util;

import net.windia.insdata.metric.StatGranularity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@Service
public class DateTimeUtils {

    private static String facebookServerTimeZone;

    @Value("${insdata.facebook.server-timezone-name}")
    public void setFacebookServerTimeZone(String timeZone) {
        facebookServerTimeZone = timeZone;
    }

    public static String getFacebookServerTimeZone() {
        return facebookServerTimeZone;
    }

    public static int hourOfFacebookServer() {
        return hourInTimeZone(facebookServerTimeZone);
    }

    public static Date dateTimeOfFacebookServer(Date time, StatGranularity gran) {
        Calendar instance = Calendar.getInstance(TimeZone.getTimeZone(facebookServerTimeZone));
        instance.setTime(time);
        if (StatGranularity.DAILY == gran) {
            instance.set(Calendar.HOUR_OF_DAY, 0);
        }
        instance.clear(Calendar.MINUTE);
        instance.clear(Calendar.SECOND);
        instance.clear(Calendar.MILLISECOND);

        return instance.getTime();
    }

    public static int hourInTimeZone(String timeZoneId, Date time) {
        Calendar instance = Calendar.getInstance(TimeZone.getTimeZone(timeZoneId));
        instance.setTime(time);
        return instance.get(Calendar.HOUR_OF_DAY);
    }

    public static boolean passedWithinOneHour(Date since, Date now) {
        long diff = now.getTime() - since.getTime();
        return 0 < diff && diff < 3600000;
    }

    public static int hourInTimeZone(String timeZoneId) {
        return Calendar.getInstance(TimeZone.getTimeZone(timeZoneId)).get(Calendar.HOUR_OF_DAY);
    }
}
