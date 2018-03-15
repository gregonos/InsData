package net.windia.insdata.service;

import lombok.extern.slf4j.Slf4j;
import net.windia.insdata.model.internal.IgProfile;
import net.windia.insdata.model.internal.IgProfileDiff;
import net.windia.insdata.model.internal.IgProfileSnapshot;
import net.windia.insdata.model.internal.IgProfileSnapshotHourly;
import net.windia.insdata.util.DateTimeUtils;
import org.springframework.data.repository.CrudRepository;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;

@Slf4j
public abstract class IgProfileDiffService<Diff extends IgProfileDiff, Snapshot extends IgProfileSnapshot> {

    private Map<Long, Snapshot> lastSnapshotCache;

    @PostConstruct
    public void init() {
        lastSnapshotCache = new HashMap<>();
    }

    public Snapshot lastSnapshot(IgProfile profile) {

        if (null == lastSnapshotCache) {
            return null;
        }

        Snapshot cached = lastSnapshotCache.get(profile.getId());

        return null == cached ? loadLastSnapshotFromPersistence(profile) : cached;
    }

    public Diff calculateAndSaveDiff(Snapshot newSnapshot) {

        Snapshot lastSnapshot = lastSnapshot(newSnapshot.getIgProfile());
        if (lastSnapshot == null) {
            log.info("No previous snapshot found. Diff calculation is cancelled.");
            return null;
        } else if (newSnapshot.getCapturedAt().before(lastSnapshot.getCapturedAt())) {
            log.warn("A newer snapshot already exists. Diff calculation is cancelled.");
            return null;
        }

        Diff diff = calculateDiff(lastSnapshot, newSnapshot);

        log.debug("Diff generated. " + diff);

        getDiffRepository().save(diff);

        lastSnapshotCache.put(newSnapshot.getIgProfile().getId(), newSnapshot);

        return diff;
    }

    private Diff calculateDiff(Snapshot lastSnapshot, Snapshot newSnapshot) {
        if (1 == DateTimeUtils.hourOfFacebookServer() && newSnapshot instanceof IgProfileSnapshotHourly) {
            lastSnapshot.setNewFollowers(0);
            lastSnapshot.setImpressions(0);
            lastSnapshot.setReach(0);
            lastSnapshot.setProfileViews(0);
            lastSnapshot.setWebsiteClicks(0);
            lastSnapshot.setGetDirectionsClicks(0);
            lastSnapshot.setPhoneCallClicks(0);
            lastSnapshot.setEmailContacts(0);
        }

        Diff diff = newDiffInstance();
        diff.setIgProfile(newSnapshot.getIgProfile());
        diff.realizeComparedTo(lastSnapshot.getCapturedAt(), newSnapshot.getIgProfile().getUser().getTimeZone());

        diff.setMediaCount(newSnapshot.getMediaCount() - lastSnapshot.getMediaCount());
        diff.setFollowers(newSnapshot.getFollowers() - lastSnapshot.getFollowers());
        diff.setFollows(newSnapshot.getFollows() - lastSnapshot.getFollows());

        diff.setNewFollowers(newSnapshot.getNewFollowers() - lastSnapshot.getNewFollowers());
        diff.setImpressions(newSnapshot.getImpressions() - lastSnapshot.getImpressions());
        diff.setReach(newSnapshot.getReach() - lastSnapshot.getReach());
        diff.setProfileViews(newSnapshot.getProfileViews() - lastSnapshot.getProfileViews());
        diff.setEmailContacts(newSnapshot.getEmailContacts() - lastSnapshot.getEmailContacts());
        diff.setPhoneCallClicks(newSnapshot.getPhoneCallClicks() - lastSnapshot.getPhoneCallClicks());
        diff.setGetDirectionsClicks(newSnapshot.getGetDirectionsClicks() - lastSnapshot.getGetDirectionsClicks());
        diff.setWebsiteClicks(newSnapshot.getWebsiteClicks() - lastSnapshot.getWebsiteClicks());

        return diff;
    }

    protected abstract Diff newDiffInstance();

    public abstract Snapshot loadLastSnapshotFromPersistence(IgProfile profile);

    public abstract CrudRepository getDiffRepository();
}
