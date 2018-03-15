package net.windia.insdata.model.internal;

import java.util.Date;

public interface IgDiff {

    void setComparedTo(Date comparedTo);

    Date getComparedTo();

    void realizeComparedTo(Date comparedTo, String timeZone);
}
