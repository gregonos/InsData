package net.windia.insdata.repository;

import net.windia.insdata.model.internal.IgProfile;
import net.windia.insdata.model.internal.IgProfileSnapshotDaily;
import org.springframework.data.repository.CrudRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface IgProfileSnapshotDailyRepository extends CrudRepository<IgProfileSnapshotDaily, Long> {

    IgProfileSnapshotDaily findFirstByIgProfileOrderByCapturedAtDesc(IgProfile igProfile);

    List<IgProfileSnapshotDaily> findByIgProfileIdAndCapturedAtBetweenOrderByCapturedAtAsc(Long igProfileId, OffsetDateTime since, OffsetDateTime until);
}
