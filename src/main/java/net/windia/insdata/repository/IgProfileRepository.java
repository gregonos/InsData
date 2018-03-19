package net.windia.insdata.repository;

import net.windia.insdata.model.internal.IgProfile;
import net.windia.insdata.model.internal.InsDataUser;
import net.windia.insdata.model.internal.Profile;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface IgProfileRepository extends CrudRepository<IgProfile, Long> {
    List<Profile> findByUser(InsDataUser user);
}
