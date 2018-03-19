package net.windia.insdata.service;

import net.windia.insdata.model.internal.InsDataUser;
import net.windia.insdata.repository.InsDataUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private InsDataUserRepository userRepo;

    public InsDataUser getUser(String email) {
        return userRepo.findByEmail(email);
    }
}
