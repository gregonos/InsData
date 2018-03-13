package net.windia.insdata.service;

import lombok.extern.slf4j.Slf4j;
import net.windia.insdata.model.internal.IgDiff;
import net.windia.insdata.model.internal.IgSnapshot;
import org.springframework.data.repository.CrudRepository;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class IgStatDiffService<Diff extends IgDiff, Snapshot extends IgSnapshot, K> {

    private Map<String, Snapshot> lastSnapshotCache;

    @PostConstruct
    public void init() {
        lastSnapshotCache = new HashMap<>();
    }

    public abstract K getStatKey(Snapshot newSnapshot);

    public abstract String getCacheKey(K key);

    public Snapshot lastSnapshot(K key) {

        if (null == lastSnapshotCache) {
            return null;
        }

        Snapshot cached = lastSnapshotCache.get(getCacheKey(key));

        return null == cached ? loadLastSnapshotFromPersistence(key) : cached;
    }

    private void replace(Snapshot snapshot) {
        lastSnapshotCache.put(getCacheKey(getStatKey(snapshot)), snapshot);
    }

    public Diff calculateAndSaveDiff(Snapshot newSnapshot, Date sinceTime) {

        Snapshot lastSnapshot = lastSnapshot(getStatKey(newSnapshot));
        if (lastSnapshot == null) {
            log.info("No previous snapshot found. Diff calculation is cancelled.");
            return null;
        } else if (newSnapshot.getCapturedAt().before(lastSnapshot.getCapturedAt())) {
            log.warn("A newer snapshot already exists. Diff calculation is cancelled.");
            return null;
        }

        Diff diff = calculateDiff(lastSnapshot, newSnapshot, sinceTime);

        log.debug("Daily Diff generated. " + diff);

        getDiffRepository().save(diff);

        replace(newSnapshot);

        return diff;
    }

    public abstract Diff calculateDiff(Snapshot lastSnapshot, Snapshot newSnapshot, Date sinceTime);

    public abstract Snapshot loadLastSnapshotFromPersistence(K key);

    public abstract CrudRepository getDiffRepository();
}
