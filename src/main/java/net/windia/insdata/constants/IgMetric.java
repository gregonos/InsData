package net.windia.insdata.constants;

import net.windia.insdata.exception.UnsupportedGranularityException;
import net.windia.insdata.model.internal.*;
import net.windia.insdata.util.DateTimeUtils;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static net.windia.insdata.constants.IgDataSource.DIFF;
import static net.windia.insdata.constants.IgDataSource.POST_DIFF;
import static net.windia.insdata.constants.IgDataSource.SNAPSHOT;
import static net.windia.insdata.constants.StatGranularity.DAILY;
import static net.windia.insdata.constants.StatGranularity.HOURLY;

public enum IgMetric {

    FOLLOWERS() {
        @Override
        protected void init() {
            this.initForSameHourlyAndDaily(SNAPSHOT, sourceRecord -> ((IgProfileSnapshot) sourceRecord).getFollowers());
        }
    },

    FOLLOWERS_DIFF() {
        @Override
        protected void init() {
            this.initForSameHourlyAndDaily(DIFF, sourceRecord -> ((IgProfileDiff) sourceRecord).getFollowers());
        }
    },

    FOLLOWINGS() {
        @Override
        protected void init() {
            this.initForSameHourlyAndDaily(SNAPSHOT, sourceRecord -> ((IgProfileSnapshot) sourceRecord).getFollows());
        }
    },

    FOLLOWINGS_DIFF() {
        @Override
        protected void init() {
            this.initForSameHourlyAndDaily(DIFF, sourceRecord -> ((IgProfileDiff) sourceRecord).getFollows());
        }
    },

    POSTS() {
        @Override
        protected void init() {
            this.initForSameHourlyAndDaily(SNAPSHOT, sourceRecord -> ((IgProfileSnapshot) sourceRecord).getMediaCount());
        }
    },

    POSTS_DIFF() {
        @Override
        protected void init() {
            this.initForSameHourlyAndDaily(DIFF, sourceRecord -> ((IgProfileDiff) sourceRecord).getMediaCount());
        }
    },

    NEW_FOLLOWERS() {
        @Override
        protected void init() {
            this.initForHourlyDiffAndDailySnapshot(sourceRecord -> ((IgProfileStat) sourceRecord).getNewFollowers());
        }
    },

    IMPRESSIONS() {
        @Override
        protected void init() {
            this.initForHourlyDiffAndDailySnapshot(sourceRecord -> ((IgProfileStat) sourceRecord).getImpressions());
        }
    },

    REACH() {
        @Override
        protected void init() {
            this.initForHourlyDiffAndDailySnapshot(sourceRecord -> ((IgProfileStat) sourceRecord).getReach());
        }
    },

    IMPRESSIONS_PER_REACH() {
        @Override
        protected void init() {
            this.initForHourlyDiffAndDailySnapshot(sourceRecord -> {
                IgProfileStat stat = (IgProfileStat) sourceRecord;
                return 0 == stat.getReach() ? 0F : stat.getImpressions().floatValue() / stat.getReach();
            });
        }
    },

    PROFILE_VIEWS() {
        @Override
        protected void init() {
            this.initForHourlyDiffAndDailySnapshot(sourceRecord -> ((IgProfileStat) sourceRecord).getProfileViews());
        }
    },

    EMAIL_CONTACTS() {
        @Override
        protected void init() {
            this.initForHourlyDiffAndDailySnapshot(sourceRecord -> ((IgProfileStat) sourceRecord).getEmailContacts());
        }
    },

    PHONE_CALL_CLICKS() {
        @Override
        protected void init() {
            this.initForHourlyDiffAndDailySnapshot(sourceRecord -> ((IgProfileStat) sourceRecord).getPhoneCallClicks());
        }
    },

    GET_DIRECTIONS_CLICKS() {
        @Override
        protected void init() {
            this.initForHourlyDiffAndDailySnapshot(sourceRecord -> ((IgProfileStat) sourceRecord).getGetDirectionsClicks());
        }
    },

    WEBSITE_CLICKS() {
        @Override
        protected void init() {
            this.initForHourlyDiffAndDailySnapshot(sourceRecord -> ((IgProfileStat) sourceRecord).getWebsiteClicks());
        }
    },

