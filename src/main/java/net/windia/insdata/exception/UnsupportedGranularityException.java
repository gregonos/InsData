package net.windia.insdata.exception;

import net.windia.insdata.constants.IgMetric;
import net.windia.insdata.constants.StatGranularity;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.EnumSet;

public class UnsupportedGranularityException extends InsDataException {

    public UnsupportedGranularityException(String invalidName) {
        this(invalidName, EnumSet.allOf(StatGranularity.class));
    }

    public UnsupportedGranularityException(String invalidName, Collection<StatGranularity> supportedList) {
        super(buildMessage(invalidName, supportedList));
    }

    public UnsupportedGranularityException(String invalidName, Collection<StatGranularity> supportedList, Throwable cause) {
        super(buildMessage(invalidName, supportedList), cause);
    }

    public UnsupportedGranularityException(IgMetric metric, StatGranularity granInstance) {
        super(buildMessage(metric, granInstance));
    }

    private static String buildMessage(IgMetric metric, StatGranularity unsupported) {
        String messageTemplate = "Metric \\'%s\\' does not support granularity \\'%s\\'. Must be one of: %s";

        return String.format(messageTemplate, metric.name().toLowerCase(), unsupported.getValue(),
                StringUtils.join(metric.getGranularities(), ", "));
    }

    private static String buildMessage(String invalidName, Collection<StatGranularity> supportedList) {
        String messageTemplate = "Granularity \\'%s\\' is not supported. Must be one of: %s";

        return String.format(messageTemplate, invalidName, StringUtils.join(supportedList, ", "));
    }
}
