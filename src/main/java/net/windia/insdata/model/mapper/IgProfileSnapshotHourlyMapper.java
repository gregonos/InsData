package net.windia.insdata.model.mapper;

import net.windia.insdata.model.client.IgAPIClientIgProfile;
import net.windia.insdata.model.internal.IgProfileSnapshotHourly;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class IgProfileSnapshotHourlyMapper extends IgProfileSnapshotMapper<IgProfileSnapshotHourly> {

    @Override
    protected IgProfileSnapshotHourly newInstance() {
        return new IgProfileSnapshotHourly();
    }

    @Override
    public IgProfileSnapshotHourly map(IgAPIClientIgProfile source) {
        IgProfileSnapshotHourly target = super.map(source);

        Date capturedAt = (Date) getExtraField(FIELD_CAPTURED_AT);
        target.realizeCapturedAt(capturedAt, target.getIgProfile().getUser().getTimeZone());

        return target;
    }
}
