package net.windia.insdata.repository;

import net.windia.insdata.model.internal.IgProfile;
import net.windia.insdata.model.internal.IgProfileAudienceDaily;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface IgProfileAudienceDailyRepository extends CrudRepository<IgProfileAudienceDaily, Long> {

    IgProfileAudienceDaily findFirstByIgProfileOrderByCapturedAtDesc(IgProfile igProfile);

    List<IgProfileAudienceDaily> findByIgProfileAndCapturedAtOrderByType(IgProfile igProfile, Date capturedAt);

}
