package net.windia.insdata.model.internal;

import net.windia.insdata.metric.StatGranularity;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table
public class IgMediaDiffDaily extends IgMediaStatImpl implements IgMediaDiff, IgStatDaily {

    @Column(nullable = false)
    private OffsetDateTime comparedTo;

    @Column(nullable = false)
    private LocalDate week;

    @Column(nullable = false)
    private Byte month;

    @Column(nullable = false)
    private Byte weekday;

    public OffsetDateTime getComparedTo() {
        return comparedTo;
    }

    public void setComparedTo(OffsetDateTime comparedTo) {
        this.comparedTo = comparedTo;
    }

    @Override
    public void realizeCapturedAt(OffsetDateTime capturedAt, ZoneId zoneId, DayOfWeek firstDayOfWeek) {
        this.setCapturedAt(capturedAt);
        calcDaily(zoneId, this, capturedAt, firstDayOfWeek);
    }

    @Override
    public StatGranularity getGranularity() {
        return StatGranularity.DAILY;
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

    public boolean isChanged() {
        return 0 != getLikes() ||
                0 != getComments() ||
                0 != getImpressions() ||
                0 != getReach() ||
                0 != getEngagement() ||
                0 != getSaved() ||
                0 != getVideoViews();
    }
}
