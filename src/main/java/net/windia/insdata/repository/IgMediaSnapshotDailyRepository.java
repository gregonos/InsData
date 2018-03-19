package net.windia.insdata.repository;

import net.windia.insdata.model.internal.IgMediaSnapshotDaily;
import net.windia.insdata.model.internal.IgProfile;
import net.windia.insdata.model.internal.IgProfileSnapshotHourly;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface IgMediaSnapshotDailyRepository extends CrudRepository<IgMediaSnapshotDaily, Long> {

    IgMediaSnapshotDaily findFirstByIgProfileOrderByCapturedAtDesc(IgProfile profile);

    List<IgMediaSnapshotDaily> findByIgProfileAndCapturedAt(IgProfile profile, Date capturedAt);
}
