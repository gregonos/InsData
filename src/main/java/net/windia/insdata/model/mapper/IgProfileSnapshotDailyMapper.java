package net.windia.insdata.model.mapper;

import net.windia.insdata.model.client.IgAPIClientIgProfile;
import net.windia.insdata.model.internal.IgProfileSnapshotDaily;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class IgProfileSnapshotDailyMapper extends IgProfileSnapshotMapper<IgProfileSnapshotDaily> {

    @Override
    protected IgProfileSnapshotDaily newInstance() {
        return new IgProfileSnapshotDaily();
    }

    @Override
    public IgProfileSnapshotDaily map(IgAPIClientIgProfile source) {
        IgProfileSnapshotDaily target = super.map(source);

        Date capturedAt = (Date) getExtraField(FIELD_CAPTURED_AT);
        target.realizeCapturedAt(capturedAt, target.getIgProfile().getUser().getTimeZone());

        return target;
    }
}
