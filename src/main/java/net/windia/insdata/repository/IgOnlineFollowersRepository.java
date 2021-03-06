package net.windia.insdata.repository;

import net.windia.insdata.model.internal.IgOnlineFollowers;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface IgOnlineFollowersRepository extends CrudRepository<IgOnlineFollowers, Long> {

    List<IgOnlineFollowers> findByIgProfileIdAndDateTimeBetweenOrderByDateTimeAsc(Long profileId, OffsetDateTime since, OffsetDateTime until);

    @Query("SELECT new IgOnlineFollowers(o.date, 0, o.weekday, AVG(o.count), AVG(o.percentage)) FROM IgOnlineFollowers o " +
            "WHERE o.igProfile.id = :profileId AND o.dateTime BETWEEN :since AND :until " +
            "GROUP BY o.date HAVING COUNT(o.date) = 24 ORDER BY o.date ASC")
    List<IgOnlineFollowers> findDailyByIgProfileIdAndDateRange(@Param("profileId") Long profileId,
                                                               @Param("since") OffsetDateTime since,
                                                               @Param("until") OffsetDateTime until);

    @Query("SELECT new IgOnlineFollowers(o.date, o.hour, o.weekday, AVG(o.count), AVG(o.percentage)) FROM IgOnlineFollowers  o " +
            "WHERE o.igProfile.id = :profileId AND o.percentage IS NOT NULL " +
            "GROUP BY o.hour ORDER BY o.hour ASC")
    List<IgOnlineFollowers> findAggregateHourByProfileId(@Param("profileId") Long profileId);

    @Query("SELECT new IgOnlineFollowers(o.date, o.hour, o.weekday, AVG(o.count), AVG(o.percentage)) FROM IgOnlineFollowers  o " +
            "WHERE o.igProfile.id = :profileId AND o.percentage IS NOT NULL " +
            "GROUP BY o.weekday ORDER BY o.weekday ASC")
    List<IgOnlineFollowers> findAggregateWeekdayByProfileId(@Param("profileId") Long profileId);

    @Query("SELECT new IgOnlineFollowers(o.date, o.hour, o.weekday, AVG(o.count), AVG(o.percentage)) FROM IgOnlineFollowers  o " +
            "WHERE o.igProfile.id = :profileId AND o.percentage IS NOT NULL " +
            "GROUP BY o.weekday, o.hour ORDER BY o.weekday DESC, o.hour ASC")
    List<IgOnlineFollowers> findAggregateHourAndWeekdayByProfileId(@Param("profileId") Long profileId);
}
