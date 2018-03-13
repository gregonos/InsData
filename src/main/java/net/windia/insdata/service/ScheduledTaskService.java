package net.windia.insdata.service;

import lombok.extern.slf4j.Slf4j;
import net.windia.insdata.model.client.IgAPIClientIgProfile;
import net.windia.insdata.model.client.IgAPIClientMedia;
import net.windia.insdata.model.client.IgAPIClientProfileAudience;
import net.windia.insdata.model.internal.IgProfile;
import net.windia.insdata.repository.IgProfileRepository;
import net.windia.insdata.service.restclient.IgRestClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ScheduledTaskService {

    @Autowired
    private IgRestClientService igRestClientService;

    @Autowired
    private IgProfileRepository igProfileRepo;

    @Autowired
    private IgProfileStatService igProfileStatService;

    @Autowired
    private IgMediaStatService igMediaStatService;

    private class IgRawMediaMetaHandler implements IgRawMediaHandler {

        @Override
        public boolean processRawMedia(IgProfile profile, List<IgAPIClientMedia> rawMediaList) {

            igMediaStatService.saveMediaMeta(profile, rawMediaList);
            return true;
        }
    }

    private class IgRawMediaStatHandler implements IgRawMediaHandler {

        @Override
        public boolean processRawMedia(IgProfile profile, List<IgAPIClientMedia> rawMediaList) {

            igMediaStatService.saveMediaHourlyStat(profile, rawMediaList);
            return true;
        }
    }

    private IgRawMediaMetaHandler rawMediaMetaHandler = new IgRawMediaMetaHandler();

    private IgRawMediaStatHandler rawMediaStatHandler = new IgRawMediaStatHandler();

//    @Scheduled(initialDelay = 2000, fixedRate = 3600000)
//    @Scheduled(cron = "1 0 * * * *")
    public void retrieveProfile() {

        IgProfile myProfile = igProfileRepo.findById(1L).get();

        log.info("Starting hourly job for account [" + myProfile.getBusinessAccountId() + "/" + myProfile.getUsername() + "]...");

        log.debug("Fetching profile basic snapshot...");

        IgAPIClientIgProfile igProfileRaw = igRestClientService.retrieveProfileStat(myProfile);
        log.debug("Response payload retrieved successfully from Facebook");

        boolean newDay = igProfileStatService.saveHourlyStat(myProfile, igProfileRaw);
        log.debug("Profile basic snapshot hourly data parsed and stored successfully!");

//        boolean newDay = true;

        if (newDay) {

            log.debug("A new reporting day detected. Start to process daily stat...");

            igProfileStatService.saveDailyStat(myProfile, igProfileRaw);

            log.debug("Profile basic snapshot daily data parsed and stored successfully!");

            // Audience
            log.debug("Fetching profile audience snapshot...");

            IgAPIClientProfileAudience igProfileAudienceRaw = igRestClientService.retrieveProfileAudienceStat(myProfile);
            log.debug("Response payload of audience retrieved successfully from Facebook");

            igProfileStatService.saveAudience(myProfile, igProfileAudienceRaw);
            log.debug("Profile audience data parsed and stored successfully!");

            // Online Followers
            log.debug("Fetching profile online followers data... ");

            IgAPIClientProfileAudience igOnlineFollowersRaw = igRestClientService.retrieveProfileOnlineFollowers(myProfile);
            log.debug("Response payload of online followers retrieved successfully from Facebook");

            igProfileStatService.saveOnlineFollowers(myProfile, igOnlineFollowersRaw);
            log.debug("Profile online followers data parsed and stored successfully!");
        }
    }

    public void retrieveMediaMeta() {
        IgProfile myProfile = igProfileRepo.findById(1L).get();

        log.info("Starting to download media for account [" + myProfile.getBusinessAccountId() + "/" + myProfile.getUsername() + "]...");

        int count = igRestClientService.retrieveAllMedia(myProfile, IgRestClientService.PARAM_FIELDS_MEDIA_META, rawMediaMetaHandler);

        log.info(count + " media meta entries are parsed and stored.");
    }

    @Scheduled(initialDelay = 2000, fixedRate = 3600000)
    public void retrieveMediaStat() {
        IgProfile myProfile = igProfileRepo.findById(1L).get();

        log.info("Starting to download hourly media stat for account [" + myProfile.getBusinessAccountId() + "/" + myProfile.getUsername() + "]...");

        int count = igRestClientService.retrieveAllMedia(myProfile, IgRestClientService.PARAM_FIELDS_MEDIA_STAT, rawMediaStatHandler);

        log.info(count + " media stat entries are parsed and stored.");
    }

}
