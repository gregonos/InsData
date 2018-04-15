package net.windia.insdata.repository;

import net.windia.insdata.model.internal.IgProfile;
import net.windia.insdata.model.internal.IgProfileAudienceDaily;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface IgProfileAudienceDailyRepository extends CrudRepository<IgProfileAudienceDaily, Long> {

    IgProfileAudienceDaily findFirstByIgProfileOrderByCapturedAtDesc(IgProfile igProfile);

    List<IgProfileAudienceDaily> findByIgProfileAndCapturedAtOrderByType(IgProfile igProfile, OffsetDateTime capturedAt);

    @Query("SELECT new IgProfileAudienceDaily(au.capturedAt, au.type) FROM IgProfileAudienceDaily au " +
            "WHERE au.igProfile = :igProfile AND au.type = :type " +
            "GROUP BY au.capturedAt HAVING SUM(au.diff) > 0 ORDER BY au.capturedAt DESC")
    List<IgProfileAudienceDaily> findLatestCapturedAtOfNonZeroDiff(@Param("igProfile") IgProfile igProfile,
                                                                   @Param("type") String type);

    List<IgProfileAudienceDaily> findByIgProfileAndTypeAndCapturedAtOrderByCountDesc(
            @Param("igProfile") IgProfile igProfile,
            @Param("type") String type,
            @Param("capturedAt") OffsetDateTime capturedAt);

}
