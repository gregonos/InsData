package net.windia.insdata.service;

import lombok.extern.slf4j.Slf4j;
import net.windia.insdata.exception.UnsupportedGranularityException;
import net.windia.insdata.metric.IgAudienceStatType;
import net.windia.insdata.metric.StatGranularity;
import net.windia.insdata.model.internal.IgMedia;
import net.windia.insdata.model.internal.IgMediaDiff;
import net.windia.insdata.model.internal.IgMediaDiffHourly;
import net.windia.insdata.model.internal.IgMediaSnapshot;
import net.windia.insdata.model.internal.IgProfile;
import net.windia.insdata.model.internal.IgProfileAudienceDaily;
import net.windia.insdata.model.internal.IgProfileDiff;
import net.windia.insdata.model.internal.IgProfileSnapshot;
import net.windia.insdata.repository.IgMediaDiffDailyRepository;
import net.windia.insdata.repository.IgMediaDiffHourlyRepository;
import net.windia.insdata.repository.IgMediaRepository;
import net.windia.insdata.repository.IgMediaSnapshotDailyRepository;
import net.windia.insdata.repository.IgMediaSnapshotHourlyRepository;
import net.windia.insdata.repository.IgProfileAudienceDailyRepository;
import net.windia.insdata.repository.IgProfileDiffDailyRepository;
import net.windia.insdata.repository.IgProfileDiffHourlyRepository;
import net.windia.insdata.repository.IgProfileSnapshotDailyRepository;
import net.windia.insdata.repository.IgProfileSnapshotHourlyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static net.windia.insdata.metric.StatGranularity.DAILY;
import static net.windia.insdata.metric.StatGranularity.HOURLY;

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

    @Autowired
    private IgMediaSnapshotHourlyRepository mediaSnapshotHourlyRepo;

    @Autowired
    private IgMediaSnapshotDailyRepository mediaSnapshotDailyRepo;

    @Autowired
    private IgMediaRepository mediaRepo;

    @Autowired
    private IgProfileAudienceDailyRepository audienceRepo;

    public List<? extends IgProfileSnapshot> getSnapshots(Long igProfileId, StatGranularity granularity, OffsetDateTime since, OffsetDateTime until)
            throws UnsupportedGranularityException {

        if (HOURLY == granularity) {
            return snapshotHourlyRepo.findByIgProfileIdAndCapturedAtBetweenOrderByCapturedAtAsc(igProfileId, since, until);
        } else if (DAILY == granularity) {
            return snapshotDailyRepo.findByIgProfileIdAndCapturedAtBetweenOrderByCapturedAtAsc(igProfileId, since, until);
        } else {
            throw new UnsupportedGranularityException(granularity.getValue());
        }
    }

    public List<? extends IgProfileDiff> getDiffs(Long igProfileId, StatGranularity granularity, OffsetDateTime since, OffsetDateTime until)
            throws UnsupportedGranularityException {

        if (HOURLY == granularity) {
            return diffHourlyRepo.findByIgProfileIdAndCapturedAtBetweenOrderByCapturedAtAsc(igProfileId, since, until);
        } else if (DAILY == granularity) {
            return diffDailyRepo.findByIgProfileIdAndCapturedAtBetweenOrderByCapturedAtAsc(igProfileId, since, until);
        } else {
            throw new UnsupportedGranularityException(granularity.getValue());
        }
    }

    public List<? extends IgMediaDiff> getPostDiffs(Long igProfileId, StatGranularity granularity, OffsetDateTime since, OffsetDateTime until)
        throws UnsupportedGranularityException {
        if (HOURLY == granularity) {
            return mediaDiffHourlyRepo.findByIgProfileIdAndCapturedAtBetweenOrderByCapturedAtAsc(igProfileId, since, until);
        } else if (DAILY == granularity) {
            return mediaDiffDailyRepo.findByIgProfileIdAndCapturedAtBetweenOrderByCapturedAtAsc(igProfileId, since, until);
        } else {
            throw new UnsupportedGranularityException(granularity.getValue());
        }
    }

    public List<IgMediaDiffHourly> getDiffsOfPosts(Long igProfileId, StatGranularity granularity, OffsetDateTime since, OffsetDateTime until) {
        return mediaDiffHourlyRepo.findByIgProfileIdAndMediaCreatedAtBetweenOrderByCapturedAtAsc(igProfileId, since, until);
    }

    public List<? extends IgMediaSnapshot> getLatestSnapshotOfPosts(Long igProfileId, StatGranularity granularity, OffsetDateTime since, OffsetDateTime until)
        throws UnsupportedGranularityException {

        List <? extends IgMediaSnapshot> snapshots;

        if (HOURLY == granularity) {
            snapshots = mediaSnapshotHourlyRepo.findByIgProfileIdAndMediaCreatedAtBetweenOrderByCapturedAtDesc(igProfileId, since, until);
        } else if (DAILY == granularity) {
            snapshots = mediaSnapshotDailyRepo.findByIgProfileIdAndMediaCreatedAtBetweenOrderByCapturedAtDesc(igProfileId, since, until);
        } else {
            throw new UnsupportedGranularityException(granularity.getValue());
        }

        if (0 == snapshots.size()) {
            return snapshots;
        }

        Map<String, ? extends Optional<? extends IgMediaSnapshot>> optionals =
                // group by media id
                snapshots.stream().collect(Collectors.groupingBy(snapshot -> snapshot.getMedia().getId(),
                // find out the latest snapshot by comparing the capturedAt
                Collectors.maxBy((s1, s2) -> (int) Duration.between(s2.getCapturedAt(), s1.getCapturedAt()).getSeconds())));

        // filter the values collection and keep the optionals where the max is found, and turns the object of the optionals into a list
        return optionals.values().stream().filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    }

    public List<IgMedia> getPosts(Long igProfileId, StatGranularity granularity, OffsetDateTime since, OffsetDateTime until) {
        List<IgMedia> igMedias = mediaRepo.findIdByIgProfileIdAndCreatedAtBetween(igProfileId,
                since.minus(1, DAILY == granularity ? ChronoUnit.DAYS : ChronoUnit.HOURS), until);

        if (HOURLY == granularity) {
            igMedias.forEach(igMedia -> igMedia.setGranularity(HOURLY));
        }

        return igMedias;
    }

    public List<IgProfileAudienceDaily> getAudiences(IgProfile igProfile, IgAudienceStatType type) {
        String typeValue = type.name().toLowerCase();
        List<IgProfileAudienceDaily> capturedAtList = audienceRepo.findLatestCapturedAtOfNonZeroDiff(igProfile, typeValue);
        if (0 == capturedAtList.size()) {
            return capturedAtList;
        } else {
            return audienceRepo.findByIgProfileAndTypeAndCapturedAtOrderByCountDesc(
                    igProfile, typeValue, capturedAtList.get(0).getCapturedAt());
        }
    }
}
