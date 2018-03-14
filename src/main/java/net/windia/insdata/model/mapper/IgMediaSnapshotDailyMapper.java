package net.windia.insdata.model.mapper;

import net.windia.insdata.model.client.IgAPIClientMedia;
import net.windia.insdata.model.internal.IgMediaSnapshotDaily;
import net.windia.insdata.model.internal.IgProfile;
import net.windia.insdata.util.DateTimeUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class IgMediaSnapshotDailyMapper extends IgMediaSnapshotMapper<IgMediaSnapshotDaily> {

    @Override
    public IgMediaSnapshotDaily getInstance() {
        return new IgMediaSnapshotDaily();
    }

    @Override
    public IgMediaSnapshotDaily map(IgAPIClientMedia source) {

        IgMediaSnapshotDaily target = super.map(source);

        Date capturedAt = (Date) getExtraField(FIELD_CAPTURED_AT);
        IgProfile profile = (IgProfile) getExtraField(FIELD_IG_PROFILE);

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(profile.getUser().getTimeZone()));
        cal.setTime(capturedAt);

        target.setCapturedAt(capturedAt);
        target.setMonth((byte) cal.get(Calendar.MONTH));
        target.setWeekday((byte) cal.get(Calendar.DAY_OF_WEEK));
        target.setWeek(DateTimeUtils.getWeekStartingDate(capturedAt));

        return target;
    }
}
