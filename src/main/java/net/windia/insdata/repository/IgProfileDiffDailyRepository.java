package net.windia.insdata.repository;

import net.windia.insdata.model.internal.IgProfileDiffDaily;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface IgProfileDiffDailyRepository extends CrudRepository<IgProfileDiffDaily, Long> {
    List<IgProfileDiffDaily> findByIgProfileIdAndCapturedAtBetweenOrderByCapturedAtAsc(Long igProfileId, Date since, Date until);
}
