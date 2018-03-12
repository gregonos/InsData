package net.windia.insdata.service;

import net.windia.insdata.model.internal.IgProfile;
import net.windia.insdata.model.internal.IgProfileDiffDaily;
import net.windia.insdata.model.internal.IgProfileSnapshotDaily;
import net.windia.insdata.repository.IgProfileDiffDailyRepository;
import net.windia.insdata.repository.IgProfileSnapshotDailyRepository;
import net.windia.insdata.service.diffcalc.IgProfileDiffCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class IgProfileDiffDailyService extends IgStatDiffService<IgProfileDiffDaily, IgProfileSnapshotDaily, IgProfile> {

    @Autowired
    private IgProfileSnapshotDailyRepository igProfileSnapshotDailyRepo;

    @Autowired
    private IgProfileDiffDailyRepository igProfileDiffDailyRepo;

    @Autowired
    @Qualifier(value = "igProfileDiffCalculator")
    private IgProfileDiffCalculator diffDailyCalculator;

    @Override
    public IgProfile getStatKey(IgProfileSnapshotDaily newSnapshot) {
        return newSnapshot.getIgProfile();
    }

    @Override
    public String getCacheKey(IgProfile key) {
        return key.getId().toString();
    }

    @Override
    public IgProfileDiffDaily calculateDiff(IgProfileSnapshotDaily lastSnapshot, IgProfileSnapshotDaily newSnapshot, Date sinceTime) {

        IgProfileDiffDaily diff = new IgProfileDiffDaily();
        diff.setIgProfile(newSnapshot.getIgProfile());
        diff.setComparedTo(lastSnapshot.getCapturedAt());

        diff.setWeek(lastSnapshot.getWeek());
        diff.setMonth(lastSnapshot.getMonth());
        diff.setWeekday(lastSnapshot.getWeekday());

        diffDailyCalculator.calculate(diff, lastSnapshot, newSnapshot, sinceTime);

        return diff;
    }

    @Override
    public IgProfileSnapshotDaily loadLastSnapshotFromPersistence(IgProfile key) {
        return igProfileSnapshotDailyRepo.findFirstByIgProfileOrderByCapturedAtDesc(key);
    }

    @Override
    public CrudRepository getDiffRepository() {
        return igProfileDiffDailyRepo;
    }
}
