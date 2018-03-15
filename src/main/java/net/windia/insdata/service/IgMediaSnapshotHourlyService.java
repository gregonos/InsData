package net.windia.insdata.service;

import lombok.extern.slf4j.Slf4j;
import net.windia.insdata.model.internal.IgMediaSnapshotHourly;
import net.windia.insdata.model.mapper.IgMediaSnapshotHourlyMapper;
import net.windia.insdata.model.mapper.IgMediaSnapshotMapper;
import net.windia.insdata.repository.IgMediaSnapshotHourlyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class IgMediaSnapshotHourlyService extends IgMediaStatService<IgMediaSnapshotHourly> {

    @Autowired
    private IgMediaDiffHourlyService igMediaDiffHourlyService;

    @Autowired
    private IgMediaSnapshotHourlyRepository mediaSnapshotHourlyRepo;

    @Override
    protected IgMediaDiffHourlyService getMediaDiffService() {
        return igMediaDiffHourlyService;
    }

    @Override
    protected IgMediaSnapshotMapper<IgMediaSnapshotHourly> getMapper() {
        return new IgMediaSnapshotHourlyMapper();
    }

    @Override
    protected CrudRepository getMediaSnapshotRepo() {
        return mediaSnapshotHourlyRepo;
    }
}
