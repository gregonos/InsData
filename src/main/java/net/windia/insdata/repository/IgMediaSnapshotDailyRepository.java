package net.windia.insdata.repository;

import net.windia.insdata.model.internal.IgMediaSnapshotDaily;
import net.windia.insdata.model.internal.IgProfile;
import org.springframework.data.repository.CrudRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface IgMediaSnapshotDailyRepository extends CrudRepository<IgMediaSnapshotDaily, Long> {

    IgMediaSnapshotDaily findFirstByIgProfileOrderByCapturedAtDesc(IgProfile profile);

    List<IgMediaSnapshotDaily> findByIgProfileAndCapturedAt(IgProfile profile, OffsetDateTime capturedAt);
}
