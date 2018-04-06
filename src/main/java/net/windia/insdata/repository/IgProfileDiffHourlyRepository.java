package net.windia.insdata.repository;

import net.windia.insdata.model.internal.IgProfileDiffHourly;
import org.springframework.data.repository.CrudRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface IgProfileDiffHourlyRepository extends CrudRepository<IgProfileDiffHourly, Long> {
    List<IgProfileDiffHourly> findByIgProfileIdAndCapturedAtBetweenOrderByCapturedAtAsc(Long igProfileId,
                                                                                        OffsetDateTime since,
                                                                                        OffsetDateTime until);
}
