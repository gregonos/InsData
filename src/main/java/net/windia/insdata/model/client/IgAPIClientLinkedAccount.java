package net.windia.insdata.model.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class IgAPIClientLinkedAccount implements Serializable {

    @JsonProperty("access_token")
    private String accessToken;
    private String category;
    private String name;
    private String id;
    private String[] perms;

    @JsonProperty("instagram_business_account")
    private IgAPIClientIgBusinessAccount instagramBusinessAccount;

}
