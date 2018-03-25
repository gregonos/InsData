package net.windia.insdata.model.internal;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table
public class IgMediaSnapshotDaily extends IgMediaStatImpl implements IgMediaSnapshot, IgStatDaily {

    @Column(nullable = false)
    private Date capturedAt;

    @Column(nullable = false)
    private Date week;

    @Column(nullable = false)
    private Byte month;

    @Column(nullable = false)
    private Byte weekday;

    public Date getCapturedAt() {
        return capturedAt;
    }

    @Override
    public void realizeCapturedAt(Date capturedAt, String timeZone) {
        this.setCapturedAt(capturedAt);
        calcDaily(timeZone, this, capturedAt);
    }

    public void setCapturedAt(Date capturedAt) {
        this.capturedAt = capturedAt;
    }

    @Override
    public Date getIndicativeDate() {
        return getCapturedAt();
    }

    public Date getWeek() {
        return week;
    }

    public void setWeek(Date week) {
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
