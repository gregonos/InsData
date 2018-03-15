package net.windia.insdata.service;

import net.windia.insdata.model.internal.IgProfile;
import net.windia.insdata.model.internal.IgProfileDiffHourly;
import net.windia.insdata.model.internal.IgProfileSnapshotHourly;
import net.windia.insdata.repository.IgProfileDiffHourlyRepository;
import net.windia.insdata.repository.IgProfileSnapshotHourlyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
public class IgProfileDiffHourlyService extends IgProfileDiffService<IgProfileDiffHourly, IgProfileSnapshotHourly> {

    @Autowired
    private IgProfileSnapshotHourlyRepository igProfileSnapshotHourlyRepo;

    @Autowired
    private IgProfileDiffHourlyRepository igProfileDiffHourlyRepo;

    @Override
    protected IgProfileDiffHourly newDiffInstance() {
        return new IgProfileDiffHourly();
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
