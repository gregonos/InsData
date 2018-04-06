package net.windia.insdata.model.internal;

import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table
public class IgProfileSnapshotHourly extends IgProfileStatImpl implements IgProfileSnapshot, IgStatHourly {

    @Column(nullable = false)
    private Byte hour;

    public Byte getHour() {
        return hour;
    }

    public void setHour(Byte hour) {
        this.hour = hour;
    }

    @Override
    public void realizeCapturedAt(OffsetDateTime capturedAt, ZoneId zoneId, DayOfWeek firstDayOfWeek) {
        this.setCapturedAt(capturedAt);
        calcHourly(zoneId, this, capturedAt);
    }

    @Override
    public OffsetDateTime getIndicativeDate() {
        return getCapturedAt();
    }

}
