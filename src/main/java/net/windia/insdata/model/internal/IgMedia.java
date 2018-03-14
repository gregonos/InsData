package net.windia.insdata.model.internal;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table
public class IgMedia {

    @Id
    private String id;

    @ManyToOne
    private IgProfile igProfile;

    @Column(nullable = false)
    private String igId;

    @Column
    private String caption;

    @Column(nullable = false)
    private String type;

    @Column
    private String permalink;

    @Column(nullable = false)
    private String shortcode;

    @Column
    private String thumbnailUrl;

    @Column
    private String url;

    @Column(nullable = false)
    private Date createdAt;

    public IgMedia() {}

    public IgMedia(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public IgProfile getIgProfile() {
        return igProfile;
    }

    public void setIgProfile(IgProfile igProfile) {
        this.igProfile = igProfile;
    }

    public String getIgId() {
        return igId;
    }

    public void setIgId(String igId) {
        this.igId = igId;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public String getShortcode() {
        return shortcode;
    }

    public void setShortcode(String shortcode) {
        this.shortcode = shortcode;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
