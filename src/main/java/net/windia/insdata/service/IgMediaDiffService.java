package net.windia.insdata.service;

import lombok.extern.slf4j.Slf4j;
import net.windia.insdata.model.internal.IgMediaDiff;
import net.windia.insdata.model.internal.IgMediaSnapshot;
import net.windia.insdata.model.internal.IgProfile;
import net.windia.insdata.model.internal.InsDataUser;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;

@Slf4j
public abstract class IgMediaDiffService<Snapshot extends IgMediaSnapshot, Diff extends IgMediaDiff> {


    private Map<Long, Map<String, Snapshot>> lastSnapshotCache;

    @PostConstruct
    public void init() {
        lastSnapshotCache = new HashMap<>(2);
    }

    private Map<String, Snapshot> lastSnapshot(IgProfile profile) {

        if (null == lastSnapshotCache) {
            return null;
        }

        Map<String, Snapshot> lastSnapshot = lastSnapshotCache.get(profile.getId());
        if (null == lastSnapshot) {
            log.debug("No media stat cache pool found for profile [" + profile.getId() + "], loading from db...");
            lastSnapshot = loadLastSnapshotFromPersistence(profile);
            lastSnapshotCache.put(profile.getId(), lastSnapshot);
        } else {
            log.debug("Existing media stat cache pool found for profile [" + profile.getId() + "], cache size: " + lastSnapshot.size());
        }

        return lastSnapshot;
    }

    public void calculateAndSaveDiff(IgProfile profile, List<Snapshot> snapshotList) {
        Map<String, Snapshot> lastSnapshotMap = lastSnapshot(profile);

        if (null == lastSnapshotMap) {
            lastSnapshotMap = new HashMap<>(snapshotList.size());
            lastSnapshotCache.put(profile.getId(), lastSnapshotMap);
        }

        List<Diff> diffList = calculateDiff(profile, lastSnapshotMap, snapshotList);

        getDiffRepository().saveAll(diffList);

        Map<String, Snapshot> newSnapshotMap = convertToMap(snapshotList);
        lastSnapshotMap.putAll(newSnapshotMap);
        log.debug(newSnapshotMap.size() + " snapshot entries are updated in cache. Cache size: " + lastSnapshotMap.size());
    }

    protected abstract CrudRepository getDiffRepository();

    protected Map<String, Snapshot> convertToMap(List<Snapshot> snapshotList) {
        Map<String, Snapshot> map = new HashMap<>(snapshotList.size());

        for (Snapshot snapshot : snapshotList) {
            map.put(snapshot.getMedia().getId(), snapshot);
        }

        return map;
    }

    protected List<Diff> calculateDiff(IgProfile profile, Map<String, Snapshot> lastSnapshotMap, List<Snapshot> snapshotList) {

        List<Diff> diffList = new ArrayList<>(snapshotList.size());

        for (Snapshot newSnapshot : snapshotList) {
            Snapshot lastSnapshot = lastSnapshotMap.get(newSnapshot.getMedia().getId());
            if (null == lastSnapshot) {
                if (isEligibleToCreateDiff(newSnapshot)) {
                    // New post
                    lastSnapshot = newSnapshotInstance(newSnapshot);
                    lastSnapshot.setLikes(0);
                    lastSnapshot.setComments(0);
                    lastSnapshot.setEngagement(0);
                    lastSnapshot.setSaved(0);
                    lastSnapshot.setImpressions(0);
                    lastSnapshot.setReach(0);
                    lastSnapshot.setVideoViews(0);
                    log.debug("No previous hourly snapshot found for media ID [" + newSnapshot.getMedia().getId() + "]. Diff will be calculated based on new snapshot. ");
                } else {
                    log.debug("No previous hourly snapshot found for media ID [" + newSnapshot.getMedia().getId() + "]. Diff calculation cancelled. ");
                    continue;
                }
            }

            Diff diff = newDiffInstance();
            diff.setIgProfile(profile);
            diff.setMediaType(newSnapshot.getMediaType());
            diff.setMedia(newSnapshot.getMedia());

            InsDataUser user = profile.getUser();
            diff.realizeCapturedAt(newSnapshot.getCapturedAt(), user.getZoneId(), user.getFirstDayOfWeekInstance());
            diff.setComparedTo(lastSnapshot.getCapturedAt());

            diff.setLikes(newSnapshot.getLikes() - lastSnapshot.getLikes());
            diff.setComments(newSnapshot.getComments() - lastSnapshot.getComments());
            diff.setEngagement(newSnapshot.getEngagement() - lastSnapshot.getEngagement());
            diff.setSaved(newSnapshot.getSaved() - lastSnapshot.getSaved());
            diff.setImpressions(newSnapshot.getImpressions() - lastSnapshot.getImpressions());
            diff.setReach(newSnapshot.getReach() - lastSnapshot.getReach());
            diff.setVideoViews(newSnapshot.getVideoViews() - lastSnapshot.getVideoViews());

            if (!diff.isChanged()) {
                // Media snapshot shows no change
                // replace the last snapshot time. Diff won't be generated
                lastSnapshot.realizeCapturedAt(newSnapshot.getCapturedAt(), user.getZoneId(), user.getFirstDayOfWeekInstance());

                // Put the updated lastSnapshot in new snapshot list so that it'll be updated when saving
                snapshotList.set(snapshotList.indexOf(newSnapshot), lastSnapshot);

                log.debug("No change found in the new snapshot of media [" + newSnapshot.getMedia().getId() + "]. Snapshot is refreshed to " + newSnapshot.getCapturedAt());
                continue;
            }

            diffList.add(diff);
        }

        return diffList;
    }

    protected abstract boolean isEligibleToCreateDiff(Snapshot newSnapshot);

    protected abstract Snapshot newSnapshotInstance(Snapshot reference);

    protected abstract Diff newDiffInstance();

    protected abstract Map<String, Snapshot> loadLastSnapshotFromPersistence(IgProfile profile);
}
