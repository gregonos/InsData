package net.windia.insdata.service.diffcalc;

import net.windia.insdata.model.internal.IgProfileBasicStat;
import net.windia.insdata.model.internal.IgProfileSnapshotDaily;
import net.windia.insdata.model.internal.IgProfileSnapshotHourly;
import net.windia.insdata.model.internal.IgSnapshot;
import net.windia.insdata.util.DateTimeUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service(value = "igProfileDiffCalculator")
public class IgProfileDiffCalculator<StatType extends IgProfileBasicStat> implements IgDiffCalculator<StatType> {

    @Override
    public void calculate(IgProfileBasicStat diff, IgProfileBasicStat lastSnapshot, IgProfileBasicStat newSnapshot, Date sinceTime) {
        diff.setMediaCount(newSnapshot.getMediaCount() - lastSnapshot.getMediaCount());
        diff.setFollowers(newSnapshot.getFollowers() - lastSnapshot.getFollowers());
        diff.setFollows(newSnapshot.getFollows() - lastSnapshot.getFollows());

        if (null == sinceTime) {
            diff.setNewFollowers(0);
            diff.setImpressions(0);
            diff.setReach(0);
            diff.setProfileViews(0);
            diff.setEmailContacts(0);
            diff.setPhoneCallClicks(0);
            diff.setGetDirectionsClicks(0);
            diff.setWebsiteClicks(0);
        } else if (newSnapshot instanceof IgProfileSnapshotHourly &&
                DateTimeUtils.passedWithinOneHour(sinceTime, ((IgSnapshot)lastSnapshot).getCapturedAt())) {
            diff.setNewFollowers(newSnapshot.getNewFollowers());
            diff.setImpressions(newSnapshot.getImpressions());
            diff.setReach(newSnapshot.getReach());
            diff.setProfileViews(newSnapshot.getProfileViews());
            diff.setEmailContacts(newSnapshot.getEmailContacts());
            diff.setPhoneCallClicks(newSnapshot.getPhoneCallClicks());
            diff.setGetDirectionsClicks(newSnapshot.getGetDirectionsClicks());
            diff.setWebsiteClicks(newSnapshot.getWebsiteClicks());
        } else {
            diff.setNewFollowers(newSnapshot.getNewFollowers() - lastSnapshot.getNewFollowers());
            diff.setImpressions(newSnapshot.getImpressions() - lastSnapshot.getImpressions());
            diff.setReach(newSnapshot.getReach() - lastSnapshot.getReach());
            diff.setProfileViews(newSnapshot.getProfileViews() - lastSnapshot.getProfileViews());
            diff.setEmailContacts(newSnapshot.getEmailContacts() - lastSnapshot.getEmailContacts());
            diff.setPhoneCallClicks(newSnapshot.getPhoneCallClicks() - lastSnapshot.getPhoneCallClicks());
            diff.setGetDirectionsClicks(newSnapshot.getGetDirectionsClicks() - lastSnapshot.getGetDirectionsClicks());
            diff.setWebsiteClicks(newSnapshot.getWebsiteClicks() - lastSnapshot.getWebsiteClicks());
        }
    }
}
