package net.windia.insdata.repository;

import net.windia.insdata.model.internal.IgMediaDiffHourly;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface IgMediaDiffHourlyRepository extends CrudRepository<IgMediaDiffHourly, Long> {
    List<IgMediaDiffHourly> findByIgProfileIdAndCapturedAtBetweenOrderByCapturedAtAsc(Long igProfileId, Date since, Date until);
}
