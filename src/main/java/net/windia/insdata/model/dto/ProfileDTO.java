package net.windia.insdata.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileDTO implements Serializable {

    private Long id;

    private String type;

    private ZonedDateTime createdAt;

    private ZonedDateTime lastUpdatedAt;

    private IgProfileDTO igProfile;
}
