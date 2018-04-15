package net.windia.insdata.metric;

import net.windia.insdata.exception.UnsupportedGranularityException;
import net.windia.insdata.model.internal.IgMedia;
import net.windia.insdata.model.internal.IgMediaDiff;
import net.windia.insdata.model.internal.IgProfileDiff;
import net.windia.insdata.model.internal.IgProfileSnapshot;
import net.windia.insdata.model.internal.IgStat;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static net.windia.insdata.metric.IgDataSource.DIFF;
import static net.windia.insdata.metric.IgDataSource.POSTS;
import static net.windia.insdata.metric.IgDataSource.POST_DIFF;
import static net.windia.insdata.metric.IgDataSource.SNAPSHOT;
import static net.windia.insdata.metric.StatGranularity.DAILY;
import static net.windia.insdata.metric.StatGranularity.HOURLY;

public class IgMetric<S extends IgStat, I, V> {

    private static Map<String, IgMetric<? extends IgStat, ?, ?>> allMetricsMap = new HashMap<>();

    static {
        allMetricsMap.put("FOLLOWERS", new IgMetric<>("FOLLOWERS", SNAPSHOT,
                IgMetricCalculators.simpleCalculator(IgProfileSnapshot::getFollowers)));

        allMetricsMap.put("FOLLOWERS_DIFF", new IgMetric<>("FOLLOWERS_DIFF", DIFF,
                IgMetricCalculators.simpleCalculator(IgProfileDiff::getFollowers)));

        allMetricsMap.put("FOLLOWERS_GAIN", new IgMetric<>("FOLLOWERS_GAIN", DIFF,
                IgMetricCalculators.simpleCalculator(
                        (IgProfileDiff diff) -> Math.max(diff.getFollowers(), diff.getNewFollowers()))
                ));

        allMetricsMap.put("FOLLOWERS_LOSS", new IgMetric<>("FOLLOWERS_LOSS", DIFF,
                IgMetricCalculators.simpleCalculator(
                        (IgProfileDiff diff) -> diff.getFollowers() - Math.max(diff.getFollowers(), diff.getNewFollowers()))
        ));

        allMetricsMap.put("FOLLOWINGS", new IgMetric<>("FOLLOWINGS", SNAPSHOT,
                IgMetricCalculators.simpleCalculator(IgProfileSnapshot::getFollows)));

        allMetricsMap.put("FOLLOWINGS_DIFF", new IgMetric<>("FOLLOWINGS_DIFF", DIFF,
                IgMetricCalculators.simpleCalculator(IgProfileDiff::getFollows)));

        allMetricsMap.put("POSTS", new IgMetric<>("POSTS", SNAPSHOT,
                IgMetricCalculators.simpleCalculator(IgProfileSnapshot::getMediaCount)));

        allMetricsMap.put("POSTS_DIFF", new IgMetric<>("POSTS_DIFF", DIFF,
                IgMetricCalculators.simpleCalculator(IgProfileDiff::getMediaCount)));

        allMetricsMap.put("NEW_FOLLOWERS", new IgMetric<>("NEW_FOLLOWERS", DIFF,
                IgMetricCalculators.simpleCalculator(IgProfileDiff::getNewFollowers)));

        allMetricsMap.put("IMPRESSIONS", new IgMetric<>("IMPRESSIONS", DIFF,
                IgMetricCalculators.simpleCalculator(IgProfileDiff::getImpressions)));

        allMetricsMap.put("REACH", new IgMetric<>("REACH", DIFF,
                IgMetricCalculators.simpleCalculator(IgProfileDiff::getReach)));

        allMetricsMap.put("IMPRESSIONS_PER_REACH", new IgMetric<>("IMPRESSIONS_PER_REACH", DIFF,
                IgMetricCalculators.simpleCalculator((IgProfileDiff diff) ->
                        0 == diff.getReach() ? 0F : diff.getImpressions().floatValue() / diff.getReach())));

        allMetricsMap.put("PROFILE_VIEWS", new IgMetric<>("PROFILE_VIEWS", DIFF,
                IgMetricCalculators.simpleCalculator(IgProfileDiff::getProfileViews)));

        allMetricsMap.put("EMAIL_CONTACTS", new IgMetric<>("EMAIL_CONTACTS", DIFF,
                IgMetricCalculators.simpleCalculator(IgProfileDiff::getEmailContacts)));

        allMetricsMap.put("PHONE_CALL_CLICKS", new IgMetric<>("PHONE_CALL_CLICKS", DIFF,
                IgMetricCalculators.simpleCalculator(IgProfileDiff::getPhoneCallClicks)));

        allMetricsMap.put("GET_DIRECTIONS_CLICKS", new IgMetric<>("GET_DIRECTIONS_CLICKS", DIFF,
                IgMetricCalculators.simpleCalculator(IgProfileDiff::getGetDirectionsClicks)));

        allMetricsMap.put("WEBSITE_CLICKS", new IgMetric<>("WEBSITE_CLICKS", DIFF,
                IgMetricCalculators.simpleCalculator(IgProfileDiff::getWebsiteClicks)));

        allMetricsMap.put("IMPRESSIONS_POST_NEW", new IgMetric<>("IMPRESSIONS_POST_NEW", POST_DIFF,
                // daily calculator
                IgMetricCalculators.conditionalAggregator(
                        (IgMediaDiff diff) -> diff.getComparedTo().equals(diff.getMedia().getCreatedAt()),
                        IgMediaDiff::getImpressions),
                // hourly calculator
                IgMetricCalculators.conditionalAggregator(
                        (IgMediaDiff diff) -> Duration.between(diff.getMedia().getCreatedAt(), diff.getCapturedAt()).getSeconds() < 3600 * 8,
                        IgMediaDiff::getImpressions)
                ));

        allMetricsMap.put("IMPRESSIONS_POST_EXISTING", new IgMetric<>("IMPRESSIONS_POST_EXISTING", POST_DIFF,
                // daily calculator
                IgMetricCalculators.conditionalAggregator(
                        (IgMediaDiff diff) -> !diff.getComparedTo().equals(diff.getMedia().getCreatedAt()),
                        IgMediaDiff::getImpressions),
                // hourly calculator
                IgMetricCalculators.conditionalAggregator(
                        (IgMediaDiff diff) -> Duration.between(diff.getMedia().getCreatedAt(), diff.getCapturedAt()).getSeconds() >= 3600 * 8,
                        IgMediaDiff::getImpressions)
        ));

        allMetricsMap.put("IMPRESSIONS_PER_K_FOLLOWERS",
                new IgMetric<>("IMPRESSIONS_PER_K_FOLLOWERS", new IgDataSource[] {DIFF, SNAPSHOT},
                IgMetricCalculators.biSourceCalculator((IgProfileDiff diff, IgProfileSnapshot snapshot) ->
                        null == snapshot || 0 == snapshot.getFollowers() ? 0D : diff.getImpressions().doubleValue() / snapshot.getFollowers() * 1000
                )));

        allMetricsMap.put("ENGAGEMENTS", new IgMetric<>("ENGAGEMENTS", POST_DIFF,
                IgMetricCalculators.simpleAggregator(IgMediaDiff::getEngagementSum)));

        allMetricsMap.put("LIKES", new IgMetric<>("LIKES", POST_DIFF,
                IgMetricCalculators.simpleAggregator(IgMediaDiff::getLikes)));

        allMetricsMap.put("VIDEO_VIEWS", new IgMetric<>("VIDEO_VIEWS", POST_DIFF,
                IgMetricCalculators.simpleAggregator(IgMediaDiff::getVideoViews)));

        allMetricsMap.put("COMMENTS", new IgMetric<>("COMMENTS", POST_DIFF,
                IgMetricCalculators.simpleAggregator(IgMediaDiff::getComments)));

        allMetricsMap.put("SAVES", new IgMetric<>("SAVES", POST_DIFF,
                IgMetricCalculators.simpleAggregator(IgMediaDiff::getSaved)));

        allMetricsMap.put("ENGAGEMENTS_PER_K_FOLLOWERS",
                new IgMetric<>("ENGAGEMENTS_PER_K_FOLLOWERS", new IgDataSource[] {POST_DIFF, SNAPSHOT},
                        IgMetricCalculators.biSourceLeftDoubleAggregator((IgMediaDiff diff, IgProfileSnapshot snapshot) ->
                                null == snapshot || 0 == snapshot.getFollowers() ? 0D : diff.getEngagementSum().doubleValue() / snapshot.getFollowers() * 1000
                        )));

        allMetricsMap.put("ENGAGEMENTS_PER_K_REACH",
                new IgMetric<>("ENGAGEMENTS_PER_K_REACH", new IgDataSource[] {POST_DIFF, DIFF},
                        IgMetricCalculators.biSourceLeftDoubleAggregator((IgMediaDiff m, IgProfileDiff p) ->
                                null == p || 0 == p.getReach() ? 0D : m.getEngagement().doubleValue() / p.getReach() * 1000
                        )));

        allMetricsMap.put("ENGAGEMENTS_POST_NEW", new IgMetric<>("ENGAGEMENTS_POST_NEW", POST_DIFF,
                // daily calculator
                IgMetricCalculators.conditionalAggregator(
                        (IgMediaDiff diff) -> diff.getComparedTo().equals(diff.getMedia().getCreatedAt()),
                        IgMediaDiff::getEngagementSum),
                // hourly calculator
                IgMetricCalculators.conditionalAggregator(
                        (IgMediaDiff diff) -> Duration.between(diff.getMedia().getCreatedAt(), diff.getCapturedAt()).getSeconds() < 3600 * 8,
                        IgMediaDiff::getEngagementSum)
        ));

        allMetricsMap.put("ENGAGEMENTS_POST_EXISTING", new IgMetric<>("ENGAGEMENTS_POST_EXISTING", POST_DIFF,
                // daily calculator
                IgMetricCalculators.conditionalAggregator(
                        (IgMediaDiff diff) -> !diff.getComparedTo().equals(diff.getMedia().getCreatedAt()),
                        IgMediaDiff::getEngagementSum),
                // hourly calculator
                IgMetricCalculators.conditionalAggregator(
                        (IgMediaDiff diff) -> Duration.between(diff.getMedia().getCreatedAt(), diff.getCapturedAt()).getSeconds() >= 3600 * 8,
                        IgMediaDiff::getEngagementSum)
        ));

        allMetricsMap.put("POSTS_ADD", new IgMetric<>("POSTS_ADD", POSTS,
                IgMetricCalculators.simpleAggregator((IgMedia media) -> 1, 1)));

        allMetricsMap.put("POSTS", new IgMetric<>("POSTS", SNAPSHOT,
                IgMetricCalculators.simpleCalculator(IgProfileSnapshot::getMediaCount)));

        allMetricsMap.put("POSTS_DEL", new IgMetric<>("POSTS_DEL", new IgDataSource[]{DIFF, POSTS},
                IgMetricCalculators.biSourceRightIntAggregator(
                        (IgProfileDiff diff, Integer postsAdd) -> diff.getMediaCount() - (null == postsAdd ? 0 : postsAdd))));

    }
//
//    protected <SSS extends IgStat, I, V> void initForSameHourlyAndDaily(IgDataSource source, IgMetricCalculator<SSS, I, V> calculator) {
//        this.variantsMap.put(HOURLY, new IgMetricVariant<>(source, calculator));
//        this.variantsMap.put(DAILY, this.variantsMap.get(HOURLY));
//    }

//    protected Map<StatGranularity, IgMetricVariant<? extends IgStat, ?, ?>> getVariantsMap() {
//        return variantsMap;
//    }

