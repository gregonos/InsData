package net.windia.insdata.service;

import lombok.extern.slf4j.Slf4j;
import net.windia.insdata.model.client.IgAPIClientDataWrapper;
import net.windia.insdata.model.client.IgAPIClientIgProfile;
import net.windia.insdata.model.client.IgAPIClientInsight;
import net.windia.insdata.model.client.IgAPIClientProfileAudience;
import net.windia.insdata.model.internal.IgProfile;
import net.windia.insdata.repository.IgProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.Map;

@Slf4j
@Service
public class ScheduledTaskService {

    private static final String PARAM_FIELDS_PROFILE_STAT =
            "media_count,followers_count,follows_count,insights.metric(impressions,reach,profile_views,follower_count,email_contacts,phone_call_clicks,get_directions_clicks,website_clicks).period(day)";

    private static final String PARAM_FIELDS_PROFILE_AUDIENCE =
            "audience_gender_age,audience_country,audience_city,audience_locale";

    @Value("${insdata.facebook.graph-api-base-url}")
    private String graphAPIBaseUrl;

    @Autowired
    private IgProfileRepository igProfileRepo;

    @Autowired
    private IgProfileStatService igProfileStatService;

    @PostConstruct
    public void init() {
        log.info("Facebook Graph API baseUrl = " + graphAPIBaseUrl);
    }

//    @Scheduled(initialDelay = 2000, fixedRate = 3600000)
    @Scheduled(cron = "1 0 * * * *")
    public void retrieveProfile() {

        IgProfile myProfile = igProfileRepo.findById(1L).get();

        log.info("Starting hourly job for account [" + myProfile.getBusinessAccountId() + "/" + myProfile.getUsername() + "]...");

        RestTemplate restTemplate = new RestTemplate();

        log.debug("Fetching profile basic snapshot...");
        IgAPIClientIgProfile igProfileRaw = restTemplate.getForObject(
                graphAPIBaseUrl + myProfile.getBusinessAccountId() + "?fields={fields}&access_token={access_token}",
                IgAPIClientIgProfile.class, PARAM_FIELDS_PROFILE_STAT, myProfile.getToken());

        log.debug("Response payload retrieved successfully from Facebook");

        boolean newDay = igProfileStatService.saveHourlyStat(myProfile, igProfileRaw);

        log.debug("Profile basic snapshot hourly data parsed and stored successfully!");

        if (newDay) {
            igProfileStatService.saveDailyStat(myProfile, igProfileRaw);

            log.debug("Profile basic snapshot daily data parsed and stored successfully!");

            // Audience
            log.debug("Fetching profile audience snapshot...");
            IgAPIClientProfileAudience igProfileAudienceRaw = restTemplate.getForObject(
                    graphAPIBaseUrl + myProfile.getBusinessAccountId() + "/insights?metric={metric}&period=lifetime&access_token={access_token}",
                    IgAPIClientProfileAudience.class, PARAM_FIELDS_PROFILE_AUDIENCE, myProfile.getToken());

            log.debug("Response payload of audience retrieved successfully from Facebook");

            igProfileStatService.saveAudience(myProfile, igProfileAudienceRaw);
        }
    }

}
