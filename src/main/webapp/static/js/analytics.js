var ISO_DATE_FORMAT = 'YYYY-MM-DDTHH:mm:ss.SSSZ';

var PREFERRED_SPLITS = [6, 5, 4, 7, 3, 8, 2, 9, 10];

var SPLITS = 6;

var WEEKDAYS = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];

var chartColors = ['#3d92d4', '#ffc107', '#48c0d4', '#45aef9', '#f28595', '#c2def2', '#aaaaaa'];

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
    return (value * 100).toFixed(1) + '%';
}

function weekdayIndexToName(index) {
    return WEEKDAYS[index - 1];
}

function rangeSplit(data, indexes, scale = [true, true]) {
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
                result = {min: Number.MAX_SAFE_INTEGER, max: 0, interval: 0, scale: 1};
                results.push(result);
            }

            if (row[minIndex] != null && row[minIndex] < result.min) {
                result.min = scale[indexId] || row[minIndex] < 0 ? row[minIndex] : 0;
            }

            if (row[maxIndex] != null && row[maxIndex] > result.max) {
                result.max = row[maxIndex];
            }
        });
    });

    results.forEach(function(result, rId) {
        if (result.min == Number.MAX_SAFE_INTEGER) {
            result.min = 0;
        }

        // detect float numbers
        if (result.min % 1 !== 0 || result.max % 1 !== 0) {
            var decLen = Math.max(decimalLength(result.min), decimalLength(result.max));
            result.scale = Math.pow(10, decLen);
            result.min *= result.scale;
            result.max *= result.scale;
        }

        result.interval = findInterval(result.min, result.max, SPLITS);

        if (result.min == 0 || result.max == 0) {
            result.min = Math.floor(result.min / result.interval) * result.interval;
            result.max = Math.ceil(result.max / result.interval) * result.interval;
        } else if (result.min < 0 && result.max > 0) {
            var diff = result.max - result.min;
            result.min = Math.round(result.min / diff * SPLITS) * result.interval;
            result.max = Math.round(result.max / diff * SPLITS) * result.interval;
        } else {
            result.min = Math.floor(result.min / result.interval * 5) * result.interval / 5;
            // decLen = decimalLength(result.interval);
            // if (decLen > 0) {
            //     result.min = parseFloat(result.min.toFixed(decLen));
            // }
            result.max = result.min + result.interval * SPLITS;
        }

        result.interval /= result.scale;
        result.min /= result.scale;
        result.max /= result.scale;

        results[rId] = result;
    });
    //
    // var splitPools = [];
    // var solutionRatings = {};
    //
    // results.forEach(function (result) {
    //     var interval = result.interval;
    //
    //     var splitPool = {};
    //     for (; interval < result.max - result.min; interval += result.interval) {
    //         var lowerBound = Math.floor(result.min / interval) * interval;
    //         var upperBound = Math.ceil(result.max / interval) * interval;
    //         var diff = upperBound - lowerBound;
    //
    //         for (var subInterval = result.interval; subInterval < diff; subInterval += result.interval) {
    //
    //             if (0 == diff % subInterval || diff % subInterval < 1e-10) {
    //
    //                 var split = diff / subInterval;
    //                 var solutionsPerSplit = splitPool[split];
    //                 var solution = {min: lowerBound, max: upperBound, interval: subInterval, scale: result.scale};
    //                 if (undefined == solutionsPerSplit) {
    //                     if (undefined == solutionRatings[split]) {
    //                         solutionRatings[split] = 1;
    //                     } else {
    //                         solutionRatings[split]++;
    //                     }
    //
    //                     splitPool[split] = solution;
    //                 } else if (result.min > 0) {
    //                     if (subInterval < solutionsPerSplit.interval) {
    //                         splitPool[split] = solution;
    //                     }
    //                 } else {
    //                     if (0 != solutionsPerSplit.max % solutionsPerSplit.interval) {
    //                         if (0 == upperBound % subInterval || subInterval < solutionsPerSplit.interval) {
    //                             splitPool[split] = solution;
    //                         }
    //                     } else if (0 == upperBound % subInterval && subInterval < solutionsPerSplit.interval) {
    //                         splitPool[split] = solution;
    //                     }
    //                 }
    //                 if (solution.scale > 1) {
    //                     solution.min = solution.min / solution.scale;
    //                     solution.max = solution.max / solution.scale;
    //                     solution.interval = solution.interval / solution.scale;
    //                 }
    //             }
    //         }
    //     }
    //
    //     splitPools.push(splitPool);
    // });
    //
    // var splitSolution = 0;
    //
    // for (var i = 0; i < PREFERRED_SPLITS.length; i++) {
    //     if (solutionRatings[PREFERRED_SPLITS[i]] == indexes.length) {
    //         splitSolution = PREFERRED_SPLITS[i];
    //         break;
    //     }
    // }
    //
    // if (splitSolution > 0) {
    //     results.forEach(function(result, rId) {
    //         results[rId] = splitPools[rId][splitSolution];
    //     });
    // } else {
    //     splitPools.forEach(function(pool, rId) {
    //         for (i = 0; i < PREFERRED_SPLITS.length; i++) {
    //             if (pool[PREFERRED_SPLITS[i]]) {
    //                 results[rId] = pool[PREFERRED_SPLITS[i]];
    //                 break;
    //             }
    //         }
    //     });
    // }

    return results;
}

function decimalLength(f) {
    return f % 1 === 0 ? 0 : f.toString().split('.')[1].length;
}

function findInterval(min, max, splits) {
    if (min == max) {
        return 0;
    }

    var rawInterval = (max - min) / splits;
    var scale = Math.pow(10, Math.floor(Math.log10(rawInterval)));

    var intervalStep = Math.ceil(rawInterval / scale);

    if (min % 1 !== 0 || max % 1 !== 0 || scale > 1) {
        for (; intervalStep / rawInterval * scale - 1 > 1 / (splits - 1); intervalStep -= 0.1) {
        }
    }

    intervalStep = parseFloat(intervalStep.toFixed(1));

    return intervalStep * scale;
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
    earliest = Math.ceil(earliest / gap) * gap;
    var countDim = data.dimensions.length;
    if (data.data.length > 0) {
        earliest = data.data[0][0];

    }
    while (earliest - since > gap) {
        earliest -= gap;
        var filler = [earliest];
        for (var i = 1; i < countDim; i++) {
            filler.push(null);
        }
        data.data.splice(0, 0, filler);
    }
    return data;
}
