package net.windia.insdata.service.restclient;

import com.google.common.io.CharStreams;
import lombok.extern.slf4j.Slf4j;
import net.windia.insdata.model.client.*;
import net.windia.insdata.model.internal.IgProfile;
import net.windia.insdata.service.IgRawMediaHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;

@Slf4j
@Service
public class IgRestClientService {

    private static final String PARAM_FIELDS_PROFILE_STAT =
            "media_count,followers_count,follows_count,insights.metric(impressions,reach,profile_views,follower_count,email_contacts,phone_call_clicks,get_directions_clicks,website_clicks).period(day)";

    private static final String PARAM_FIELDS_PROFILE_AUDIENCE =
            "audience_gender_age,audience_country,audience_city,audience_locale";

    private static final String PARAM_FIELDS_PROFILE_ONLINE_FOLLOWERS =
            "online_followers";

    public static final String PARAM_FIELDS_MEDIA_META =
            "ig_id,caption,media_type,media_url,permalink,shortcode,thumbnail_url,timestamp";

    public static final String PARAM_FIELDS_MEDIA_STAT =
            "timestamp,media_type,like_count,comments_count,insights.metric(engagement,impressions,reach,saved)";

    public static final String PARAM_FIELDS_MEDIA_STAT_BASIC =
            "timestamp,media_type,like_count,comments_count";

    @Value("${insdata.facebook.graph-api-base-url}")
    private String graphAPIBaseUrl;

    private RestTemplate restTemplate;

    class RestCallResponseErrorHandler implements ResponseErrorHandler {

        @Override
        public boolean hasError(ClientHttpResponse response) throws IOException {
            return !response.getStatusCode().is2xxSuccessful();
        }

        @Override
        public void handleError(ClientHttpResponse response) throws IOException {
            InputStream is = response.getBody();
            String body = CharStreams.toString(new InputStreamReader(is));

            log.error("Error round in Facebook API response with code: " + response.getRawStatusCode());
            log.error(body);
        }
    }

    @PostConstruct
    public void init() {
        restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new RestCallResponseErrorHandler());

