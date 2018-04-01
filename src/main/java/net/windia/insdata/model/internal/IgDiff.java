package net.windia.insdata.model.internal;

import java.util.Date;

public interface IgDiff {

    void setCapturedAt(Date capturedAt);

    Date getCapturedAt();

    void realizeCapturedAt(Date capturedAt, String timeZone);

    void setComparedTo(Date comparedTo);

    Date getComparedTo();

//    void realizeComparedTo(Date comparedTo, String timeZone);
}
