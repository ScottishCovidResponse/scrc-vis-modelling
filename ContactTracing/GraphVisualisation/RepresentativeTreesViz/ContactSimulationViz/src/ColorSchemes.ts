import { vars } from './vizVariables';

export let categoricalColorScheme = ["#a6cee3", "#1f78b4", "#b2df8a", "#33a02c", "#fb9a99", "#e31a1c", "#fdbf6f"];
export let integerColorScheme = ["#feedde", "#fdd0a2", "#fdae6b", "#fd8d3c", "#f16913", "#d94801", "#8c2d04"]
export let noneColorScheme = ["#888888"];

/**
 * Returns a [colorScheme,colorSchemeValues] pair. 
 * colorScheme: the {maxParts} colors we are using in order.
 * colorSchemeValue: The values used to determine which color to pick. For integers, these holds the upper bounds of the bin partitions. For categorical, these holds the 9 most frequent value names and "other" in the last bin
 * @param {*} varType the type of the attribute we are coloring
 * @param {*} values An array of all the values for the attribute we are visualizing
 */
export function getColorScheme(varType, values) {
    let colorScheme;
    let colorSchemeValues;


    if (varType == "categorical") {
        //color scheme is defaulted
        colorScheme = categoricalColorScheme;

        //get which string goes at which position
        let topKValues = getTopKValues(values, colorScheme.length - 1); //leave 1 color for the "other category"
        colorSchemeValues = topKValues;

        //make sure we only have colors for what we use
        let valueLength = topKValues.length
        colorScheme = colorScheme.slice(0, valueLength + 1)

        //anything not in the top values, gets shoved into the "other" cateogry
        colorSchemeValues[valueLength] = "Other";
    } else if (varType == "integer" || varType == "date") { //date is a unix timestamp
        colorScheme = integerColorScheme;

        //calculate the upper bounds for the bins based on the numbers
        let bins = calculateUpperboundsBins(values, integerColorScheme.length);
        colorSchemeValues = bins;

    } else if (varType == "None") {
        colorScheme = noneColorScheme;
        colorSchemeValues = ["none"];
    } else {
        console.error("Variable type " + varType + " is not implemented yet")
    }

    return [colorScheme, colorSchemeValues];
}

export function getIndexInColorScheme(value, attributeType, colorSchemeValues) {
    if (attributeType == "categorical") {
        const index = colorSchemeValues.indexOf(value);
        if (index == -1) {
            return vars.maxParts - 1; //can't find it, so "other" category
        } else {
            return index;
        }
    } else if (attributeType == "integer" || attributeType == "date") {
        //get the first value larger than x
        const index = colorSchemeValues.findIndex(x => x >= value)
        return index;
    } else if (attributeType == "None") { //single color
        return 0;
    }
    console.error("Type " + attributeType + " is not yet implemented");
    return -1;
}



function getTopKValues(values, k) {

    let frequencyMap = {};
    //count frequencies
    for (let val in values) {
        let name = values[val];
        if (frequencyMap[name]) {
            frequencyMap[name]++;
        } else {
            frequencyMap[name] = 1;
        }
    }
    //convert to array of arrays instead of dictionary so that we can sort
    //first value is key, second is frequency
    let frequencyArray = Object.entries(frequencyMap);
    frequencyArray.sort((a, b) => { return b[1] - a[1] });

    //get only the k most frequent values
    const kMostFrequent = frequencyArray.slice(0, k);

    //trim of the frequencies to get only the keys in an array
    const results1d = kMostFrequent.map(val => val[0])
    return results1d;
}

/**
 * 
 * @param {integer values} values 
 * @param {Amount of bins} binCount 
 */
function calculateUpperboundsBins(values, binCount) {

    let min = Number.MAX_VALUE;
    let max = Number.MIN_VALUE;
    for (let i = 0; i < values.length; i++) {
        min = Math.min(min, values[i]);
        max = Math.max(max, values[i]);
    }


    //calculate the interval
    let interval = (max - min) / binCount;

    //TODO: Better rounding. Need to make sure the topmost-node always has the same color

    //smoothen it so that we have a nice round 10's interval with the right amount of digits
    // const digits = getDigitCount(interval)
    // console.log(digits)
    // if (digits > 1) {
    //     const digitRounder = Math.pow(10, digits - 1);
    //     interval = Math.ceil(interval / digitRounder) * (digitRounder);
    // }


    let bins = [];

    for (let i = 0; i < binCount; i++) {
        bins[i] = Math.ceil(min + interval * (i + 1));
    }

    return bins;
}

function getDigitCount(number) {
    return Math.floor(number).toString().length;
}


function getPartColorSimple() {
    return "#005a32";
}