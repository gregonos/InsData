package net.windia.insdata.service;

import net.windia.insdata.model.internal.IgMediaDiffDaily;
import net.windia.insdata.model.internal.IgMediaSnapshotDaily;
import net.windia.insdata.model.internal.IgProfile;
import net.windia.insdata.repository.IgMediaDiffDailyRepository;
import net.windia.insdata.repository.IgMediaSnapshotDailyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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
    protected IgMediaDiffDaily newDiffInstance() {
        return new IgMediaDiffDaily();
    }

    @Override
    protected void calculateDiffExtra(IgMediaDiffDaily diff, IgMediaSnapshotDaily lastSnapshot, IgMediaSnapshotDaily newSnapshot) {
        diff.setWeek(lastSnapshot.getWeek());
        diff.setMonth(lastSnapshot.getMonth());
        diff.setWeekday(lastSnapshot.getWeekday());
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

        return convertToMap(snapshotList);
    }
}
