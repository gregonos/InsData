package net.windia.insdata.model.internal;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import javax.persistence.*;

@MappedSuperclass
public abstract class IgStatBase implements IgStat {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private IgProfile igProfile;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public IgProfile getIgProfile() {
        return igProfile;
    }

    public void setIgProfile(IgProfile igProfile) {
        this.igProfile = igProfile;
    }

    @Transient
    public abstract Date getIndicativeDate();

    public static void calcHourly(String timeZone, IgStatHourly hourly, Date time) {

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
        cal.setTime(time);

        hourly.setHour((byte) cal.get(Calendar.HOUR_OF_DAY));
    }

    public static void calcDaily(String timeZone, IgStatDaily daily, Date time) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
        cal.setTime(time);

        daily.setMonth((byte) cal.get(Calendar.MONTH));
        daily.setWeekday((byte) cal.get(Calendar.DAY_OF_WEEK));

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());

        daily.setWeek(cal.getTime());
    }
}
