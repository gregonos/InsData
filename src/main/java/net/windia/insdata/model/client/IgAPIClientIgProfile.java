package net.windia.insdata.model.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class IgAPIClientIgProfile implements Serializable {

    private String id;
    private String igId;
    private String username;
    private String name;
    private String biography;

    @JsonProperty("profile_picture_url")
    private String profilePictureUrl;

    @JsonProperty("media_count")
    private Integer mediaCount;

    @JsonProperty("followers_count")
    private Integer followersCount;

    @JsonProperty("follows_count")
    private Integer followsCount;

    private IgAPIClientDataWrapper<IgAPIClientInsight<Integer>> insights;
}
