package net.windia.insdata.model.internal;

import java.time.OffsetDateTime;

public interface IgStat {

    IgProfile getIgProfile();

    void setIgProfile(IgProfile igProfile);

    OffsetDateTime getIndicativeDate();
}
