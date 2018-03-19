package net.windia.insdata.repository;

import net.windia.insdata.model.internal.IgProfile;
import net.windia.insdata.model.internal.IgProfileSnapshotHourly;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface IgProfileSnapshotHourlyRepository extends CrudRepository<IgProfileSnapshotHourly, Long> {

    IgProfileSnapshotHourly findFirstByIgProfileOrderByCapturedAtDesc(IgProfile igProfile);

    @Query("SELECT sh.capturedAt, sh.hour, sh.followers FROM IgProfileSnapshotHourly sh " +
            "WHERE sh.igProfile = :igProfile AND sh.capturedAt > :from AND sh.capturedAt < :to " +
            "ORDER BY sh.capturedAt ASC")
    List<IgProfileSnapshotHourly> findFollowersByDay(@Param("igProfile") IgProfile igProfile,
                                                     @Param("from") Date from,
                                                     @Param("to") Date to);

    List<IgProfileSnapshotHourly> findByIgProfileIdAndCapturedAtBetweenOrderByCapturedAtAsc(Long igProfileId, Date since, Date until);
}
