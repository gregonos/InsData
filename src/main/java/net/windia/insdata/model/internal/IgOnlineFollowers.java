package net.windia.insdata.model.internal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table
public class IgOnlineFollowers extends IgProfileStat {

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
