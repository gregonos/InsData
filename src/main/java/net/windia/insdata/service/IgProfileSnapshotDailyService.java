package net.windia.insdata.service;

import net.windia.insdata.model.client.IgAPIClientIgProfile;
import net.windia.insdata.model.internal.IgProfile;
import net.windia.insdata.model.internal.IgProfileSnapshotDaily;
import net.windia.insdata.model.mapper.IgProfileDailyInsightsMapper;
import net.windia.insdata.model.mapper.IgProfileSnapshotDailyMapper;
import net.windia.insdata.model.mapper.IgProfileSnapshotMapper;
import net.windia.insdata.repository.IgProfileSnapshotDailyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IgProfileSnapshotDailyService extends IgProfileStatService<IgProfileSnapshotDaily> {

    @Autowired
    private IgProfileDailyInsightsMapper mapper;

    public void saveStatSeries(IgProfile owningProfile, IgAPIClientIgProfile statRaw) {
        mapper.addExtraField(IgProfileSnapshotMapper.FIELD_IG_PROFILE, owningProfile);

        List<IgProfileSnapshotDaily> snapshots = mapper.map(statRaw);
        for (IgProfileSnapshotDaily snapshot : snapshots) {
            getProfileDiffService().calculateAndSaveDiff(snapshot);
            getProfileSnapshotRepo().save(snapshot);
        }
    }

    @Autowired
    private IgProfileSnapshotDailyRepository profileSnapshotDailyRepo;

    @Autowired
    private IgProfileDiffDailyService igProfileDiffDailyService;

    @Override
    protected IgProfileSnapshotDailyRepository getProfileSnapshotRepo() {
        return profileSnapshotDailyRepo;
    }

    @Override
    protected IgProfileDiffDailyService getProfileDiffService() {
        return igProfileDiffDailyService;
    }

    @Override
    protected IgProfileSnapshotMapper<IgProfileSnapshotDaily> getMapper() {
        return new IgProfileSnapshotDailyMapper();
    }
}
