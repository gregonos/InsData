package net.windia.insdata.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfilesDTO implements Serializable {

    private List<ProfileDTO> profiles;

    public ProfilesDTO(List<ProfileDTO> profiles) {
        this.profiles = profiles;
    }
}
