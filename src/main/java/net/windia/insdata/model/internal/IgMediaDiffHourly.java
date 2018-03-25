package net.windia.insdata.model.internal;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table
public class IgMediaDiffHourly extends IgMediaStatImpl implements IgMediaDiff, IgStatHourly {

    @Column(nullable = false)
    private Date comparedTo;

    @Column(nullable = false)
    private Byte hour;

    @Override
    public void realizeComparedTo(Date comparedTo, String timeZone) {
        this.setComparedTo(comparedTo);
        calcHourly(timeZone, this, comparedTo);
    }

    public Date getComparedTo() {
        return comparedTo;
    }

    public void setComparedTo(Date comparedTo) {
        this.comparedTo = comparedTo;
    }

    @Override
    public Date getIndicativeDate() {
        return getComparedTo();
    }

    public Byte getHour() {
        return hour;
    }

    public void setHour(Byte hour) {
        this.hour = hour;
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
