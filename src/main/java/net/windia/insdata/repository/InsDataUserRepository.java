package net.windia.insdata.repository;

import net.windia.insdata.model.internal.InsDataUser;
import org.springframework.data.repository.CrudRepository;

public interface InsDataUserRepository extends CrudRepository<InsDataUser, Long> {

    InsDataUser findByEmail(String email);
}
