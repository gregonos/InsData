package net.windia.insdata.model.internal;

import java.util.Date;

public interface IgSnapshot {

    void setCapturedAt(Date capturedAt);

    Date getCapturedAt();

    void realizeCapturedAt(Date capturedAt, String timeZone);
}
