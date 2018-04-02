var ISO_DATE_FORMAT = 'YYYY-MM-DDTHH:mm:ss.SSSZ';

var PREFERRED_SPLITS = [6, 5, 4, 7, 3, 8, 2, 9, 10];

var WEEKDAYS = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];

var chartColors = ['#3d92d4', '#ffc107', '#48c0d4', '#45aef9', '#f28595'];

var loading = {
    text: '',
    color: 'rgba(70, 179, 255, 0.54)',
    textColor: '#999',
    maskColor: 'rgba(255, 255, 255, 0.8)'
};

var xAxisBase = {
    type: 'category',
    axisTick: {
        show: false
    },
    axisLabel: {
        interval: 6,
        formatter: function (value) {
            return moment(value).format('MM/DD');
        }
    },
    axisLine: {
        lineStyle: {
            color: '#aaa'
        }
    },
    splitLine: {
        lineStyle: {
            color: '#e8e8e8'
        }
    },
    axisPointer: {
        type: 'line',
        lineStyle: {
            type: 'dashed'
        },
        label: {
            formatter: function(params) {
                return moment(params.value).format('MM/DD HH:mm');
            }
        }
    }
};

var yAxisBase = {
    type: 'value',
    scale: true,
    axisLine: {
        show: false
    },
    axisTick: {
        show: false
    },
    splitLine: {
        lineStyle: {
            color: '#e8e8e8'
        }
    }
};

var seriesBarBase = {
    type: 'bar',
    barWidth: '70%',
    encode: {
        x: 'time'
    },
    animationEasing: 'backOut',
    animationDuration: 500,
    animationDelay: function(id) {
        return id * 20;
    }
};

var seriesBarBaseHourly = $.extend({}, seriesBarBase, {
    animationDuration: 500,
    animationDelay: function(id) {
        return id * 10;
    }
});

var seriesLineBase = {
    type: 'line',
    encode: {
        x: 'time'
    }
};

var option = {
    color: chartColors,
    textStyle: {
        fontSize: 11,
        color: '#666'
    },
    grid: {
        containLabel: true,
        left: 0,
        right: 0,
        top: 8,
        bottom: 5
    },
    tooltip: {
        trigger: 'axis',
        axisPointer: {
            type: 'cross',
            crossStyle: {
                color: '#ccc'
            }
        },
        textStyle: {
            fontSize: 12
        }
    },
    legend: {
        show: false
    },
    xAxis: [
        xAxisBase
    ],
    yAxis: [
        yAxisBase,
        yAxisBase
    ]
};

function floatToPercentage(value) {
    return (Math.round(value * 1000)) / 10 + '%';
}

function weekdayIndexToName(index) {
    return WEEKDAYS[index - 1];
}

function rangeSplit(data, indexes, minIntervals, scale = [true, true]) {
    var results = [];

    data.forEach(function (row) {
        indexes.forEach(function(index, indexId) {

            var minIndex, maxIndex;
            if (index instanceof Array) {
                minIndex = index[0];
                maxIndex = index[1];
            } else {
                minIndex = index;
                maxIndex = index;
            }

            var result = results[indexId];
            if (undefined === result) {
                result = {min: Number.MAX_SAFE_INTEGER, max: 0, interval: minIntervals[indexId]};
                results.push(result);
            }

            if (row[minIndex] != null && row[minIndex] < result.min) {
                result.min = scale[indexId] ? row[minIndex] : 0;
            }

            if (row[maxIndex] != null && row[maxIndex] > result.max) {
                result.max = row[maxIndex];
            }
        });
    });

    var minimumReached = 0;

    results.forEach(function(result, rId) {
        if (result.min == Number.MAX_SAFE_INTEGER) {
            result.min = 0;
        }

        if (result.interval >= result.max - result.min) {
            result.interval = result.max - result.min;
            results[rId] = result;
            minimumReached++;
        }
    });

    if (minimumReached == indexes.length) {
        return results;
    }

    var splitPools = [];
    var solutionRatings = {};

    results.forEach(function (result) {
        var interval = result.interval;

        var splitPool = {};
        for (; interval < result.max - result.min; interval += result.interval) {
            var lowerBound = Math.floor(result.min / interval) * interval;
            var upperBound = Math.ceil(result.max / interval) * interval;
            var diff = upperBound - lowerBound;

            for (var subInterval = result.interval; subInterval < diff; subInterval += result.interval) {

                if (0 == diff % subInterval) {

                    var split = diff / subInterval;
                    var solutionsPerSplit = splitPool[split];
                    var solution = {min: lowerBound, max: upperBound, interval: subInterval};
                    if (undefined == solutionsPerSplit) {
                        if (undefined == solutionRatings[split]) {
                            solutionRatings[split] = 1;
                        } else {
                            solutionRatings[split]++;
                        }

                        splitPool[split] = solution;
                    } else if (result.min > 0) {
                        if (subInterval < solutionsPerSplit.interval) {
                            splitPool[split] = solution;
                        }
                    } else {
                        if (0 != solutionsPerSplit.max % solutionsPerSplit.interval) {
                            if (0 == upperBound % subInterval || subInterval < solutionsPerSplit.interval) {
                                splitPool[split] = solution;
                            }
                        } else if (0 == upperBound % subInterval && subInterval < solutionsPerSplit.interval) {
                            splitPool[split] = solution;
                        }
                    }
                }
            }
        }

        splitPools.push(splitPool);
    });

    var splitSolution = 0;

    for (var i = 0; i < PREFERRED_SPLITS.length; i++) {
        if (solutionRatings[PREFERRED_SPLITS[i]] == indexes.length) {
            splitSolution = PREFERRED_SPLITS[i];
            break;
        }
    }

    if (splitSolution > 0) {
        results.forEach(function(result, rId) {
            results[rId] = splitPools[rId][splitSolution];
        });
    } else {
        splitPools.forEach(function(pool, rId) {
            for (i = 0; i < PREFERRED_SPLITS.length; i++) {
                if (pool[PREFERRED_SPLITS[i]]) {
                    results[rId] = pool[PREFERRED_SPLITS[i]];
                    break;
                }
            }
        });
    }

    return results;
}

/**
 *
 * @param data Object
 * @param gran string
 * @param since long
 * @param until long
 */
function populateDateForEmptyDates(data, gran, since, until) {
    var earliest = until;
    var gap = 3600000;
    if ('daily' === gran) {
        gap = 86400000;
    }
    if (data.data.length > 0) {
        var countDim = data.dimensions.length;
        earliest = data.data[0][0];

        while (earliest - since > gap) {
            earliest -= gap;
            var filler = [earliest];
            for (var i = 1; i < countDim; i++) {
                filler.push(null);
            }
            data.data.splice(0, 0, filler);
        }
    }
    return data;
}
