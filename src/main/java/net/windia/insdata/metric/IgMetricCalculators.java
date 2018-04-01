package net.windia.insdata.metric;

import net.windia.insdata.model.internal.IgStat;
import net.windia.insdata.util.DateTimeUtils;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleBiFunction;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

public final class IgMetricCalculators {

    private IgMetricCalculators() {}

    static class IgSimpleCalculator<S extends IgStat, V> implements IgMetricCalculator<S, Date, V> {

        private final Function<S, V> extractor;

        IgSimpleCalculator(Function<S, V> extractor) {
            this.extractor = extractor;
        }

        @Override
        public Map<Date, V> calculate(List<List<S>> dataSources, StatGranularity gran) {
            List<S> dataSource = dataSources.get(0);
            Map<Date, V> resultMap = new LinkedHashMap<>(dataSource.size());

            dataSource.forEach(sourceRecord -> resultMap.put(
                    DateTimeUtils.dateTimeOfFacebookServer(sourceRecord.getIndicativeDate(), gran), extractor.apply(sourceRecord)));

            return resultMap;
        }
    }

    static class IgConditionalAggregator<S extends IgStat> implements IgMetricCalculator<S, Date, Integer> {

        private final Predicate<S> filter;
        private final ToIntFunction<S> extractor;

        IgConditionalAggregator(Predicate<S> filter, ToIntFunction<S> extractor) {
            this.filter = filter;
            this.extractor = extractor;
        }

        @Override
        public Map<Date, Integer> calculate(List<List<S>> dataSources, StatGranularity gran) {
            List<S> dataSource = dataSources.get(0);

            return dataSource.stream()
                    .filter(filter)
                    .collect(Collectors.groupingBy(sourceRec -> DateTimeUtils.dateTimeOfFacebookServer(sourceRec.getIndicativeDate(), gran),
                            Collectors.summingInt(extractor)));
        }
    }

    static class IgBiSourceCalculator<S extends IgStat, S1 extends S, S2 extends S> implements IgMetricCalculator<S, Date, Double> {

        private final ToDoubleBiFunction<S1, S2> extractor;

        IgBiSourceCalculator(ToDoubleBiFunction<S1, S2> extractor) {
            this.extractor = extractor;
        }

        @Override
        public Map<Date, Double> calculate(List<List<S>> dataSources, StatGranularity gran) {

            List<S1> stats1 = (List<S1>) dataSources.get(0);
            Map<Date, S2> stats2Map = ((List<S2>) dataSources.get(1)).stream().collect(Collectors.toMap(IgStat::getIndicativeDate, Function.identity()));

            return stats1.stream()
                    .collect(Collectors.toMap(stat1 -> DateTimeUtils.dateTimeOfFacebookServer(stat1.getIndicativeDate(), gran),
                            stat1 -> extractor.applyAsDouble(stat1, stats2Map.get(stat1.getIndicativeDate()))));
        }
    }

    static class IgBiSourceAggregator<S extends IgStat, S1 extends S, S2 extends S> implements IgMetricCalculator<S, Date, Double> {

        private final ToDoubleBiFunction<S1, S2> extractor;

        IgBiSourceAggregator(ToDoubleBiFunction<S1, S2> extractor) {
            this.extractor = extractor;
        }

        @Override
        public Map<Date, Double> calculate(List<List<S>> dataSources, StatGranularity gran) {

            List<S1> stats1 = (List<S1>) dataSources.get(0);
            Map<Date, S2> stats2Map = ((List<S2>) dataSources.get(1)).stream().collect(Collectors.toMap(IgStat::getIndicativeDate, Function.identity()));

            return stats1.stream()
                    .collect(Collectors.groupingBy(stat1 -> DateTimeUtils.dateTimeOfFacebookServer(stat1.getIndicativeDate(), gran),
                            Collectors.summingDouble(stat1 -> extractor.applyAsDouble(stat1, stats2Map.get(stat1.getIndicativeDate())))));
        }
    }

    public static <S extends IgStat, V> IgSimpleCalculator<S, V> simpleCalculator(Function<S, V> extractor) {
        return new IgSimpleCalculator<>(extractor);
    }

    public static <S extends IgStat> IgConditionalAggregator<S> conditionalAggregator(Predicate<S> filter, ToIntFunction<S> extractor) {
        return new IgConditionalAggregator<>(filter, extractor);
    }

    public static <S extends IgStat, S1 extends S, S2 extends S> IgBiSourceAggregator<S, S1, S2> biSourceAggregator(ToDoubleBiFunction<S1, S2> extractor) {
        return new IgBiSourceAggregator<>(extractor);
    }

    public static <S extends IgStat, S1 extends S, S2 extends S> IgBiSourceCalculator<S, S1, S2> biSourceCalculator(ToDoubleBiFunction<S1, S2> extractor) {
        return new IgBiSourceCalculator<>(extractor);
    }
}
