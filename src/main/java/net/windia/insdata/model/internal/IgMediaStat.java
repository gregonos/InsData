package net.windia.insdata.model.internal;

public interface IgMediaStat {

    IgProfile getIgProfile();

    void setIgProfile(IgProfile igProfile);

    IgMedia getMedia();

    void setMedia(IgMedia media);

    Integer getImpressions();

    void setImpressions(Integer impressions);

    Integer getReach();

    void setReach(Integer reach);

    Integer getEngagement();

    void setEngagement(Integer engagement);

    Integer getSaved();

    void setSaved(Integer saved);

    Integer getVideoViews();

    void setVideoViews(Integer videoViews);

    Integer getLikes();

    void setLikes(Integer likes);

    Integer getComments();

    void setComments(Integer comments);

    String getMediaType();

    void setMediaType(String mediaType);
}
