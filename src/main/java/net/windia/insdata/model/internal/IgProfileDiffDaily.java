package net.windia.insdata.model.internal;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table
public class IgProfileDiffDaily extends IgProfileDailyStat implements IgProfileDiff {

    @Column(nullable = false)
    private Date comparedTo;

    public Date getComparedTo() {
        return comparedTo;
    }

    public void setComparedTo(Date comparedTo) {
        this.comparedTo = comparedTo;
    }

    @Override
    public void realizeComparedTo(Date comparedTo, String timeZone) {
        this.setComparedTo(comparedTo);
        calcDaily(timeZone, this, comparedTo);
    }
}
