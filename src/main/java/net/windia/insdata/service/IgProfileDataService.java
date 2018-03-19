package net.windia.insdata.service;

import lombok.extern.slf4j.Slf4j;
import net.windia.insdata.constants.StatGranularity;
import net.windia.insdata.model.internal.IgProfileDiff;
import net.windia.insdata.model.internal.IgProfileSnapshot;
import net.windia.insdata.repository.IgProfileDiffDailyRepository;
import net.windia.insdata.repository.IgProfileDiffHourlyRepository;
import net.windia.insdata.repository.IgProfileSnapshotDailyRepository;
import net.windia.insdata.repository.IgProfileSnapshotHourlyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class IgProfileDataService {

    @Autowired
    private IgProfileSnapshotHourlyRepository snapshotHourlyRepo;

    @Autowired
    private IgProfileSnapshotDailyRepository snapshotDailyRepo;

    @Autowired
    private IgProfileDiffHourlyRepository diffHourlyRepo;

    @Autowired
    private IgProfileDiffDailyRepository diffDailyRepo;

    public List<? extends IgProfileSnapshot> getSnapshots(Long igProfileId, String granularity, Date since, Date until) {
        if (StatGranularity.HOURLY.getValue().equalsIgnoreCase(granularity)) {
            return snapshotHourlyRepo.findByIgProfileIdAndCapturedAtBetweenOrderByCapturedAtAsc(igProfileId, since, until);
        } else if (StatGranularity.DAILY.getValue().equalsIgnoreCase(granularity)) {
            return snapshotDailyRepo.findByIgProfileIdAndCapturedAtBetweenOrderByCapturedAtAsc(igProfileId, since, until);
        } else {
            throw new IllegalArgumentException("Granularity has to be one of 'hourly' and 'daily'.");
        }
    }

    public List<? extends IgProfileDiff> getDiffs(Long igProfileId, String granularity, Date since, Date until) {
        if (StatGranularity.HOURLY.getValue().equalsIgnoreCase(granularity)) {
            return diffHourlyRepo.findByIgProfileIdAndComparedToBetweenOrderByComparedToAsc(igProfileId, since, until);
        } else if (StatGranularity.DAILY.getValue().equalsIgnoreCase(granularity)) {
            return diffDailyRepo.findByIgProfileIdAndComparedToBetweenOrderByComparedToAsc(igProfileId, since, until);
        } else {
            throw new IllegalArgumentException("Granularity has to be one of 'hourly' and 'daily'.");
        }
    }
}
