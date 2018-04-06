package net.windia.insdata.service;

import lombok.extern.slf4j.Slf4j;
import net.windia.insdata.model.internal.IgMediaDiffDaily;
import net.windia.insdata.model.internal.IgMediaSnapshotDaily;
import net.windia.insdata.model.internal.IgProfile;
import net.windia.insdata.model.internal.InsDataUser;
import net.windia.insdata.repository.IgMediaDiffDailyRepository;
import net.windia.insdata.repository.IgMediaSnapshotDailyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("igMediaDiffDailyService")
public class IgMediaDiffDailyService extends IgMediaDiffService<IgMediaSnapshotDaily, IgMediaDiffDaily> {

    @Autowired
    private IgMediaDiffDailyRepository mediaDiffDailyRepo;

    @Autowired
    private IgMediaSnapshotDailyRepository mediaSnapshotDailyRepo;

    @Override
    protected CrudRepository getDiffRepository() {
        return mediaDiffDailyRepo;
    }

    @Override
    protected boolean isEligibleToCreateDiff(IgMediaSnapshotDaily newSnapshot) {
        return newSnapshot.getCapturedAt().until(OffsetDateTime.now(), ChronoUnit.HOURS) <= 24;
    }

    @Override
    protected IgMediaDiffDaily newDiffInstance() {
        return new IgMediaDiffDaily();
    }

    @Override
    protected IgMediaSnapshotDaily newSnapshotInstance(IgMediaSnapshotDaily reference) {
        IgMediaSnapshotDaily instance = new IgMediaSnapshotDaily();
        InsDataUser user = reference.getIgProfile().getUser();
        instance.realizeCapturedAt(reference.getMedia().getCreatedAt(), user.getZoneId(), user.getFirstDayOfWeekInstance());

        return instance;
    }

    @Override
    protected Map<String, IgMediaSnapshotDaily> loadLastSnapshotFromPersistence(IgProfile profile) {
        // Find out the latest capturedAt for a given profile
        IgMediaSnapshotDaily snapshot = mediaSnapshotDailyRepo.findFirstByIgProfileOrderByCapturedAtDesc(profile);
        if (null == snapshot || null == snapshot.getCapturedAt()) {
            return null;
        }

        List<IgMediaSnapshotDaily> snapshotList = mediaSnapshotDailyRepo.findByIgProfileAndCapturedAt(profile, snapshot.getCapturedAt());
        if (null == snapshotList || 0 == snapshotList.size()) {
            return null;
        }

        log.debug(snapshotList.size() + " media snapshot entries loaded from db.");

        return convertToMap(snapshotList);
    }
}
