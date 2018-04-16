package net.windia.insdata.model.internal;

import net.windia.insdata.metric.StatGranularity;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table
public class IgOnlineFollowers extends IgStatBase implements IgStatHourly {

    public IgOnlineFollowers() {
    }

    public IgOnlineFollowers(LocalDate date, int hour, byte weekday, double count, double percentage) {
        this(date, (byte) hour, weekday, count, percentage);
    }

    public IgOnlineFollowers(LocalDate date, byte hour, byte weekday, double count, double percentage) {
        this.date = date;
        this.hour = hour;
        this.weekday = weekday;
        this.count = new Double(count).intValue();
        this.percentage = (float) percentage;
    }

    @Column(nullable = false)
    private OffsetDateTime dateTime;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Byte hour;

    @Column(nullable = false)
    private Byte weekday;

    @Column(nullable = false)
    private Integer count;

    @Column(nullable = false)
    private Float percentage;

    public OffsetDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(OffsetDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public OffsetDateTime getIndicativeDate() {
        return getDateTime();
    }

    @Override
    public StatGranularity getGranularity() {
        return StatGranularity.HOURLY;
    }

    public Byte getHour() {
        return hour;
    }

    public void setHour(Byte hour) {
        this.hour = hour;
    }

    public Byte getWeekday() {
        return weekday;
    }

    public void setWeekday(Byte weekday) {
        this.weekday = weekday;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Float getPercentage() {
        return percentage;
    }

    public void setPercentage(Float percentage) {
        this.percentage = percentage;
    }
}
