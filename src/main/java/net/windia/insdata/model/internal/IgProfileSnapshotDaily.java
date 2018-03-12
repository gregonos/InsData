package net.windia.insdata.model.internal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table
public class IgProfileSnapshotDaily extends IgProfileDailyStat implements IgSnapshot {

    @Column(nullable = false)
    private Date capturedAt;

    public Date getCapturedAt() {
        return capturedAt;
    }

    public void setCapturedAt(Date capturedAt) {
        this.capturedAt = capturedAt;
    }
}
