package net.windia.insdata.model.internal;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table
public class IgOnlineFollowers extends IgStat {

    public IgOnlineFollowers() {
    }

    public IgOnlineFollowers(Date date, int hour, byte weekday, double count, double percentage) {
        this(date, (byte) hour, weekday, count, percentage);
    }

    public IgOnlineFollowers(Date date, byte hour, byte weekday, double count, double percentage) {
        this.date = date;
        this.hour = hour;
        this.weekday = weekday;
        this.count = new Double(count).intValue();
        this.percentage = (float) percentage;
    }

    @Column(nullable = false)
    private Date date;

    @Column(nullable = false)
    private Byte hour;

    @Column(nullable = false)
    private Byte weekday;

    @Column(nullable = false)
    private Integer count;

    @Column(nullable = false)
    private Float percentage;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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
