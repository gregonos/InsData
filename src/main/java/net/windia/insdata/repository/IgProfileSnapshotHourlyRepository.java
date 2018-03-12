package net.windia.insdata.repository;

import net.windia.insdata.model.internal.IgProfile;
import net.windia.insdata.model.internal.IgProfileSnapshotHourly;
import org.springframework.data.repository.CrudRepository;

public interface IgProfileSnapshotHourlyRepository extends CrudRepository<IgProfileSnapshotHourly, Long> {

    IgProfileSnapshotHourly findFirstByIgProfileOrderByCapturedAtDesc(IgProfile igProfile);
}
