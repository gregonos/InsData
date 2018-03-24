package net.windia.insdata.repository;

import net.windia.insdata.model.internal.IgMedia;
import net.windia.insdata.model.internal.IgProfile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IgMediaRepository extends CrudRepository<IgMedia, String> {

    @Query("SELECT new IgMedia(m.id, m.createdAt) FROM IgMedia m WHERE m.igProfile = :igProfile")
    List<IgMedia> findIdByIgProfile(@Param("igProfile")IgProfile igProfile);
}
