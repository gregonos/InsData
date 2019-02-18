package net.windia.insdata.metric;

import lombok.extern.slf4j.Slf4j;
import net.windia.insdata.model.internal.IgMediaStat;
import net.windia.insdata.model.internal.IgStat;
import net.windia.insdata.util.DateTimeUtils;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleBiFunction;
import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public final class IgMetricCalculators {

    private IgMetricCalculators() {}

    static class IgSimpleCalculator<S extends IgStat, I, V> implements IgMetricCalculator<S, I, V> {

        private final Function<S, V> extractor;
        private final Function<S, I> indicator;

        IgSimpleCalculator(Function<S, V> extractor, Function<S, I> indicator) {
            this.extractor = extractor;
            this.indicator = indicator;
        }

        @Override
        public Map<I, V> calculate(List<List<S>> dataSources, StatGranularity gran) {
            List<S> dataSource = dataSources.get(0);

            return dataSource.stream().collect(Collectors.toMap(indicator, extractor));
        }
    }

    static class IgConditionalAggregator<S extends IgStat, I> implements IgMetricCalculator<S, I, Integer> {

        protected final Predicate<S> filter;
        protected final ToIntFunction<S> extractor;
        protected final Function<S, I> classifier;

        IgConditionalAggregator(Predicate<S> filter, ToIntFunction<S> extractor, Function<S, I> classifier) {
            this.filter = filter;
            this.extractor = extractor;
            this.classifier = classifier;
        }

        @Override
        public Map<I, Integer> calculate(List<List<S>> dataSources, StatGranularity gran) {
            List<S> dataSource = dataSources.get(0);

            Stream<S> s = dataSource.stream();
            if (null != filter) {
                s = s.filter(filter);
            }

            return s.collect(Collectors.groupingBy(classifier, Collectors.summingInt(extractor)));
        }
    }

    static class IgBiSourceCalculator<S extends IgStat, S1 extends S, S2 extends S> implements IgMetricCalculator<S, ZonedDateTime, Double> {

        private final ToDoubleBiFunction<S1, S2> extractor;

        IgBiSourceCalculator(ToDoubleBiFunction<S1, S2> extractor) {
            this.extractor = extractor;
        }

        @Override
        public Map<ZonedDateTime, Double> calculate(List<List<S>> dataSources, StatGranularity gran) {

            @SuppressWarnings("unchecked")
            List<S1> stats1 = (List<S1>) dataSources.get(0);
            @SuppressWarnings("unchecked")
            Map<OffsetDateTime, S2> stats2Map = ((List<S2>) dataSources.get(1)).stream().collect(
                    Collectors.toMap(IgStat::getIndicativeDate, Function.identity()));

            return stats1.stream()
                    .collect(Collectors.toMap(IgStat::getAggregatingDate,
                            stat1 -> extractor.applyAsDouble(stat1, stats2Map.get(stat1.getIndicativeDate()))));
        }
    }

    static class IgBiSourceConditionalLeftDoubleAggregator<S extends IgStat, S1 extends S, S2 extends S, I> implements IgMetricCalculator<S, I, Double> {

        protected final ToDoubleBiFunction<S1, S2> extractor;
        protected final Function<S1, I> classifier;
        protected final Predicate<S1> filter;

        IgBiSourceConditionalLeftDoubleAggregator(Predicate<S1> filter,
                                                         ToDoubleBiFunction<S1, S2> extractor,
                                                         Function<S1, I> classifier) {
            this.extractor = extractor;
            this.classifier = classifier;
            this.filter = filter;
        }

        IgBiSourceConditionalLeftDoubleAggregator(ToDoubleBiFunction<S1, S2> extractor,
                                                  Function<S1, I> classifier) {
            this(null, extractor, classifier);
        }

        @Override
        public Map<I, Double> calculate(List<List<S>> dataSources, StatGranularity gran) {

            @SuppressWarnings("unchecked")
            List<S1> stats1 = (List<S1>) dataSources.get(0);
            @SuppressWarnings("unchecked")
            Map<ZonedDateTime, S2> stats2Map = ((List<S2>) dataSources.get(1)).stream().collect(
                    Collectors.toMap(IgStat::getAggregatingDate, Function.identity()));


            Stream<S1> s = stats1.stream();
            if (null != filter) {
                s = s.filter(filter);
            }

            return s
                    .collect(Collectors.groupingBy(classifier,
                            Collectors.summingDouble(
                                    stat1 -> extractor.applyAsDouble(stat1, stats2Map.get(stat1.getAggregatingDate())))));
        }
    }

    static class IgBiSourceLeftDoubleAggregator<S extends IgStat, S1 extends S, S2 extends S> extends IgBiSourceConditionalLeftDoubleAggregator<S, S1, S2, ZonedDateTime> {

        IgBiSourceLeftDoubleAggregator(ToDoubleBiFunction<S1, S2> extractor) {
            super(extractor, IgStat::getAggregatingDate);
        }
    }

    static class IgBiSourceRightIntAggregator<S extends IgStat, S1 extends S, S2 extends S> implements IgMetricCalculator<S, ZonedDateTime, Integer> {
        private final ToIntBiFunction<S1, Integer> extractor;

        IgBiSourceRightIntAggregator(ToIntBiFunction<S1, Integer> extractor) {
            this.extractor = extractor;
        }

        @Override
        public Map<ZonedDateTime, Integer> calculate(List<List<S>> dataSources, StatGranularity gran) {

            @SuppressWarnings("unchecked")
            List<S1> stats1 = (List<S1>) dataSources.get(0);
            @SuppressWarnings("unchecked")
            Map<ZonedDateTime, Integer> stats2Map = ((List<S2>) dataSources.get(1)).stream().collect(
                    Collectors.groupingBy(
                            stat2 -> DateTimeUtils.dateTimeOfFacebookServer(stat2.getIndicativeDate().minus(15, ChronoUnit.MINUTES), gran, 1),
                            Collectors.summingInt(stat2 -> 1))
            );

            return stats1.stream()
                    .collect(Collectors.toMap(IgStat::getAggregatingDate,
                            stat1 -> extractor.applyAsInt(stat1, stats2Map.get(stat1.getAggregatingDate()))));
        }
    }

    public static <S extends IgStat, V> IgSimpleCalculator<S, ZonedDateTime, V> dateTimeCalculator(Function<S, V> extractor) {
        return new IgSimpleCalculator<>(extractor, IgStat::getAggregatingDate);
    }

    public static <S extends IgMediaStat, V> IgSimpleCalculator<S, String, V> postCalculator(Function<S, V> extractor) {
        return new IgSimpleCalculator<>(extractor, stat -> stat.getMedia().getId());
    }

    public static <S extends IgMediaStat> IgConditionalAggregator<S, String> postAggregator(ToIntFunction<S> extractor) {
        return conditionalPostAggregator(null, extractor);
    }

    public static <S extends IgMediaStat> IgConditionalAggregator<S, String> conditionalPostAggregator(Predicate<S> filter, ToIntFunction<S> extractor) {
        return new IgConditionalAggregator<>(filter, extractor, stat -> stat.getMedia().getId());
    }

    public static <S extends IgStat> IgConditionalAggregator<S, ZonedDateTime> conditionalDateTimeAggregator(Predicate<S> filter, ToIntFunction<S> extractor) {
        return new IgConditionalAggregator<>(filter, extractor, IgStat::getAggregatingDate);
    }

    public static <S extends IgStat, I> IgConditionalAggregator<S, I> aggregator(ToIntFunction<S> extractor, Function<S, I> classifier) {
        return new IgConditionalAggregator<>(null, extractor, classifier);
    }

    public static <S extends IgStat, I> IgConditionalAggregator<S, I> conditionalAggregator(Predicate<S> filter, ToIntFunction<S> extractor, Function<S, I> classifier) {
        return new IgConditionalAggregator<>(filter, extractor, classifier);
    }

    public static <S extends IgStat> IgConditionalAggregator<S, ZonedDateTime> dateTimeAggregator(ToIntFunction<S> extractor) {
        return conditionalDateTimeAggregator(null, extractor);
    }

    public static <S extends IgStat, S1 extends S, S2 extends S> IgBiSourceLeftDoubleAggregator<S, S1, S2> biSourceLeftDoubleAggregator(ToDoubleBiFunction<S1, S2> extractor) {
        return new IgBiSourceLeftDoubleAggregator<>(extractor);
    }

    public static <S extends IgStat, S1 extends S, S2 extends S, I>
        IgBiSourceConditionalLeftDoubleAggregator<S, S1, S2, I> biSourceConditionalLeftDoubleAggregator(
            ToDoubleBiFunction<S1, S2> extractor,
            Function<S1, I> classifier,
            Predicate<S1> filter
    ) {
        return new IgBiSourceConditionalLeftDoubleAggregator<>(filter, extractor, classifier);
    }

    public static <S extends IgStat, S1 extends S, S2 extends S, I>
    IgBiSourceConditionalLeftDoubleAggregator<S, S1, S2, I> biSourceConditionalLeftDoubleAggregator(
            ToDoubleBiFunction<S1, S2> extractor,
            Function<S1, I> classifier
    ) {
        return new IgBiSourceConditionalLeftDoubleAggregator<>(extractor, classifier);
    }

    public static <S extends IgStat, S1 extends S, S2 extends S> IgBiSourceCalculator<S, S1, S2> biSourceCalculator(ToDoubleBiFunction<S1, S2> extractor) {
        return new IgBiSourceCalculator<>(extractor);
    }

    public static <S extends IgStat, S1 extends S, S2 extends S> IgBiSourceRightIntAggregator<S, S1, S2> biSourceRightIntAggregator(ToIntBiFunction<S1, Integer> extractor) {
        return new IgBiSourceRightIntAggregator<>(extractor);
    }
}
