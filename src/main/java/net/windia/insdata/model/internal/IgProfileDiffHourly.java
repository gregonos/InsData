package net.windia.insdata.model.internal;

import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table
public class IgProfileDiffHourly extends IgProfileStatImpl implements IgProfileDiff, IgStatHourly {

    @Column(nullable = false)
    private OffsetDateTime comparedTo;

    @Column(nullable = false)
    private Byte hour;

    public OffsetDateTime getComparedTo() {
        return comparedTo;
    }

    public void setComparedTo(OffsetDateTime comparedTo) {
        this.comparedTo = comparedTo;
    }

    @Override
    public void realizeCapturedAt(OffsetDateTime capturedAt, ZoneId zoneId, DayOfWeek firstDayOfWeek) {
        this.setCapturedAt(capturedAt);
        calcHourly(zoneId, this, capturedAt);
    }

    public Byte getHour() {
        return hour;
    }

    public void setHour(Byte hour) {
        this.hour = hour;
    }

    @Override
    public OffsetDateTime getIndicativeDate() {
        return getCapturedAt();
    }
}
