package net.windia.insdata.model.internal;

public interface IgProfileStat extends IgStat {

    IgProfile getIgProfile();

    void setIgProfile(IgProfile igProfile);

    Integer getMediaCount();

    void setMediaCount(Integer mediaCount);

    Integer getFollowers();

    void setFollowers(Integer followers);

    Integer getFollows();

    void setFollows(Integer follows);

    Integer getNewFollowers();

    void setNewFollowers(Integer newFollowers);

    Integer getImpressions();

    void setImpressions(Integer impressions);

    Integer getReach();

    void setReach(Integer reach);

    Integer getProfileViews();

    void setProfileViews(Integer profileViews);

    Integer getEmailContacts();

    void setEmailContacts(Integer emailContacts);

    Integer getPhoneCallClicks();

    void setPhoneCallClicks(Integer phoneCallClicks);

    Integer getGetDirectionsClicks();

    void setGetDirectionsClicks(Integer getDirectionsClicks);

    Integer getWebsiteClicks();

    void setWebsiteClicks(Integer websiteClicks);
}
