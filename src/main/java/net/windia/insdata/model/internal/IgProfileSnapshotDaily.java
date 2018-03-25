package net.windia.insdata.model.internal;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table
public class IgProfileSnapshotDaily extends IgProfileDailyStat implements IgProfileSnapshot {

    @Column(nullable = false)
    private Date capturedAt;

    public Date getCapturedAt() {
        return capturedAt;
    }

    public void setCapturedAt(Date capturedAt) {
        this.capturedAt = capturedAt;
    }

    @Override
    public void realizeCapturedAt(Date capturedAt, String timeZone) {
        this.setCapturedAt(capturedAt);
        calcDaily(timeZone, this, capturedAt);
    }

    @Override
    public Date getIndicativeDate() {
        return getCapturedAt();
    }

}
