package net.windia.insdata.model.internal;

import javax.persistence.*;

@MappedSuperclass
public abstract class IgMediaStat extends IgProfileStat {

    @ManyToOne
    private IgMedia media;

    @Column(nullable = false)
    private Integer impressions;

    @Column(nullable = false)
    private Integer reach;

    @Column(nullable = false)
    private Integer engagement;

    @Column(nullable = false)
    private Integer saved;

    @Column(nullable = false)
    private Integer videoViews;

    @Column(nullable = false)
    private Integer likes;

    @Column(nullable = false)
    private Integer comments;

    @Column(nullable = false)
    private String mediaType;

    public IgMedia getMedia() {
        return media;
    }

    public void setMedia(IgMedia media) {
        this.media = media;
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

    public Integer getEngagement() {
        return engagement;
    }

    public void setEngagement(Integer engagement) {
        this.engagement = engagement;
    }

    public Integer getSaved() {
        return saved;
    }

    public void setSaved(Integer saved) {
        this.saved = saved;
    }

    public Integer getVideoViews() {
        return videoViews;
    }

    public void setVideoViews(Integer videoViews) {
        this.videoViews = videoViews;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Integer getComments() {
        return comments;
    }

    public void setComments(Integer comments) {
        this.comments = comments;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }
}
