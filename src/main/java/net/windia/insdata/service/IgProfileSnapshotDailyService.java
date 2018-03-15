package net.windia.insdata.service;

import net.windia.insdata.model.internal.IgProfileSnapshotDaily;
import net.windia.insdata.model.mapper.IgProfileSnapshotDailyMapper;
import net.windia.insdata.model.mapper.IgProfileSnapshotMapper;
import net.windia.insdata.repository.IgProfileSnapshotDailyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IgProfileSnapshotDailyService extends IgProfileStatService<IgProfileSnapshotDaily> {

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
