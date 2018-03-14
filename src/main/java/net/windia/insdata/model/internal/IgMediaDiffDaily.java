package net.windia.insdata.model.internal;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table
public class IgMediaDiffDaily extends IgMediaStatImpl implements IgMediaDiff {

    @Column(nullable = false)
    private Date comparedTo;

    @Column(nullable = false)
    private Date week;

    @Column(nullable = false)
    private Byte month;

    @Column(nullable = false)
    private Byte weekday;

    public Date getComparedTo() {
        return comparedTo;
    }

    public void setComparedTo(Date comparedTo) {
        this.comparedTo = comparedTo;
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
