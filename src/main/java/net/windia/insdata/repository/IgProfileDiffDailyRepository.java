package net.windia.insdata.repository;

import net.windia.insdata.model.internal.IgProfileDiffDaily;
import org.springframework.data.repository.CrudRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface IgProfileDiffDailyRepository extends CrudRepository<IgProfileDiffDaily, Long> {
    List<IgProfileDiffDaily> findByIgProfileIdAndCapturedAtBetweenOrderByCapturedAtAsc(Long igProfileId,
                                                                                       OffsetDateTime since,
                                                                                       OffsetDateTime until);
}