        log.info("Facebook Graph API baseUrl = " + graphAPIBaseUrl);
    }

    public IgAPIClientIgProfile retrieveProfileInsightHistory(IgProfile profile, Long since, Long until) {
        String targetUrl = graphAPIBaseUrl + profile.getBusinessAccountId() +
                "?fields={fields}.since({since}).until({until})&period=day&access_token={access_token}";

        log.debug(targetUrl);

        ResponseEntity<IgAPIClientIgProfile> response = restTemplate.getForEntity(targetUrl,
                IgAPIClientIgProfile.class, PARAM_FIELDS_PROFILE_STAT, since, until, profile.getToken());

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            return null;
        }
    }

    public IgAPIClientIgProfile retrieveProfileStat(IgProfile profile) {
        String targetUrl = graphAPIBaseUrl + profile.getBusinessAccountId() +
                "?fields={fields}&period=day&access_token={access_token}";

        log.debug(targetUrl);

        ResponseEntity<IgAPIClientIgProfile> response = restTemplate.getForEntity(targetUrl,
                IgAPIClientIgProfile.class, PARAM_FIELDS_PROFILE_STAT, profile.getToken());

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            return null;
        }
    }

    public IgAPIClientProfileAudience retrieveProfileAudienceStat(IgProfile profile) {
        String targetUrl = graphAPIBaseUrl + profile.getBusinessAccountId() +
                "/insights?metric={metric}&period=lifetime&access_token={access_token}";

        ResponseEntity<IgAPIClientProfileAudience> response = restTemplate.getForEntity(targetUrl,
                IgAPIClientProfileAudience.class, PARAM_FIELDS_PROFILE_AUDIENCE, profile.getToken());

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            return null;
        }
    }

    public IgAPIClientProfileAudience retrieveProfileOnlineFollowers(IgProfile profile) {

        long since = ((new Date()).getTime() - 86400000 * 2) / 1000;
        long until = since + 86400;
//
//            since = 1520665261;
//            until = 1520751661;
//
//            log.debug("since = " + since + " until = " + until);

        String targetUrl = graphAPIBaseUrl + profile.getBusinessAccountId() +
                "/insights?metric={metric}&period=lifetime&since={since}&until={until}&access_token={access_token}";

        ResponseEntity<IgAPIClientProfileAudience> response = restTemplate.getForEntity(targetUrl,
                IgAPIClientProfileAudience.class, PARAM_FIELDS_PROFILE_ONLINE_FOLLOWERS, since, until, profile.getToken());

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            return null;
        }
    }

    public IgAPIClientMedia retrieveMediaMeta(String mediaId, IgProfile profile) {
        String targetUrl = graphAPIBaseUrl + mediaId + "?fields={fields}&access_token={access_token}";

        ResponseEntity<IgAPIClientMedia> response = restTemplate.getForEntity(targetUrl,
                IgAPIClientMedia.class, PARAM_FIELDS_MEDIA_META, profile.getToken());

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            return null;
        }
    }

    public int retrieveAllMedia(IgProfile profile, String fields, IgRawMediaHandler rawMediaHandler, int batchesLimit) {

        int mediaCountRetrieved = 0;

        String targetUrl = graphAPIBaseUrl + profile.getBusinessAccountId() + "/media?fields={fields}&access_token={access_token}";

        ResponseEntity<IgAPIClientMediaSet> response = restTemplate.getForEntity(targetUrl,
                IgAPIClientMediaSet.class, fields, profile.getToken());

        if (!response.getStatusCode().is2xxSuccessful()) {
            return mediaCountRetrieved;
        }

        IgAPIClientMediaSet mediasRaw = response.getBody();
        int mediaCountInThisBatch = mediasRaw.getData().size();
        mediaCountRetrieved += mediaCountInThisBatch;
        int batchesDone = 1;

        log.debug(mediaCountInThisBatch + " entries of media received from Facebook response payload.");

        Date now = new Date();

        rawMediaHandler.processRawMedia(profile, mediasRaw.getData(), now);

        log.debug("Media data parsed and stored.");

        try {
            while (null != mediasRaw.getPaging().getNext()) {
                if (batchesDone == batchesLimit) {
                    break;
                }
                log.debug("Continue to retrieve next page at: " + mediasRaw.getPaging().getNext());

                response = restTemplate.getForEntity(URLDecoder.decode(mediasRaw.getPaging().getNext(), "UTF-8"),
                        IgAPIClientMediaSet.class);


                if (response.getStatusCode().is2xxSuccessful()) {
                    mediasRaw = response.getBody();
                } else {
                    if (400 == response.getStatusCodeValue()) {
                        // These batch includes media which are not eligible to insights
                        // start to evaluate one by one
                        log.debug("This batch includes media which are ineligible to insights, start to evaluate one by one");
                        mediasRaw = handleMixedMediaBatch(profile, mediasRaw.getPaging());
                    } else {
                        break;
                    }
                }

                mediaCountInThisBatch = mediasRaw.getData().size();
                mediaCountRetrieved += mediaCountInThisBatch;
                batchesDone++;

                log.debug(mediaCountInThisBatch + " entries of media received from Facebook response payload.");

                rawMediaHandler.processRawMedia(profile, mediasRaw.getData(), now);

                log.debug("Media data parsed and stored.");
            }
        } catch (UnsupportedEncodingException e) {
            log.error("failed to decode with UTF-8", e);
        }

        return mediaCountRetrieved;
    }

    private IgAPIClientMediaSet handleMixedMediaBatch(IgProfile profile, IgAPIClientPaging startPaging) {

        List<IgAPIClientMedia> mediaList = new ArrayList<>(25);

        String targetUrl = graphAPIBaseUrl + profile.getBusinessAccountId() +
                "/media?fields={fields}&after={after}&limit={limit}&access_token={access_token}";

        int businessMediaCount = 0;
        IgAPIClientMediaSet mediasRaw = null;
        ResponseEntity<IgAPIClientMediaSet> response;
        IgAPIClientPaging paging = startPaging;

        do {
            response = restTemplate.getForEntity(targetUrl, IgAPIClientMediaSet.class,
                    PARAM_FIELDS_MEDIA_STAT, paging.getCursors().getAfter(), 1, profile.getToken());

            if (response.getStatusCode().is2xxSuccessful()) {
                mediasRaw = response.getBody();
                mediaList.addAll(mediasRaw.getData());
                paging = mediasRaw.getPaging();
                businessMediaCount++;
            } else if (400 == response.getStatusCodeValue()) {

                log.debug("The first ineligible media found. " + businessMediaCount + " business media have been found. Start to retrieve basic stat only for the rest.");

                response = restTemplate.getForEntity(targetUrl, IgAPIClientMediaSet.class,
                        PARAM_FIELDS_MEDIA_STAT_BASIC, paging.getCursors().getAfter(), 25 - businessMediaCount, profile.getToken());
                mediasRaw = response.getBody();
                mediaList.addAll(mediasRaw.getData());
                mediasRaw.setData(mediaList);
                mediasRaw.getPaging().setNext(graphAPIBaseUrl + profile.getBusinessAccountId()
                        + "/media?fields=" + PARAM_FIELDS_MEDIA_STAT_BASIC + "&after=" + mediasRaw.getPaging().getCursors().getAfter()
                        + "&limit=25&access_token=" + profile.getToken());

                break;
            } else {
                break;
            }
        } while (null != mediasRaw.getPaging().getNext());

        return mediasRaw;
    }
}
