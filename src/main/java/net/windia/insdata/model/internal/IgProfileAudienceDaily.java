package net.windia.insdata.model.internal;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table
public class IgProfileAudienceDaily extends IgStatBase implements IgSnapshot {

    @Column(nullable = false)
    private Date capturedAt;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String section;

    @Column(nullable = false)
    private Integer count;

    @Column
    private Integer diff;

    public Date getCapturedAt() {
        return capturedAt;
    }

    public void setCapturedAt(Date capturedAt) {
        this.capturedAt = capturedAt;
    }

    @Override
    public void realizeCapturedAt(Date capturedAt, String timeZone) {
        this.setCapturedAt(capturedAt);
    }

    @Override
    public Date getIndicativeDate() {
        return getCapturedAt();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getDiff() {
        return diff;
    }

    public void setDiff(Integer diff) {
        this.diff = diff;
    }
}
