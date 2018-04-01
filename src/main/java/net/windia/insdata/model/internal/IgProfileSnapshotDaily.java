package net.windia.insdata.model.internal;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table
public class IgProfileSnapshotDaily extends IgProfileDailyStat implements IgProfileSnapshot {

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
