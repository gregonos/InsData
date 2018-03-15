package net.windia.insdata.model.mapper;

import net.windia.insdata.model.client.IgAPIClientMedia;
import net.windia.insdata.model.internal.IgMediaSnapshotDaily;
import net.windia.insdata.model.internal.IgProfile;

import java.util.Date;

public class IgMediaSnapshotDailyMapper extends IgMediaSnapshotMapper<IgMediaSnapshotDaily> {

    @Override
    public IgMediaSnapshotDaily newInstance() {
        return new IgMediaSnapshotDaily();
    }

    @Override
    public IgMediaSnapshotDaily map(IgAPIClientMedia source) {

        IgMediaSnapshotDaily target = super.map(source);

        Date capturedAt = (Date) getExtraField(FIELD_CAPTURED_AT);
        IgProfile profile = (IgProfile) getExtraField(FIELD_IG_PROFILE);

        target.realizeCapturedAt(capturedAt, profile.getUser().getTimeZone());

        return target;
    }
}
