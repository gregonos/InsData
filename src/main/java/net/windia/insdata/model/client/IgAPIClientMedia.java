package net.windia.insdata.model.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Data
public class IgAPIClientMedia implements Serializable {

    private String id;

    @JsonProperty("ig_id")
    private String igId;

    private String caption;

    @JsonProperty("media_type")
    private String mediaType;

    @JsonProperty("media_url")
    private String mediaUrl;

    @JsonProperty("thumbnail_url")
    private String thumbnailUrl;

    private String permalink;

    private String shortcode;

    private OffsetDateTime timestamp;

    @JsonProperty("like_count")
    private Integer likeCount;

    @JsonProperty("comments_count")
    private Integer commentsCount;

    private IgAPIClientDataWrapper<IgAPIClientInsight<Integer>> insights;
}