    IMPRESSIONS_POST_NEW() {
        @Override
        protected void init() {
            getVariantsMap().put(DAILY,
                    new IgMetricVariant(POST_DIFF,
                            (IgNewPostAggregator) sourceRecord -> ((IgMediaDiff) sourceRecord).getImpressions()));
        }
    },

    IMPRESSIONS_POST_EXISTING() {
        @Override
        protected void init() {
            getVariantsMap().put(DAILY,
                    new IgMetricVariant(POST_DIFF,
                            (IgExistingPostAggregator) sourceRecord -> ((IgMediaDiff) sourceRecord).getImpressions()));
        }
    },
    ;

    private Map<StatGranularity, IgMetricVariant> variantsMap = new EnumMap<>(StatGranularity.class);

    static {
        EnumSet.allOf(IgMetric.class).forEach(IgMetric::init);
    }

    protected abstract void init();

    protected void initForSameHourlyAndDaily(IgDataSource source, IgMetricCalculator calculator) {
        this.variantsMap.put(HOURLY, new IgMetricVariant(source, calculator));
        this.variantsMap.put(DAILY, this.variantsMap.get(HOURLY));
    }

    protected void initForHourlyDiffAndDailySnapshot(IgMetricCalculator calculator) {
        this.variantsMap.put(HOURLY, new IgMetricVariant(DIFF, calculator));
        this.variantsMap.put(DAILY, new IgMetricVariant(SNAPSHOT, calculator));
    }

    protected Map<StatGranularity, IgMetricVariant> getVariantsMap() {
        return variantsMap;
    }

    public boolean supports(StatGranularity granularity) {
        return variantsMap.keySet().contains(granularity);
    }

    private IgMetricVariant getVariant(StatGranularity granularity) {
        IgMetricVariant variant = variantsMap.get(granularity);

        if (null == variant) {
            throw new UnsupportedGranularityException(granularity.getValue());
        }

        return variant;
    }

    public IgDataSource getSource(StatGranularity gran) {
        return getVariant(gran).source;
    }

    public Map<Object, Object> calculate(StatGranularity gran, List<? extends IgStat> sourceData) {
        return getVariant(gran).calculator.calculate(sourceData);
    }

    public Set<StatGranularity> getGranularities() {
        return this.variantsMap.keySet();
    }

    class IgMetricVariant {
        private IgDataSource source;
        private IgMetricCalculator calculator;

        IgMetricVariant(IgDataSource source, IgMetricCalculator calculator) {
            this.source = source;
            this.calculator = calculator;
        }
    }

    interface IgMetricCalculator {

        default Map<Object, Object> calculate(List<? extends IgStat> sourceData) {
            Map<Object, Object> resultMap = new LinkedHashMap<>(sourceData.size());

            sourceData.forEach(sourceRecord -> resultMap.put(sourceRecord.getIndicativeDate(), extract(sourceRecord)));

            return resultMap;
        }

        Object extract(IgStat sourceRecord);
    }

    interface IgMetricAggregator extends IgMetricCalculator {

        @Override
        default Map<Object, Object> calculate(List<? extends IgStat> sourceData) {
            Map<Object, Object> resultMap = new LinkedHashMap<>();

            Map<Object, Integer> reduceResult = sourceData.stream()
                    .filter(predicate())
                    .collect(Collectors.groupingBy(sourceRec -> DateTimeUtils.dateOfFacebookServer(sourceRec.getIndicativeDate()),
                            Collectors.summingInt(sourceRec -> (int) extract(sourceRec))));

            reduceResult.forEach(resultMap::put);

            return resultMap;
        }

        Predicate<IgStat> predicate();
    }

    interface IgNewPostAggregator extends IgMetricAggregator {
        @Override
        default Predicate<IgStat> predicate() {
            return sourceRec -> {
                IgMediaDiff diff = (IgMediaDiff) sourceRec;
                return diff.getComparedTo().equals(diff.getMedia().getCreatedAt());
            };
        }
    }

    interface IgExistingPostAggregator extends IgMetricAggregator {
        @Override
        default Predicate<IgStat> predicate() {
            return sourceRec -> {
                IgMediaDiff diff = (IgMediaDiff) sourceRec;
                return !diff.getComparedTo().equals(diff.getMedia().getCreatedAt());
            };
        }
    }

}
