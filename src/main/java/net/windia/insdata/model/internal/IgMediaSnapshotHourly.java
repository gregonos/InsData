package net.windia.insdata.model.internal;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table
public class IgMediaSnapshotHourly extends IgMediaStatImpl implements IgMediaSnapshot, IgStatHourly {

    @Column(nullable = false)
    private Byte hour;

    public Byte getHour() {
        return hour;
    }

    public void setHour(Byte hour) {
        this.hour = hour;
    }

    @Override
    public Date getIndicativeDate() {
        return getCapturedAt();
    }

    @Override
    public void realizeCapturedAt(Date capturedAt, String timeZone) {
        this.setCapturedAt(capturedAt);
        calcHourly(timeZone, this, capturedAt);
    }
}
