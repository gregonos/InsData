package net.windia.insdata.model.mapper;

import net.windia.insdata.model.client.IgAPIClientMedia;
import net.windia.insdata.model.internal.IgMediaSnapshotHourly;
import net.windia.insdata.model.internal.IgProfile;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class IgMediaSnapshotHourlyMapper extends IgMediaSnapshotMapper<IgMediaSnapshotHourly> {

    @Override
    public IgMediaSnapshotHourly getInstance() {
        return new IgMediaSnapshotHourly();
    }

    @Override
    public IgMediaSnapshotHourly map(IgAPIClientMedia source) {

        IgMediaSnapshotHourly target = super.map(source);

        Date capturedAt = (Date) getExtraField(FIELD_CAPTURED_AT);
        IgProfile profile = (IgProfile) getExtraField(FIELD_IG_PROFILE);

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(profile.getUser().getTimeZone()));
        cal.setTime(capturedAt);

        target.setCapturedAt(capturedAt);
        target.setHour((byte) cal.get(Calendar.HOUR_OF_DAY));

        return target;
    }
}
