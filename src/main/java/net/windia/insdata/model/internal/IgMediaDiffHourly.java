package net.windia.insdata.model.internal;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table
public class IgMediaDiffHourly extends IgMediaStatImpl implements IgMediaDiff {

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
