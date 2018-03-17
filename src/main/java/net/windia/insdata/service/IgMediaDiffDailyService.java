package net.windia.insdata.service;

import lombok.extern.slf4j.Slf4j;
import net.windia.insdata.model.internal.IgMediaDiffDaily;
import net.windia.insdata.model.internal.IgMediaSnapshotDaily;
import net.windia.insdata.model.internal.IgProfile;
import net.windia.insdata.repository.IgMediaDiffDailyRepository;
import net.windia.insdata.repository.IgMediaSnapshotDailyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
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
        return (new Date()).getTime() - newSnapshot.getCreatedAt().getTime() <= 3600000 * 25;
    }

    @Override
    protected IgMediaDiffDaily newDiffInstance() {
        return new IgMediaDiffDaily();
    }

    @Override
    protected IgMediaSnapshotDaily newSnapshotInstance(IgMediaSnapshotDaily reference) {
        IgMediaSnapshotDaily instance = new IgMediaSnapshotDaily();
        Date capturedAt = reference.getCapturedAt();
        Calendar cal = Calendar.getInstance();
        cal.setTime(capturedAt);
        cal.add(Calendar.DATE, -1);
        instance.realizeCapturedAt(reference.getCapturedAt(), reference.getIgProfile().getUser().getTimeZone());

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
