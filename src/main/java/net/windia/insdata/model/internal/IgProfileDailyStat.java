package net.windia.insdata.model.internal;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Date;

@MappedSuperclass
public abstract class IgProfileDailyStat extends IgProfileBasicStat {

    @Column(nullable = false)
    private Date week;

    @Column(nullable = false)
    private Byte month;

    @Column(nullable = false)
    private Byte weekday;

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
