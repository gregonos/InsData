package net.windia.insdata.service;

import lombok.extern.slf4j.Slf4j;
import net.windia.insdata.exception.UnsupportedGranularityException;
import net.windia.insdata.metric.StatGranularity;
import net.windia.insdata.model.internal.IgMediaDiff;
import net.windia.insdata.model.internal.IgProfileDiff;
import net.windia.insdata.model.internal.IgProfileSnapshot;
import net.windia.insdata.repository.IgMediaDiffDailyRepository;
import net.windia.insdata.repository.IgMediaDiffHourlyRepository;
import net.windia.insdata.repository.IgProfileDiffDailyRepository;
import net.windia.insdata.repository.IgProfileDiffHourlyRepository;
import net.windia.insdata.repository.IgProfileSnapshotDailyRepository;
import net.windia.insdata.repository.IgProfileSnapshotHourlyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
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

    @Autowired
    private IgMediaDiffHourlyRepository mediaDiffHourlyRepo;

    @Autowired
    private IgMediaDiffDailyRepository mediaDiffDailyRepo;

    public List<? extends IgProfileSnapshot> getSnapshots(Long igProfileId, StatGranularity granularity, OffsetDateTime since, OffsetDateTime until)
            throws UnsupportedGranularityException {

        if (StatGranularity.HOURLY == granularity) {
            return snapshotHourlyRepo.findByIgProfileIdAndCapturedAtBetweenOrderByCapturedAtAsc(igProfileId, since, until);
        } else if (StatGranularity.DAILY == granularity) {
            return snapshotDailyRepo.findByIgProfileIdAndCapturedAtBetweenOrderByCapturedAtAsc(igProfileId, since, until);
        } else {
            throw new UnsupportedGranularityException(granularity.getValue());
        }
    }

    public List<? extends IgProfileDiff> getDiffs(Long igProfileId, StatGranularity granularity, OffsetDateTime since, OffsetDateTime until)
            throws UnsupportedGranularityException {

        if (StatGranularity.HOURLY == granularity) {
            return diffHourlyRepo.findByIgProfileIdAndCapturedAtBetweenOrderByCapturedAtAsc(igProfileId, since, until);
        } else if (StatGranularity.DAILY == granularity) {
            return diffDailyRepo.findByIgProfileIdAndCapturedAtBetweenOrderByCapturedAtAsc(igProfileId, since, until);
        } else {
            throw new UnsupportedGranularityException(granularity.getValue());
        }
    }

    public List<? extends IgMediaDiff> getPostDiffs(Long igProfileId, StatGranularity granularity, OffsetDateTime since, OffsetDateTime until)
        throws UnsupportedGranularityException {
        if (StatGranularity.HOURLY == granularity) {
            return mediaDiffHourlyRepo.findByIgProfileIdAndCapturedAtBetweenOrderByCapturedAtAsc(igProfileId, since, until);
        } else if (StatGranularity.DAILY == granularity) {
            return mediaDiffDailyRepo.findByIgProfileIdAndCapturedAtBetweenOrderByCapturedAtAsc(igProfileId, since, until);
        } else {
            throw new UnsupportedGranularityException(granularity.getValue());
        }
    }
}
