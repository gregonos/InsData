package net.windia.insdata.model.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class IgAPIClientMedia implements Serializable {

    private String id;

    private String igId;

    private String caption;

    @JsonProperty("media_type")
    private String mediaType;

    private String mediaUrl;

    private String permalink;

    private String shortcode;

    private Date timestamp;

    @JsonProperty("like_count")
    private Integer likeCount;

    @JsonProperty("comments_count")
    private Integer commentsCount;

    private IgAPIClientDataWrapper<IgAPIClientInsight<Integer>> insights;
}
