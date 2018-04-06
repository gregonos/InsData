package net.windia.insdata.model.internal;

import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

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
    public abstract OffsetDateTime getIndicativeDate();

    public static void calcHourly(ZoneId zoneId, IgStatHourly hourly, OffsetDateTime time) {
        ZonedDateTime userTime = time.atZoneSameInstant(zoneId);
        hourly.setHour((byte) userTime.getHour());
    }

    public static void calcDaily(ZoneId zoneId, IgStatDaily daily, OffsetDateTime time, DayOfWeek dayOfWeek) {
        ZonedDateTime userTime = time.atZoneSameInstant(zoneId);
        daily.setMonth((byte) userTime.getMonth().getValue());
        daily.setWeekday((byte) userTime.getDayOfWeek().getValue());
        daily.setWeek(userTime.with(TemporalAdjusters.previousOrSame(dayOfWeek)).toLocalDate());
    }
}
