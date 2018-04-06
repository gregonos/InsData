package net.windia.insdata.model.mapper;

import net.windia.insdata.model.client.IgAPIClientIgProfile;
import net.windia.insdata.model.internal.IgProfileSnapshotDaily;
import net.windia.insdata.model.internal.InsDataUser;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class IgProfileSnapshotDailyMapper extends IgProfileSnapshotMapper<IgProfileSnapshotDaily> {

    @Override
    protected IgProfileSnapshotDaily newInstance() {
        return new IgProfileSnapshotDaily();
    }

    @Override
    public IgProfileSnapshotDaily map(IgAPIClientIgProfile source) {
        IgProfileSnapshotDaily target = super.map(source);

        OffsetDateTime capturedAt = (OffsetDateTime) getExtraField(FIELD_CAPTURED_AT);
        InsDataUser user = target.getIgProfile().getUser();
        target.realizeCapturedAt(capturedAt, user.getZoneId(), user.getFirstDayOfWeekInstance());

        return target;
    }
}
