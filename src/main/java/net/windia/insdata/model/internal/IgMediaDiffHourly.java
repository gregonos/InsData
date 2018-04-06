package net.windia.insdata.model.internal;

import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table
public class IgMediaDiffHourly extends IgMediaStatImpl implements IgMediaDiff, IgStatHourly {

    @Column(nullable = false)
    private OffsetDateTime comparedTo;

    @Column(nullable = false)
    private Byte hour;

    @Override
    public void realizeCapturedAt(OffsetDateTime capturedAt, ZoneId zoneId, DayOfWeek firstDayOfWeek) {
        this.setCapturedAt(capturedAt);
        calcHourly(zoneId, this, capturedAt);
    }

    public OffsetDateTime getComparedTo() {
        return comparedTo;
    }

    public void setComparedTo(OffsetDateTime comparedTo) {
        this.comparedTo = comparedTo;
    }

    @Override
    public OffsetDateTime getIndicativeDate() {
        return getCapturedAt();
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
