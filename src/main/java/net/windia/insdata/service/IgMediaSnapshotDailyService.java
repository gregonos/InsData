package net.windia.insdata.service;

import lombok.extern.slf4j.Slf4j;
import net.windia.insdata.model.internal.IgMediaSnapshotDaily;
import net.windia.insdata.model.mapper.IgMediaSnapshotDailyMapper;
import net.windia.insdata.repository.IgMediaSnapshotDailyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class IgMediaSnapshotDailyService extends IgMediaStatService<IgMediaSnapshotDaily> {

    @Autowired
    @Qualifier("igMediaDiffDailyService")
    private IgMediaDiffDailyService igMediaDiffDailyService;

    @Autowired
    private IgMediaSnapshotDailyRepository mediaSnapshotDailyRepo;

    @Override
    protected IgMediaDiffDailyService getMediaDiffService() {
        return igMediaDiffDailyService;
    }

    @Override
    protected IgMediaSnapshotDailyMapper getMapper() {
        return new IgMediaSnapshotDailyMapper();
    }

    @Override
    protected CrudRepository getMediaSnapshotRepo() {
        return mediaSnapshotDailyRepo;
    }
}
