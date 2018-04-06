package net.windia.insdata.model.mapper;

import net.windia.insdata.model.client.IgAPIClientMedia;
import net.windia.insdata.model.internal.IgMediaSnapshotHourly;
import net.windia.insdata.model.internal.IgProfile;
import net.windia.insdata.model.internal.InsDataUser;

import java.time.OffsetDateTime;

public class IgMediaSnapshotHourlyMapper extends IgMediaSnapshotMapper<IgMediaSnapshotHourly> {

    @Override
    public IgMediaSnapshotHourly newInstance() {
        return new IgMediaSnapshotHourly();
    }

    @Override
    public IgMediaSnapshotHourly map(IgAPIClientMedia source) {

        IgMediaSnapshotHourly target = super.map(source);

        OffsetDateTime capturedAt = (OffsetDateTime) getExtraField(FIELD_CAPTURED_AT);
        IgProfile profile = (IgProfile) getExtraField(FIELD_IG_PROFILE);

        InsDataUser user = profile.getUser();
        target.realizeCapturedAt(capturedAt, user.getZoneId(), user.getFirstDayOfWeekInstance());

        return target;
    }
}
