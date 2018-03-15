package net.windia.insdata.service;

import lombok.extern.slf4j.Slf4j;
import net.windia.insdata.model.internal.IgMediaDiffHourly;
import net.windia.insdata.model.internal.IgMediaSnapshotHourly;
import net.windia.insdata.model.internal.IgProfile;
import net.windia.insdata.repository.IgMediaDiffHourlyRepository;
import net.windia.insdata.repository.IgMediaSnapshotHourlyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("igMediaDiffHourlyService")
public class IgMediaDiffHourlyService extends IgMediaDiffService<IgMediaSnapshotHourly, IgMediaDiffHourly> {

    @Autowired
    private IgMediaDiffHourlyRepository mediaDiffHourlyRepo;

    @Autowired
    private IgMediaSnapshotHourlyRepository mediaSnapshotHourlyRepo;

    @Override
    protected CrudRepository getDiffRepository() {
        return mediaDiffHourlyRepo;
    }

    @Override
    protected boolean isEligibleToCreateDiff(IgMediaSnapshotHourly newSnapshot) {
        return (new Date()).getTime() - newSnapshot.getCreatedAt().getTime() <= 3600000 * 2;
    }

    @Override
    protected IgMediaDiffHourly newDiffInstance() {
        return new IgMediaDiffHourly();
    }

    @Override
    protected IgMediaSnapshotHourly newSnapshotInstance(IgMediaSnapshotHourly reference) {
        IgMediaSnapshotHourly snapshot = new IgMediaSnapshotHourly();
        snapshot.realizeCapturedAt(reference.getCapturedAt(), reference.getIgProfile().getUser().getTimeZone());

        return snapshot;
    }

    @Override
    protected Map<String, IgMediaSnapshotHourly> loadLastSnapshotFromPersistence(IgProfile profile) {
        // Find out the latest capturedAt for a given profile
        IgMediaSnapshotHourly snapshot = mediaSnapshotHourlyRepo.findFirstByIgProfileOrderByCapturedAtDesc(profile);
        if (null == snapshot || null == snapshot.getCapturedAt()) {
            return null;
        }

        List<IgMediaSnapshotHourly> snapshotList = mediaSnapshotHourlyRepo.findByIgProfileAndCapturedAt(profile, snapshot.getCapturedAt());
        if (null == snapshotList || 0 == snapshotList.size()) {
            return null;
        }

        log.debug(snapshotList.size() + " media snapshot entries loaded from db.");

        return convertToMap(snapshotList);
    }
}
