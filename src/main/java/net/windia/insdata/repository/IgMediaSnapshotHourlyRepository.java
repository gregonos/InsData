package net.windia.insdata.repository;

import net.windia.insdata.model.internal.IgMediaSnapshotHourly;
import net.windia.insdata.model.internal.IgProfile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface IgMediaSnapshotHourlyRepository extends CrudRepository<IgMediaSnapshotHourly, Long> {

    IgMediaSnapshotHourly findFirstByIgProfileOrderByCapturedAtDesc(@Param("igProfile")IgProfile igProfile);

    List<IgMediaSnapshotHourly> findByIgProfileAndCapturedAt(IgProfile igProfile, OffsetDateTime capturedAt);

}
