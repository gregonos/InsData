package net.windia.insdata.model.mapper;

import net.windia.insdata.model.dto.IgProfileDTO;
import net.windia.insdata.model.dto.ProfileDTO;
import net.windia.insdata.model.internal.IgProfile;
import net.windia.insdata.model.internal.Profile;
import org.springframework.stereotype.Service;

@Service
public class ProfileDTOMapper extends ResourceMapper<Profile, ProfileDTO> {

    @Override
    public ProfileDTO map(Profile source) {

        ProfileDTO target = new ProfileDTO();

        target.setId(source.getId());
        target.setType(source.getType());
        target.setCreatedAt(source.getCreatedAt());
        target.setLastUpdatedAt(source.getLastUpdatedAt());

        if (source instanceof IgProfile) {
            IgProfileDTO igProfileDTO = new IgProfileDTO();

            igProfileDTO.setId(source.getId());
            IgProfile srcAsIg = (IgProfile) source;
            igProfileDTO.setBusinessAccountId(srcAsIg.getBusinessAccountId());
            igProfileDTO.setIgId(srcAsIg.getIgId());
            igProfileDTO.setUsername(srcAsIg.getUsername());
            igProfileDTO.setName(srcAsIg.getName());
            igProfileDTO.setBiography(srcAsIg.getBiography());
            igProfileDTO.setPictureUrl(srcAsIg.getPictureUrl());
            igProfileDTO.setWebsite(srcAsIg.getWebsite());

            target.setIgProfile(igProfileDTO);
        }

        return target;
    }
}
