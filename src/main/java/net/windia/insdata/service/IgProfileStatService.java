package net.windia.insdata.service;

import lombok.extern.slf4j.Slf4j;
import net.windia.insdata.model.client.IgAPIClientIgProfile;
import net.windia.insdata.model.internal.IgProfile;
import net.windia.insdata.model.internal.IgProfileDiff;
import net.windia.insdata.model.internal.IgProfileSnapshot;
import net.windia.insdata.model.mapper.IgProfileSnapshotMapper;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public abstract class IgProfileStatService<Snapshot extends IgProfileSnapshot> {

    public void saveStat(IgProfile owningProfile, IgAPIClientIgProfile statRaw) {
        IgProfileSnapshotMapper<Snapshot> mapper = getMapper();
        mapper.addExtraField(IgProfileSnapshotMapper.FIELD_IG_PROFILE, owningProfile);
        mapper.addExtraField(IgProfileSnapshotMapper.FIELD_CAPTURED_AT, new Date());

        Snapshot newSnapshot = mapper.map(statRaw);
        getProfileDiffService().calculateAndSaveDiff(newSnapshot);
        getProfileSnapshotRepo().save(newSnapshot);
    }

    protected abstract CrudRepository<Snapshot, Long> getProfileSnapshotRepo();

    protected abstract <M extends IgProfileDiffService<IgProfileDiff, Snapshot>> M getProfileDiffService();

    protected abstract IgProfileSnapshotMapper<Snapshot> getMapper();

}