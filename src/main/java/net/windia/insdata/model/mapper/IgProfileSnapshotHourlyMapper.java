package net.windia.insdata.model.mapper;

import net.windia.insdata.model.client.IgAPIClientIgProfile;
import net.windia.insdata.model.internal.IgProfileSnapshotHourly;
import net.windia.insdata.model.internal.InsDataUser;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class IgProfileSnapshotHourlyMapper extends IgProfileSnapshotMapper<IgProfileSnapshotHourly> {

    @Override
    protected IgProfileSnapshotHourly newInstance() {
        return new IgProfileSnapshotHourly();
    }

    @Override
    public IgProfileSnapshotHourly map(IgAPIClientIgProfile source) {
        IgProfileSnapshotHourly target = super.map(source);

        OffsetDateTime capturedAt = (OffsetDateTime) getExtraField(FIELD_CAPTURED_AT);
        InsDataUser user = target.getIgProfile().getUser();
        target.realizeCapturedAt(capturedAt, user.getZoneId(), user.getFirstDayOfWeekInstance());

        return target;
    }
}
