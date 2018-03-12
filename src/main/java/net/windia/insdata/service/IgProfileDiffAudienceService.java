package net.windia.insdata.service;

import lombok.extern.slf4j.Slf4j;
import net.windia.insdata.model.internal.IgProfile;
import net.windia.insdata.model.internal.IgProfileAudienceDaily;
import net.windia.insdata.repository.IgProfileProfileAudienceDailyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class IgProfileDiffAudienceService {

    private Map<Long, Map<String, Map<String, Integer>>> cache;

    @Autowired
    private IgProfileProfileAudienceDailyRepository audienceRepo;

    @PostConstruct
    public void init() {
        cache = new HashMap<>();
    }

    public void enrichWithDiff(List<IgProfileAudienceDaily> audienceRecords) {

        if (null == audienceRecords || 0 == audienceRecords.size()) {
            return;
        }

        IgProfile igProfile = audienceRecords.get(0).getIgProfile();
        Map<String, Map<String, Integer>> lastSnapshot = lastSnapshot(igProfile);

        if (null == lastSnapshot) {
            log.debug("No previous audience records found! Diff calculation is cancelled.");
            return;
        }

        for (IgProfileAudienceDaily curRecord : audienceRecords) {
            Integer lastValue = lastSnapshot.get(curRecord.getType()).get(curRecord.getSection());

            if (null == lastValue) {
                lastValue = 0;
            }

            curRecord.setDiff(curRecord.getCount() - lastValue);
        }

        // Store current set as cache
        cache.put(igProfile.getId(), buildCacheFromList(audienceRecords));
    }

    private Map<String, Map<String, Integer>> lastSnapshot(IgProfile igProfile) {

        if (null == cache) {
            return null;
        }

        Map<String, Map<String, Integer>> lastAudienceSet = cache.get(igProfile.getId());

        return null == lastAudienceSet ? loadLastSnapshotFromPersistence(igProfile) : lastAudienceSet;
    }

    private Map<String, Map<String, Integer>> loadLastSnapshotFromPersistence(IgProfile igProfile) {

        // Get the last 'capturedAt' time of this profile
        IgProfileAudienceDaily latestSample = audienceRepo.findFirstByIgProfileOrderByCapturedAtDesc(igProfile);

        if (null == latestSample) {
            return null;
        }

        // Get all the records matching this capturedAt under the same profile
        List<IgProfileAudienceDaily> lastRecords = audienceRepo.findByIgProfileAndCapturedAtOrderByType(igProfile, latestSample.getCapturedAt());

        if (null == lastRecords || 0 == lastRecords.size()) {
            return null;
        }

        return buildCacheFromList(lastRecords);
    }

    private Map<String, Map<String, Integer>> buildCacheFromList(List<IgProfileAudienceDaily> lastRecords) {

        Map<String, Map<String, Integer>> lastSnapshot = new HashMap<>(4);

        // build cache
        for (IgProfileAudienceDaily audRecord : lastRecords) {
            String type = audRecord.getType();

            Map<String, Integer> audOfTheType = lastSnapshot.computeIfAbsent(type, k -> new HashMap<>(8));

            audOfTheType.put(audRecord.getSection(), audRecord.getCount());
        }

        return lastSnapshot;
    }
}
