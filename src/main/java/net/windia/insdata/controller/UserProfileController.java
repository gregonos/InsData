package net.windia.insdata.controller;

import lombok.extern.slf4j.Slf4j;
import net.windia.insdata.constants.InsDataConstants;
import net.windia.insdata.model.dto.ProfileDTO;
import net.windia.insdata.model.dto.ProfilesDTO;
import net.windia.insdata.model.internal.Profile;
import net.windia.insdata.model.mapper.ProfileDTOMapper;
import net.windia.insdata.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/api/users", produces = {InsDataConstants.SERVER_CONTENT_TYPE_JSON_UTF8})
@ResponseBody
public class UserProfileController {

    private static final String USER_ID_SELF = "me";

    @Autowired
    private ProfileService profileService;

    @Autowired
    private ProfileDTOMapper profileDTOMapper;

    @RequestMapping("/{userId}/profiles")
    public ProfilesDTO getProfies(@PathVariable("userId") String userId) {
        if (USER_ID_SELF.equalsIgnoreCase(userId)) {

            // TODO: Get user from session
            userId = "gregorysong@gmail.com";
            List<Profile> profiles = profileService.getProfiles(userId);

            return new ProfilesDTO(profileDTOMapper.map(profiles));
        }

        return null;
    }
}
