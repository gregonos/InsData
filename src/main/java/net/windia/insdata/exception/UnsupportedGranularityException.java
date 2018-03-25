package net.windia.insdata.exception;

import net.windia.insdata.constants.IgMetric;
import net.windia.insdata.constants.StatGranularity;
import org.apache.commons.lang3.StringUtils;

import java.util.EnumSet;
import java.util.List;

public class UnsupportedGranularityException extends InsDataException {

    public UnsupportedGranularityException(String invalidName) {
        this(invalidName, EnumSet.allOf(StatGranularity.class).toArray(new StatGranularity[]{}));
    }

    public UnsupportedGranularityException(String invalidName, StatGranularity[] supportedList) {
        super(buildMessage(invalidName, supportedList));
    }

    public UnsupportedGranularityException(String invalidName, StatGranularity[] supportedList, Throwable cause) {
        super(buildMessage(invalidName, supportedList), cause);
    }

    public UnsupportedGranularityException(IgMetric metric, StatGranularity granInstance) {
        super(buildMessage(metric, granInstance));
    }

    private static String buildMessage(IgMetric metric, StatGranularity unsupported) {
        String messageTemplate = "Metric \\'%s\\' does not support granularity \\'%s\\'. Must be one of: %s";

        return String.format(messageTemplate, metric.getName(), unsupported.getValue(),
                StringUtils.join(metric.getGranularities(), ", "));
    }

    private static String buildMessage(String invalidName, StatGranularity[] supportedList) {
        String messageTemplate = "Granularity \\'%s\\' is not supported. Must be one of: %s";

        return String.format(messageTemplate, invalidName, StringUtils.join(supportedList, ", "));
    }
}
