package net.windia.insdata.repository;

import net.windia.insdata.model.internal.IgMediaDiffDaily;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface IgMediaDiffDailyRepository extends CrudRepository<IgMediaDiffDaily, Long> {

    List<IgMediaDiffDaily> findByIgProfileIdAndComparedToBetweenOrderByComparedToAsc(Long igProfileId, Date since, Date until);
}
