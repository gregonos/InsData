package net.windia.insdata.model.internal;

import java.time.OffsetDateTime;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class IgProfileStatImpl extends IgStatBase implements IgProfileStat {

    @Column(nullable = false)
    private OffsetDateTime capturedAt;

    @Column(nullable = false)
    private Integer mediaCount;

    @Column(nullable = false)
    private Integer followers;

    @Column(nullable = false)
    private Integer follows;

    @Column(nullable = false)
    private Integer newFollowers;

    @Column(nullable = false)
    private Integer impressions;

    @Column(nullable = false)
    private Integer reach;

    @Column(nullable = false)
    private Integer profileViews;

    @Column(nullable = false)
    private Integer emailContacts;

    @Column(nullable = false)
    private Integer phoneCallClicks;

    @Column(nullable = false)
    private Integer getDirectionsClicks;

    @Column(nullable = false)
    private Integer websiteClicks;

    public OffsetDateTime getCapturedAt() {
        return capturedAt;
    }

    public void setCapturedAt(OffsetDateTime capturedAt) {
        this.capturedAt = capturedAt;
    }

    public Integer getMediaCount() {
        return mediaCount;
    }

    public void setMediaCount(Integer mediaCount) {
        this.mediaCount = mediaCount;
    }

    public Integer getFollowers() {
        return followers;
    }

    public void setFollowers(Integer followers) {
        this.followers = followers;
    }

    public Integer getFollows() {
        return follows;
    }

    public void setFollows(Integer follows) {
        this.follows = follows;
    }

    public Integer getNewFollowers() {
        return newFollowers;
    }

    public void setNewFollowers(Integer newFollowers) {
        this.newFollowers = newFollowers;
    }

    public Integer getImpressions() {
        return impressions;
    }

    public void setImpressions(Integer impressions) {
        this.impressions = impressions;
    }

    public Integer getReach() {
        return reach;
    }

    public void setReach(Integer reach) {
        this.reach = reach;
    }

    public Integer getProfileViews() {
        return profileViews;
    }

    public void setProfileViews(Integer profileViews) {
        this.profileViews = profileViews;
    }

    public Integer getEmailContacts() {
        return emailContacts;
    }

    public void setEmailContacts(Integer emailContacts) {
        this.emailContacts = emailContacts;
    }

    public Integer getPhoneCallClicks() {
        return phoneCallClicks;
    }

    public void setPhoneCallClicks(Integer phoneCallClicks) {
        this.phoneCallClicks = phoneCallClicks;
    }

    public Integer getGetDirectionsClicks() {
        return getDirectionsClicks;
    }

    public void setGetDirectionsClicks(Integer getDirectionsClicks) {
        this.getDirectionsClicks = getDirectionsClicks;
    }

    public Integer getWebsiteClicks() {
        return websiteClicks;
    }

    public void setWebsiteClicks(Integer websiteClicks) {
        this.websiteClicks = websiteClicks;
    }
}
