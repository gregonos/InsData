package net.windia.insdata.service;

import net.windia.insdata.model.internal.IgProfile;
import net.windia.insdata.model.internal.IgProfileDiffHourly;
import net.windia.insdata.model.internal.IgProfileSnapshotHourly;
import net.windia.insdata.repository.IgProfileDiffHourlyRepository;
import net.windia.insdata.repository.IgProfileSnapshotHourlyRepository;
import net.windia.insdata.service.diffcalc.IgProfileDiffCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class IgProfileDiffHourlyService extends IgStatDiffService<IgProfileDiffHourly, IgProfileSnapshotHourly, IgProfile> {

    @Autowired
    private IgProfileSnapshotHourlyRepository igProfileSnapshotHourlyRepo;

    @Autowired
    private IgProfileDiffHourlyRepository igProfileDiffHourlyRepo;

    @Autowired
    @Qualifier(value = "igProfileDiffCalculator")
    private IgProfileDiffCalculator diffHourlyCalculator;

    @Override
    public IgProfile getStatKey(IgProfileSnapshotHourly newSnapshot) {
        return newSnapshot.getIgProfile();
    }

    @Override
    public String getCacheKey(IgProfile key) {
        return key.getId().toString();
    }

    @Override
    public IgProfileDiffHourly calculateDiff(IgProfileSnapshotHourly last, IgProfileSnapshotHourly newSnapshot, Date sinceTime) {

        IgProfileDiffHourly diff = new IgProfileDiffHourly();
        diff.setIgProfile(newSnapshot.getIgProfile());
        diff.setComparedTo(last.getCapturedAt());
        diff.setHour(last.getHour());

        diffHourlyCalculator.calculate(diff, last, newSnapshot, sinceTime);

        return diff;
    }

    @Override
    public IgProfileSnapshotHourly loadLastSnapshotFromPersistence(IgProfile igProfile) {
        return igProfileSnapshotHourlyRepo.findFirstByIgProfileOrderByCapturedAtDesc(igProfile);
    }

    @Override
    public CrudRepository getDiffRepository() {
        return igProfileDiffHourlyRepo;
    }
}
