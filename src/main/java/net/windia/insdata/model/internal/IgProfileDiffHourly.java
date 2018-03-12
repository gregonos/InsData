package net.windia.insdata.model.internal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table
public class IgProfileDiffHourly extends IgProfileBasicStat implements IgDiff {

    @Column(nullable = false)
    private Date comparedTo;

    @Column(nullable = false)
    private Byte hour;

    public Date getComparedTo() {
        return comparedTo;
    }

    public void setComparedTo(Date comparedTo) {
        this.comparedTo = comparedTo;
    }

    public Byte getHour() {
        return hour;
    }

    public void setHour(Byte hour) {
        this.hour = hour;
    }
}
