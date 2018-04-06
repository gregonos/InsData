package net.windia.insdata.service;

import net.windia.insdata.model.internal.IgProfile;
import net.windia.insdata.repository.IgProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IgProfileService {

    @Autowired
    private IgProfileRepository igProfileRepo;

    public IgProfile getIgProfile(Long profileId) {
        return igProfileRepo.findById(profileId).get();
    }
}
