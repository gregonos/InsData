package net.windia.insdata.repository;

import net.windia.insdata.model.internal.IgProfile;
import net.windia.insdata.model.internal.IgProfileSnapshotDaily;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface IgProfileSnapshotDailyRepository extends CrudRepository<IgProfileSnapshotDaily, Long> {

    IgProfileSnapshotDaily findFirstByIgProfileOrderByCapturedAtDesc(IgProfile igProfile);

    List<IgProfileSnapshotDaily> findByIgProfileIdAndCapturedAtBetweenOrderByCapturedAtAsc(Long igProfileId, Date since, Date until);
}
