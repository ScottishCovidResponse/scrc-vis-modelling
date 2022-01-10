import { getMetaDataValueFromId, getMetaDataValuesFromRepTrees} from './dataQueries';
import { getIndexInColorScheme } from './ColorSchemes';
import {
    // currentLeftColorScheme, currentRightColorScheme,
    // currentLeftColorSchemeValues, currentRightColorSchemeValues,
    // currentLeftAttributeName, currentRightAttributeName,
    // currentLeftAttributeType, currentRightAttributeType, maxParts
    vars
} from './vizVariables';
import * as d3 from 'd3';



/**
 * Creates the stacked chart glyph for each node
 * @param {*} gElement 
 * @param {*} nodeId 
 * @param {*} isRepTree 
 */
export function makeNodeGlyph(gElement, nodeId, isRepTree) {
    //make left chart
    makeStackedChart(gElement, nodeId, isRepTree, true);

    //make right chart
    makeStackedChart(gElement, nodeId, isRepTree, false);
}

function makeStackedChart(gElement, nodeId, isRepTree, isLeftChart) {
    let [startX, rectWidth] = getRectGlyphXPositions(isLeftChart)

    for (let partI = 0; partI < vars.maxParts; partI++) {
        constructRect(gElement, nodeId, isRepTree, isLeftChart, partI, startX, rectWidth);
    }
}


function constructRect(gElement, nodeId, isRepTree, isLeftChart, partIndex, startX, rectWidth) {

    const color = getPartColor(partIndex, isLeftChart);
    const [y, height] = getRectGlyphYPositions(nodeId, partIndex, isRepTree, isLeftChart);

    if (height > 0) { //only add rectangles that have a height
        gElement.append("rect")
            .attr("x", startX)
            .attr("y", y)
            .attr("width", rectWidth)
            .attr("height", height)
            .attr("fill", color)
            .attr("class", "glyphRectangle")
    }
}

export function updateNodeGlyphs(isRepTree) {
    const gElements = d3.select("#treeGrid") //do not animate these. d3 animations break down at around 20000 svg elements. The largest tree alone has 100 nodes with 10 parts each.
        .selectAll(".svgtree.visible")
        .selectAll(".node")
        .selectAll("g");

    gElements.selectAll("*").remove(); //remove all rectangles so we can add only those that are needed again

    gElements.each(function () {
        const nodeId = parseInt(d3.select(this).attr("id"));
        makeNodeGlyph(d3.select(this), nodeId, isRepTree)
    });
}

export function getPartColor(index, isLeftChart) {
    if (isLeftChart) {
        return vars.currentLeftColorScheme[index];
    } else {
        return vars.currentRightColorScheme[index];
    }
}


function getRectGlyphXPositions(isLeftChart) {
    let startX = getStartX(isLeftChart);
    let rectWidth = vars.nodeBaseSize;

    return [startX, rectWidth];
}


function getRectGlyphYPositions(id, partIndex, isRepTree, isLeftChart) {

    const partRange = getPartPercentages(id, partIndex, isRepTree, isLeftChart);
    const rectSize = vars.nodeBaseSize * 2; //nodeBaseSize is radius

    const y1 = partRange[0] * rectSize - rectSize / 2;
    const y2 = partRange[1] * rectSize - rectSize / 2;
    const rectHeight = y2 - y1;

    return [y1, rectHeight];
}


function isRectIndexFromLeftChart(rectIndex) {
    return rectIndex < vars.maxParts;
}




function getStartX(isLeftChart) {
    if (isLeftChart) {
        return -vars.nodeBaseSize;
    } else {
        return 0;
    }
}



/**
 * returns [startPercentage,endPercentage] that indicates how much of the value this part has. 
 * @param {*} id 
 * @param {*} partIndex 
 * @param {*} isRepTree 
 * @param {*} isLeftChart
 * @returns 
 */
function getPartPercentages(id, partIndex, isRepTree, isLeftChart) {
    const counts = getPartCounts(id, isRepTree, isLeftChart);

    let startValue = 0; //value of all parts up to index {partIndex}
    let sum = 0;
    for (let i = 0; i < counts.length; i++) {
        sum += counts[i];
        if (i < partIndex) {
            startValue += counts[i];
        }
    }

    if (sum == 0) {
        console.log("Shouldn't happen. Something went wrong in data reading/parsing")
        return [0, 0];
    }

    const startPercentage = startValue / sum;

    const value = counts[partIndex];
    const endPercentage = (startValue + value) / sum;

    return [startPercentage, endPercentage];
}



export function getPartCounts(id, isRepTree, isLeftChart) {
    let partCounts = new Array(vars.maxParts).fill(0); //array length equal to amount of parts. Fill them in one by one



    let colorSchemeType;
    let colorSchemeValues;
    let attributeName;
    if (isLeftChart) {
        colorSchemeType = vars.currentLeftAttributeType;
        colorSchemeValues = vars.currentLeftColorSchemeValues;
        attributeName = vars.currentLeftAttributeName;
    } else {
        colorSchemeType = vars.currentRightAttributeType;
        colorSchemeValues = vars.currentRightColorSchemeValues;
        attributeName = vars.currentRightAttributeName;
    }


    let values;
    if (isRepTree) {
        //get value of all nodes represented by this idea
        values = getMetaDataValuesFromRepTrees(attributeName, id, vars.currentEditDistance);
    } else {
        values = [getMetaDataValueFromId(attributeName, id)]; //put into arrow for consistency
    }


    for (let val of values) {
        const index = getIndexInColorScheme(val, colorSchemeType, colorSchemeValues)
        partCounts[index]++;
    }
    return partCounts;
}