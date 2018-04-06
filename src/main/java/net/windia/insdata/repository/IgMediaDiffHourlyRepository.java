package net.windia.insdata.repository;

import net.windia.insdata.model.internal.IgMediaDiffHourly;
import org.springframework.data.repository.CrudRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface IgMediaDiffHourlyRepository extends CrudRepository<IgMediaDiffHourly, Long> {
    List<IgMediaDiffHourly> findByIgProfileIdAndCapturedAtBetweenOrderByCapturedAtAsc(Long igProfileId, OffsetDateTime since, OffsetDateTime until);
}
