package net.windia.insdata.model.internal;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table
public class IgMediaSnapshotDaily extends IgMediaStatImpl implements IgMediaSnapshot, IgStatDaily {

    @Column(nullable = false)
    private LocalDate week;

    @Column(nullable = false)
    private Byte month;

    @Column(nullable = false)
    private Byte weekday;

    @Override
    public void realizeCapturedAt(OffsetDateTime capturedAt, ZoneId zoneId, DayOfWeek dayOfWeek) {
        this.setCapturedAt(capturedAt);
        calcDaily(zoneId, this, capturedAt, dayOfWeek);
    }

    @Override
    public OffsetDateTime getIndicativeDate() {
        return getCapturedAt();
    }

    public LocalDate getWeek() {
        return week;
    }

    public void setWeek(LocalDate week) {
        this.week = week;
    }

    public Byte getMonth() {
        return month;
    }

    public void setMonth(Byte month) {
        this.month = month;
    }

    public Byte getWeekday() {
        return weekday;
    }

    public void setWeekday(Byte weekday) {
        this.weekday = weekday;
    }
}
