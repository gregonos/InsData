package net.windia.insdata.model.internal;

import java.util.Date;

public interface IgStat {

    Long getId();

    void setId(Long id);

    IgProfile getIgProfile();

    void setIgProfile(IgProfile igProfile);

    Date getIndicativeDate();
}
