package net.windia.insdata.model.internal;

import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.time.ZoneId;

public interface IgSnapshot {

    void setCapturedAt(OffsetDateTime capturedAt);

    OffsetDateTime getCapturedAt();

    void realizeCapturedAt(OffsetDateTime capturedAt, ZoneId zoneId, DayOfWeek dayOfWeek);
}
