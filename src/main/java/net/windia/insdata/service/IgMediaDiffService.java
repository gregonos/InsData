package net.windia.insdata.service;

import lombok.extern.slf4j.Slf4j;
import net.windia.insdata.model.internal.IgMediaDiff;
import net.windia.insdata.model.internal.IgMediaSnapshot;
import net.windia.insdata.model.internal.IgProfile;
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

        return null == lastSnapshot ? loadLastSnapshotFromPersistence(profile) : lastSnapshot;
    }

    public void calculateAndSaveDiff(IgProfile profile, List<Snapshot> snapshotList) {
        Map<String, Snapshot> lastSnapshot = lastSnapshot(profile);

        if (null == lastSnapshot) {
            log.info("Last snapshot not found, diff calculation cancelled. ");
            return;
        } else {
            Snapshot sampleSnapshot = snapshotList.get(0);
            Snapshot sampleCache = lastSnapshot.values().iterator().next();

            if (sampleCache.getCapturedAt().after(sampleSnapshot.getCapturedAt())) {
                log.info("A newer snapshot has been found, diff calculation cancelled. ");
                return;
            }
        }

        List<Diff> diffList = calculateDiff(lastSnapshot, snapshotList);

        getDiffRepository().saveAll(diffList);

        Map<String, Snapshot> newSnapshotMap = convertToMap(snapshotList);
        lastSnapshotCache.put(profile.getId(), newSnapshotMap);
    }

    protected abstract CrudRepository getDiffRepository();

    protected Map<String, Snapshot> convertToMap(List<Snapshot> snapshotList) {
        Map<String, Snapshot> map = new HashMap<>(snapshotList.size());

        for (Snapshot snapshot : snapshotList) {
            map.put(snapshot.getMedia().getId(), snapshot);
        }

        return map;
    }

    protected List<Diff> calculateDiff(Map<String, Snapshot> lastSnapshotMap, List<Snapshot> snapshotList) {

        List<Diff> diffList = new ArrayList<>(snapshotList.size());

        for (Snapshot newSnapshot : snapshotList) {
            Snapshot lastSnapshot = lastSnapshotMap.get(newSnapshot.getMedia().getId());
            if (null == lastSnapshot) {
                log.debug("No previous hourly snapshot found for media ID [" + newSnapshot.getMedia().getId() + "]. Diff calculation cancelled. ");
                continue;
            }

            Diff diff = newDiffInstance();
            diff.setMediaType(newSnapshot.getMediaType());
            diff.setMedia(newSnapshot.getMedia());
            diff.setComparedTo(lastSnapshot.getCapturedAt());

            diff.setLikes(newSnapshot.getLikes() - lastSnapshot.getLikes());
            diff.setComments(newSnapshot.getComments() - lastSnapshot.getComments());
            diff.setEngagement(newSnapshot.getEngagement() - lastSnapshot.getEngagement());
            diff.setSaved(newSnapshot.getSaved() - lastSnapshot.getSaved());
            diff.setImpressions(newSnapshot.getImpressions() - lastSnapshot.getImpressions());
            diff.setReach(newSnapshot.getReach() - lastSnapshot.getReach());
            diff.setVideoViews(newSnapshot.getVideoViews() - lastSnapshot.getVideoViews());

            calculateDiffExtra(diff, lastSnapshot, newSnapshot);

            diffList.add(diff);
        }

        return diffList;
    }

    protected abstract Diff newDiffInstance();

    protected abstract void calculateDiffExtra(Diff diff, Snapshot lastSnapshot, Snapshot newSnapshot);

    protected abstract Map<String, Snapshot> loadLastSnapshotFromPersistence(IgProfile profile);
}
