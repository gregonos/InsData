package net.windia.insdata.repository;

import net.windia.insdata.model.internal.IgMediaDiffDaily;
import net.windia.insdata.model.internal.IgMediaDiffHourly;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface IgMediaDiffHourlyRepository extends CrudRepository<IgMediaDiffHourly, Long> {
    List<IgMediaDiffDaily> findByIgProfileIdAndComparedToBetweenOrderByComparedToAsc(Long igProfileId, Date since, Date until);
}
