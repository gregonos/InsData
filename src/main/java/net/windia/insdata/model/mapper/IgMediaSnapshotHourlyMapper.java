package net.windia.insdata.model.mapper;

import net.windia.insdata.model.client.IgAPIClientMedia;
import net.windia.insdata.model.internal.IgMediaSnapshotHourly;
import net.windia.insdata.model.internal.IgProfile;

import java.util.Date;

public class IgMediaSnapshotHourlyMapper extends IgMediaSnapshotMapper<IgMediaSnapshotHourly> {

    @Override
    public IgMediaSnapshotHourly newInstance() {
        return new IgMediaSnapshotHourly();
    }

    @Override
    public IgMediaSnapshotHourly map(IgAPIClientMedia source) {

        IgMediaSnapshotHourly target = super.map(source);

        Date capturedAt = (Date) getExtraField(FIELD_CAPTURED_AT);
        IgProfile profile = (IgProfile) getExtraField(FIELD_IG_PROFILE);

        target.realizeCapturedAt(capturedAt, profile.getUser().getTimeZone());

        return target;
    }
}
