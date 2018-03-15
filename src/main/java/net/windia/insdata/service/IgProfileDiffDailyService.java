package net.windia.insdata.service;

import net.windia.insdata.model.internal.IgProfile;
import net.windia.insdata.model.internal.IgProfileDiffDaily;
import net.windia.insdata.model.internal.IgProfileSnapshotDaily;
import net.windia.insdata.repository.IgProfileDiffDailyRepository;
import net.windia.insdata.repository.IgProfileSnapshotDailyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
public class IgProfileDiffDailyService extends IgProfileDiffService<IgProfileDiffDaily, IgProfileSnapshotDaily> {

    @Autowired
    private IgProfileSnapshotDailyRepository igProfileSnapshotDailyRepo;

    @Autowired
    private IgProfileDiffDailyRepository igProfileDiffDailyRepo;

    @Override
    protected IgProfileDiffDaily newDiffInstance() {
        return new IgProfileDiffDaily();
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
