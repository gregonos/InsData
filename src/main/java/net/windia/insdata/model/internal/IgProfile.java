package net.windia.insdata.model.internal;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;


@Entity
@Table
@DiscriminatorValue("instagram")
public class IgProfile extends Profile {

    @Column
    private String igId;

    @Column
    private String businessAccountId;

    @Column
    private String biography;

    @Column
    private String username;

    @Column
    private String name;

    @Column
    private String pictureUrl;

    @Column
    private String website;

    public String getIgId() {
        return igId;
    }

    public void setIgId(String igId) {
        this.igId = igId;
    }

    public String getBusinessAccountId() {
        return businessAccountId;
    }

    public void setBusinessAccountId(String businessAccountId) {
        this.businessAccountId = businessAccountId;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    @Override
    public String toString() {
        return "IgProfile{" +
                "token='" + getToken() + '\'' +
                "businessAccountId='" + businessAccountId + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
