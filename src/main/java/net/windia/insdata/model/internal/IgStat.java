package net.windia.insdata.model.internal;

import net.windia.insdata.metric.StatGranularity;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import javax.persistence.Transient;

public interface IgStat {

    IgProfile getIgProfile();

    void setIgProfile(IgProfile igProfile);

    @Transient
    OffsetDateTime getIndicativeDate();

    @Transient
    ZonedDateTime getAggregatingDate();

    @Transient
    StatGranularity getGranularity();
}
