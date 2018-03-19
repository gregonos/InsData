package net.windia.insdata.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileDTO implements Serializable {

    private Long id;

    private String type;

    private Date createdAt;

    private Date lastUpdatedAt;

    private IgProfileDTO igProfile;
}
