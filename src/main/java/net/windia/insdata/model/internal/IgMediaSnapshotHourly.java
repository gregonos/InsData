package net.windia.insdata.model.internal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table
public class IgMediaSnapshotHourly extends IgMediaStat implements IgSnapshot {

    @Column(nullable = false)
    private Date capturedAt;

    @Column(nullable = false)
    private Byte hour;

    public Date getCapturedAt() {
        return capturedAt;
    }

    public void setCapturedAt(Date capturedAt) {
        this.capturedAt = capturedAt;
    }

    public Byte getHour() {
        return hour;
    }

    public void setHour(Byte hour) {
        this.hour = hour;
    }
}
