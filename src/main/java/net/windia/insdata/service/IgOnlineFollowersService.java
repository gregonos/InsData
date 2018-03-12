package net.windia.insdata.service;

import lombok.extern.slf4j.Slf4j;
import net.windia.insdata.model.internal.IgOnlineFollowers;
import net.windia.insdata.model.internal.IgProfile;
import net.windia.insdata.model.internal.IgProfileSnapshotHourly;
import net.windia.insdata.repository.IgProfileSnapshotHourlyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class IgOnlineFollowersService {

    @Autowired
    private IgProfileSnapshotHourlyRepository igProfileSnapshotHourlyRepo;

    public void enrichPercentage(IgProfile igProfile, List<IgOnlineFollowers> onlineFollowersRecords, Date baseTime) {

        Calendar endTimeCal = Calendar.getInstance(TimeZone.getTimeZone(igProfile.getUser().getTimeZone()));
        endTimeCal.setTime(baseTime);
        endTimeCal.add(Calendar.DATE, 1);

        List<IgProfileSnapshotHourly> igProfileSnapshotHourlyList =
                igProfileSnapshotHourlyRepo.findFollowersByDay(igProfile, baseTime, endTimeCal.getTime());

        Map<Byte, Integer> hourToFollowersMap = new HashMap<>(24);
        for (IgProfileSnapshotHourly igProfileSnapshotHourly : igProfileSnapshotHourlyList) {
            hourToFollowersMap.put(igProfileSnapshotHourly.getHour(), igProfileSnapshotHourly.getFollowers());
        }

        for (IgOnlineFollowers onlineFollowersRecord : onlineFollowersRecords) {
            Integer followerCount = hourToFollowersMap.get(onlineFollowersRecord.getHour());

            if (null == followerCount || 0 == followerCount) {
                continue;
            }

            onlineFollowersRecord.setPercentage((float) onlineFollowersRecord.getCount() / followerCount);
        }
    }
}
