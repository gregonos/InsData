package net.windia.insdata.model.internal;

import net.windia.insdata.metric.StatGranularity;

import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table
public class IgProfileDiffDaily extends IgProfileDailyStat implements IgProfileDiff {

    @Column(nullable = false)
    private OffsetDateTime comparedTo;

    public OffsetDateTime getComparedTo() {
        return comparedTo;
    }

    public void setComparedTo(OffsetDateTime comparedTo) {
        this.comparedTo = comparedTo;
    }

    @Override
    public void realizeCapturedAt(OffsetDateTime capturedAt, ZoneId zoneId, DayOfWeek firstDayOfWeek) {
        this.setCapturedAt(capturedAt);
        calcDaily(zoneId, this, capturedAt, firstDayOfWeek);
    }

    @Override
    public StatGranularity getGranularity() {
        return StatGranularity.DAILY;
    }
}
