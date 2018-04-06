package net.windia.insdata.repository;

import net.windia.insdata.model.internal.IgProfile;
import net.windia.insdata.model.internal.IgProfileSnapshotHourly;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface IgProfileSnapshotHourlyRepository extends CrudRepository<IgProfileSnapshotHourly, Long> {

    IgProfileSnapshotHourly findFirstByIgProfileOrderByCapturedAtDesc(IgProfile igProfile);

    @Query("SELECT sh FROM IgProfileSnapshotHourly sh " +
            "WHERE sh.igProfile = :igProfile AND sh.capturedAt > :from AND sh.capturedAt < :to " +
            "ORDER BY sh.capturedAt ASC")
    List<IgProfileSnapshotHourly> findFollowersByDay(@Param("igProfile") IgProfile igProfile,
                                                     @Param("from") OffsetDateTime from,
                                                     @Param("to") OffsetDateTime to);

    List<IgProfileSnapshotHourly> findByIgProfileIdAndCapturedAtBetweenOrderByCapturedAtAsc(Long igProfileId, OffsetDateTime since, OffsetDateTime until);
}
