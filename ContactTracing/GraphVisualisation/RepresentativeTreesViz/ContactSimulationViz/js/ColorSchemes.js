/**
 * Returns a [colorScheme,colorSchemeValues] pair. 
 * colorScheme: the {maxParts} colors we are using in order.
 * colorSchemeValue: The values used to determine which color to pick. For integers, these holds the upper bounds of the bin partitions. For categorical, these holds the 9 most frequent value names and "other" in the last bin
 * @param {*} varType the type of the attribute we are coloring
 * @param {*} values An array of all the values for the attribute we are visualizing
 */
function getColorScheme(varType, values) {
    let colorScheme;
    let colorSchemeValues;

    if (varType == "categorical") {
        //color scheme is defaulted
        colorScheme = categoricalColorScheme;

        //get which string goes at which position
        let topKValues = getTopKValues(values, 9);
        colorSchemeValues = topKValues;
        //anything not in the top 9, gets shoved into the "other" cateogry
        colorSchemeValues[9] = "Other";
    } else if (varType == "None") {
        colorScheme = noneColorScheme;
        colorSchemeValues = ["none"];
    } else {
        console.error("Variable type " + varType + " is not implemented yet")
    }

    return [colorScheme, colorSchemeValues];
}

function getIndexInColorScheme(value, attributeType, colorSchemeValues) {
    if (attributeType == "categorical") {
        const index = colorSchemeValues.indexOf(value);
        if (index == -1) {
            return 9;
        } else {
            return index;
        }
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



function getPartColorSimple() {
    return "#005a32";
}