package net.windia.insdata.metric;

import net.windia.insdata.model.internal.IgStat;

import java.util.List;
import java.util.Map;

public interface IgMetricCalculator<S extends IgStat, I, V> {
    Map<I, V> calculate(List<List<S>> dataSources, StatGranularity gran);
}
