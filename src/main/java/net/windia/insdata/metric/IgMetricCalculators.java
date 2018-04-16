package net.windia.insdata.metric;

import lombok.extern.slf4j.Slf4j;
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

@Slf4j
public final class IgMetricCalculators {

    private IgMetricCalculators() {}

    static class IgSimpleCalculator<S extends IgStat, V> implements IgMetricCalculator<S, ZonedDateTime, V> {

        private final Function<S, V> extractor;

        IgSimpleCalculator(Function<S, V> extractor) {
            this.extractor = extractor;
        }

        @Override
        public Map<ZonedDateTime, V> calculate(List<List<S>> dataSources, StatGranularity gran) {
            List<S> dataSource = dataSources.get(0);

            return dataSource.stream().collect(Collectors.toMap(
                    rec -> DateTimeUtils.dateTimeOfFacebookServer(rec.getIndicativeDate(), gran),
                    extractor));
        }
    }

    static class IgSimpleAggregator<S extends IgStat> implements IgMetricCalculator<S, ZonedDateTime, Integer> {

        private final ToIntFunction<S> extractor;
        private int timeUnitShift;

        IgSimpleAggregator(ToIntFunction<S> extractor) {
            this(extractor, 0);
        }

        IgSimpleAggregator(ToIntFunction<S> extractor, int timeUnitShift) {
            this.extractor = extractor;
            this.timeUnitShift = timeUnitShift;
        }

        @Override
        public Map<ZonedDateTime, Integer> calculate(List<List<S>> dataSources, StatGranularity gran) {
            List<S> dataSource = dataSources.get(0);

            return dataSource.stream()
                    .collect(Collectors.groupingBy(sourceRec -> DateTimeUtils.dateTimeOfFacebookServer(sourceRec.getIndicativeDate(), gran, timeUnitShift),
                            Collectors.summingInt(extractor)));
        }
    }

    static class IgConditionalAggregator<S extends IgStat> implements IgMetricCalculator<S, ZonedDateTime, Integer> {

        private final Predicate<S> filter;
        private final ToIntFunction<S> extractor;

        IgConditionalAggregator(Predicate<S> filter, ToIntFunction<S> extractor) {
            this.filter = filter;
            this.extractor = extractor;
        }

        @Override
        public Map<ZonedDateTime, Integer> calculate(List<List<S>> dataSources, StatGranularity gran) {
            List<S> dataSource = dataSources.get(0);

            return dataSource.stream()
                    .filter(filter)
                    .collect(Collectors.groupingBy(sourceRec -> DateTimeUtils.dateTimeOfFacebookServer(sourceRec.getIndicativeDate(), gran),
                            Collectors.summingInt(extractor)));
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
                    .collect(Collectors.toMap(stat1 -> DateTimeUtils.dateTimeOfFacebookServer(stat1.getIndicativeDate(), gran),
                            stat1 -> extractor.applyAsDouble(stat1, stats2Map.get(stat1.getIndicativeDate()))));
        }
    }

    static class IgBiSourceConditionalLeftDoubleAggregator<S extends IgStat, S1 extends S, S2 extends S, I> implements IgMetricCalculator<S, I, Double> {

        protected final ToDoubleBiFunction<S1, S2> extractor;
        protected final Function<S1, I> classifier;
        protected final Predicate<S1> filter;

        public IgBiSourceConditionalLeftDoubleAggregator(ToDoubleBiFunction<S1, S2> extractor,
                                                         Function<S1, I> classifier,
                                                         Predicate<S1> filter) {
            this.extractor = extractor;
            this.classifier = classifier;
            this.filter = filter;
        }

        @Override
        public Map<I, Double> calculate(List<List<S>> dataSources, StatGranularity gran) {

            @SuppressWarnings("unchecked")
            List<S1> stats1 = (List<S1>) dataSources.get(0);
            @SuppressWarnings("unchecked")
            Map<ZonedDateTime, S2> stats2Map = ((List<S2>) dataSources.get(1)).stream().collect(
                    Collectors.toMap(stat -> DateTimeUtils.dateTimeOfFacebookServer(stat.getIndicativeDate(), gran), Function.identity()));

            return stats1.stream()
                    .filter(filter)
                    .collect(Collectors.groupingBy(classifier,
                            Collectors.summingDouble(stat1 -> extractor.applyAsDouble(stat1,
                                    stats2Map.get(DateTimeUtils.dateTimeOfFacebookServer(stat1.getIndicativeDate(), gran))))));
        }
    }

    static class IgBiSourceLeftDoubleAggregator<S extends IgStat, S1 extends S, S2 extends S> implements IgMetricCalculator<S, ZonedDateTime, Double> {

        protected final ToDoubleBiFunction<S1, S2> extractor;

        IgBiSourceLeftDoubleAggregator(ToDoubleBiFunction<S1, S2> extractor) {
            this.extractor = extractor;
        }

        @Override
        public Map<ZonedDateTime, Double> calculate(List<List<S>> dataSources, StatGranularity gran) {

            @SuppressWarnings("unchecked")
            List<S1> stats1 = (List<S1>) dataSources.get(0);
            @SuppressWarnings("unchecked")
            Map<ZonedDateTime, S2> stats2Map = ((List<S2>) dataSources.get(1)).stream().collect(
                    Collectors.toMap(stat -> DateTimeUtils.dateTimeOfFacebookServer(stat.getIndicativeDate(), gran), Function.identity()));

            return stats1.stream()
                    .collect(Collectors.groupingBy(stat1 -> DateTimeUtils.dateTimeOfFacebookServer(stat1.getIndicativeDate(), gran),
                            Collectors.summingDouble(stat1 -> extractor.applyAsDouble(stat1,
                                    stats2Map.get(DateTimeUtils.dateTimeOfFacebookServer(stat1.getIndicativeDate(), gran))))));
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
                    .collect(Collectors.toMap(stat1 -> DateTimeUtils.dateTimeOfFacebookServer(stat1.getIndicativeDate(), gran),
                            stat1 -> extractor.applyAsInt(stat1,
                                    stats2Map.get(DateTimeUtils.dateTimeOfFacebookServer(stat1.getIndicativeDate(), gran)))));
        }
    }

    public static <S extends IgStat, V> IgSimpleCalculator<S, V> simpleCalculator(Function<S, V> extractor) {
        return new IgSimpleCalculator<>(extractor);
    }

    public static <S extends IgStat> IgConditionalAggregator<S> conditionalAggregator(Predicate<S> filter, ToIntFunction<S> extractor) {
        return new IgConditionalAggregator<>(filter, extractor);
    }

    public static <S extends IgStat> IgSimpleAggregator<S> simpleAggregator(ToIntFunction<S> extractor) {
        return new IgSimpleAggregator<>(extractor);
    }

    public static <S extends IgStat> IgSimpleAggregator<S> simpleAggregator(ToIntFunction<S> extractor, int timeUnitShift) {
        return new IgSimpleAggregator<>(extractor, timeUnitShift);
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
        return new IgBiSourceConditionalLeftDoubleAggregator<>(extractor, classifier, filter);
    }

    public static <S extends IgStat, S1 extends S, S2 extends S> IgBiSourceCalculator<S, S1, S2> biSourceCalculator(ToDoubleBiFunction<S1, S2> extractor) {
        return new IgBiSourceCalculator<>(extractor);
    }

    public static <S extends IgStat, S1 extends S, S2 extends S> IgBiSourceRightIntAggregator<S, S1, S2> biSourceRightIntAggregator(ToIntBiFunction<S1, Integer> extractor) {
        return new IgBiSourceRightIntAggregator<>(extractor);
    }
}
