package net.windia.insdata.util;

import net.windia.insdata.metric.StatGranularity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@Service
public class DateTimeUtils {

    private static String facebookServerTimeZone;
    private static ZoneId facebookServerZoneId;

    @Value("${insdata.facebook.server-timezone-name}")
    public void setFacebookServerTimeZone(String timeZone) {
        facebookServerTimeZone = timeZone;
        facebookServerZoneId = ZoneId.of(timeZone);
    }

    public static String getFacebookServerTimeZone() {
        return facebookServerTimeZone;
    }

    public static int hourOfFacebookServer() {
        return hourInTimeZone(facebookServerTimeZone);
    }

    public static ZonedDateTime dateTimeOfFacebookServer(OffsetDateTime time, StatGranularity gran) {
        return dateTimeOfFacebookServer(time, gran, 0);
    }

    public static ZonedDateTime dateTimeOfFacebookServer(OffsetDateTime time, StatGranularity gran, Integer shift) {
        ZonedDateTime zoned = time.atZoneSameInstant(facebookServerZoneId);
        if (StatGranularity.DAILY == gran) {
            return zoned.truncatedTo(ChronoUnit.DAYS).plus(shift, ChronoUnit.DAYS);
        } else {
            return zoned.truncatedTo(ChronoUnit.HOURS).plus(shift, ChronoUnit.HOURS);
        }
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
        return ZonedDateTime.now(ZoneId.of(timeZoneId)).getHour();
    }
}
