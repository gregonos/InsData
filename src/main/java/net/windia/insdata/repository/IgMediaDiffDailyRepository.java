package net.windia.insdata.repository;

import net.windia.insdata.model.internal.IgMediaDiffDaily;
import org.springframework.data.repository.CrudRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface IgMediaDiffDailyRepository extends CrudRepository<IgMediaDiffDaily, Long> {

    List<IgMediaDiffDaily> findByIgProfileIdAndCapturedAtBetweenOrderByCapturedAtAsc(Long igProfileId, OffsetDateTime since, OffsetDateTime until);
}
