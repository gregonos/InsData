package net.windia.insdata.service;

import net.windia.insdata.model.internal.InsDataUser;
import net.windia.insdata.model.internal.Profile;
import net.windia.insdata.repository.IgProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfileService {

    @Autowired
    private UserService userService;

    @Autowired
    private IgProfileRepository profileRepo;

    public List<Profile> getProfiles(String ownerEmail) {
        InsDataUser user = userService.getUser(ownerEmail);

        return profileRepo.findByUser(user);
    }
}