    private String name;
    private Map<StatGranularity, IgMetricVariant<S, I, V>> variantsMap = new EnumMap<>(StatGranularity.class);

    private IgMetric(String name, IgDataSource source, IgMetricCalculator<S, I, V> calculator) {
        this.name = name;
        this.variantsMap.put(HOURLY, new IgMetricVariant<>(source, calculator));
        this.variantsMap.put(DAILY, this.variantsMap.get(HOURLY));
    }

    private IgMetric(String name, IgDataSource source,
                     IgMetricCalculator<S, I, V> dailyCalculator, IgMetricCalculator<S, I, V> hourlyCalculator) {

        this(name, source, hourlyCalculator);
        this.variantsMap.put(DAILY, new IgMetricVariant<>(source, dailyCalculator));
    }

    private IgMetric(String name, IgDataSource[] dataSources,
                     IgMetricCalculator<S, I,V> calculator) {
        this.name = name;
        this.variantsMap.put(HOURLY, new IgMetricVariant<>(dataSources, calculator));
        this.variantsMap.put(DAILY, this.variantsMap.get(HOURLY));
    }

    private IgMetric(String name, IgDataSource[] dataSources,
                     IgMetricCalculator<S, I, V> dailyCalculator, IgMetricCalculator<S, I, V> hourlyCalculator) {
        this.name = name;
        this.variantsMap.put(HOURLY, new IgMetricVariant<>(dataSources, hourlyCalculator));
        this.variantsMap.put(DAILY, new IgMetricVariant<>(dataSources, dailyCalculator));
    }

