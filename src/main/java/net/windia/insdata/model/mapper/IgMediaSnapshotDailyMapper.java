package net.windia.insdata.model.mapper;

import net.windia.insdata.model.client.IgAPIClientMedia;
import net.windia.insdata.model.internal.IgMediaSnapshotDaily;
import net.windia.insdata.model.internal.IgProfile;
import net.windia.insdata.model.internal.InsDataUser;

import java.time.OffsetDateTime;

public class IgMediaSnapshotDailyMapper extends IgMediaSnapshotMapper<IgMediaSnapshotDaily> {

    @Override
    public IgMediaSnapshotDaily newInstance() {
        return new IgMediaSnapshotDaily();
    }

    @Override
    public IgMediaSnapshotDaily map(IgAPIClientMedia source) {

        IgMediaSnapshotDaily target = super.map(source);

        OffsetDateTime capturedAt = (OffsetDateTime) getExtraField(FIELD_CAPTURED_AT);
        IgProfile profile = (IgProfile) getExtraField(FIELD_IG_PROFILE);

        InsDataUser user = profile.getUser();
        target.realizeCapturedAt(capturedAt, user.getZoneId(), user.getFirstDayOfWeekInstance());

        return target;
    }
}
