package net.windia.insdata.model.internal;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table
public class IgMediaDiffDaily extends IgMediaStatImpl implements IgMediaDiff, IgStatDaily {

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

    @Override
    public void realizeComparedTo(Date comparedTo, String timeZone) {
        this.setComparedTo(comparedTo);
        calcDaily(timeZone, this, comparedTo);
    }

    @Override
    public Date getIndicativeDate() {
        return getComparedTo();
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
