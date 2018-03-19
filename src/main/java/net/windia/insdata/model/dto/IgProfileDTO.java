package net.windia.insdata.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IgProfileDTO implements Serializable {
    private Long id;

    private String businessAccountId;

    private String igId;

    private String username;

    private String name;

    private String biography;

    private String pictureUrl;

    private String website;
}
