package net.windia.insdata.exception;

import net.windia.insdata.constants.IgMetric;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class UnsupportedMetricException extends InsDataException {

    private static final String MESSAGE_TEMPLATE = "Metric(s) \\'%s\\' not supported. ";

    public UnsupportedMetricException(List<String> invalidMetricNames) {
        super(buildMessage(invalidMetricNames));
    }

    public UnsupportedMetricException(String metric) {
        super(buildMessage(metric));
    }

    private static String buildMessage(String metric) {
        return String.format(MESSAGE_TEMPLATE, metric);
    }

    private static String buildMessage(List<String> metrics) {
        return String.format(MESSAGE_TEMPLATE, StringUtils.join(metrics, ", "));
    }
}
