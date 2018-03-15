package net.windia.insdata.service;

import net.windia.insdata.model.internal.IgProfileSnapshotHourly;
import net.windia.insdata.model.mapper.IgProfileSnapshotHourlyMapper;
import net.windia.insdata.model.mapper.IgProfileSnapshotMapper;
import net.windia.insdata.repository.IgProfileSnapshotHourlyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IgProfileSnapshotHourlyService extends IgProfileStatService<IgProfileSnapshotHourly> {

    @Autowired
    private IgProfileSnapshotHourlyRepository profileSnapshotHourlyRepo;

    @Autowired
    private IgProfileDiffHourlyService igProfileDiffHourlyService;

    @Override
    protected IgProfileSnapshotHourlyRepository getProfileSnapshotRepo() {
        return profileSnapshotHourlyRepo;
    }

    @Override
    protected IgProfileDiffHourlyService getProfileDiffService() {
        return igProfileDiffHourlyService;
    }

    @Override
    protected IgProfileSnapshotMapper<IgProfileSnapshotHourly> getMapper() {
        return new IgProfileSnapshotHourlyMapper();
    }
}
