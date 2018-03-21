var ISO_DATE_FORMAT = 'YYYY-MM-DDTHH:mm:ss.SSSZ';

function retrieveUser() {
    $.getJSON("/api/users/me/profiles", function(data) {
        var profile = data.profiles[0];

        $(".data-userFullName").text(profile.igProfile.name);
    });
}

var PREFERRED_SPLITS = [6, 5, 4, 7, 3, 8, 2, 9, 10];

function rangeSplit(data, indexes, minIntervals, scale = [true, true]) {
    var results = [];

    data.forEach(function(row, rowId) {
        indexes.forEach(function(index, indexId) {
            var result = results[indexId];
            if (undefined === result) {
                result = {min: Number.MAX_SAFE_INTEGER, max: 0, interval: minIntervals[indexId]};
                results.push(result);
            }

            if (row[index] != null && row[index] < result.min) {
                result.min = scale[indexId] ? row[index] : 0;
            }

            if (row[index] != null && row[index] > result.max) {
                result.max = row[index];
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

    results.forEach(function(result, rId) {
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