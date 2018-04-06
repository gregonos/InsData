package net.windia.insdata.model.internal;

import java.time.OffsetDateTime;

public interface IgStat {

    Long getId();

    void setId(Long id);

    IgProfile getIgProfile();

    void setIgProfile(IgProfile igProfile);

    OffsetDateTime getIndicativeDate();
}
