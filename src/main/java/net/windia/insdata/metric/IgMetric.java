package net.windia.insdata.metric;

import net.windia.insdata.exception.UnsupportedGranularityException;
import net.windia.insdata.model.internal.IgMediaDiff;
import net.windia.insdata.model.internal.IgProfileDiff;
import net.windia.insdata.model.internal.IgProfileSnapshot;
import net.windia.insdata.model.internal.IgStat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static net.windia.insdata.metric.IgDataSource.DIFF;
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
                        (IgMediaDiff diff) -> diff.getCapturedAt().getTime() - diff.getMedia().getCreatedAt().getTime() < 3600000 * 8,
                        IgMediaDiff::getImpressions)
                ));

        allMetricsMap.put("IMPRESSIONS_POST_EXISTING", new IgMetric<>("IMPRESSIONS_POST_EXISTING", POST_DIFF,
                // daily calculator
                IgMetricCalculators.conditionalAggregator(
                        (IgMediaDiff diff) -> !diff.getComparedTo().equals(diff.getMedia().getCreatedAt()),
                        IgMediaDiff::getImpressions),
                // hourly calculator
                IgMetricCalculators.conditionalAggregator(
                        (IgMediaDiff diff) -> diff.getCapturedAt().getTime() - diff.getMedia().getCreatedAt().getTime() >= 3600000 * 8,
                        IgMediaDiff::getImpressions)
        ));

        allMetricsMap.put("IMPRESSIONS_PER_K_FOLLOWERS", new IgMetric<>("IMPRESSIONS_PER_K_FOLLOWERS", DIFF, SNAPSHOT,
                // daily calculator
                IgMetricCalculators.biSourceCalculator((IgProfileDiff diff, IgProfileSnapshot snapshot) ->
                        null == snapshot || 0 == snapshot.getFollowers() ? 0D : diff.getImpressions().doubleValue() / snapshot.getFollowers() * 1000
                ),
                // hourly calculator
                IgMetricCalculators.biSourceCalculator((IgProfileDiff diff, IgProfileSnapshot snapshot) ->
                        null == snapshot || 0 == snapshot.getFollowers() ? 0D : diff.getImpressions().doubleValue() / snapshot.getFollowers() * 1000
                )));

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

    private IgMetric(String name, IgDataSource source1, IgDataSource source2,
                     IgMetricCalculator<S, I, V> dailyCalculator, IgMetricCalculator<S, I, V> hourlyCalculator) {
        this.name = name;
        IgDataSource[] dataSources = {source1, source2};
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