    public static IgMetric<? extends IgStat, ?, ?> forName(String name) {
        return allMetricsMap.get(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean supports(StatGranularity granularity) {
        return variantsMap.keySet().contains(granularity);
    }

    private IgMetricVariant<S, I, V> getVariant(StatGranularity granularity) {
        IgMetricVariant<S, I, V> variant = variantsMap.get(granularity);

        if (null == variant) {
            throw new UnsupportedGranularityException(granularity.getValue());
        }

        return variant;
    }

    public Collection<IgDataSource> getSources(StatGranularity gran) {
        return getVariant(gran).sources;
    }

    public Map<I, V> calculate(StatGranularity gran, List<List<S>> dataSources) {
        return getVariant(gran).calculator.calculate(dataSources, gran);
    }

    public Set<StatGranularity> getGranularities() {
        return this.variantsMap.keySet();
    }

    class IgMetricVariant<SS extends IgStat, II, VV> {
        private List<IgDataSource> sources = new ArrayList<>();
        private final IgMetricCalculator<SS, II, VV> calculator;

        IgMetricVariant(IgDataSource source, IgMetricCalculator<SS, II, VV> calculator) {
            this.sources.add(source);
            this.calculator = calculator;
        }

        IgMetricVariant(IgDataSource[] sources, IgMetricCalculator<SS, II, VV> calculator) {
            Collections.addAll(this.sources, sources);
            this.calculator = calculator;
        }
    }
}
