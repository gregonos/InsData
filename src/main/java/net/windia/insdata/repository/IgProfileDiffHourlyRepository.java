package net.windia.insdata.repository;

import net.windia.insdata.model.internal.IgProfileDiffHourly;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface IgProfileDiffHourlyRepository extends CrudRepository<IgProfileDiffHourly, Long> {
    List<IgProfileDiffHourly> findByIgProfileIdAndComparedToBetweenOrderByComparedToAsc(Long igProfileId, Date since, Date until);
}
