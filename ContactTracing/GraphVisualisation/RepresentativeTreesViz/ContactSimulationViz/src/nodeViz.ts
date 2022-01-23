import { getMetaDataValueFromId, getMetaDataValuesFromRepTrees } from './dataQueries';
import { getIndexInColorScheme } from './ColorSchemes';
import { vars } from './vizVariables';
import * as d3 from 'd3';


export function initNodeGlyph(gElement: d3.Selection<d3.BaseType, unknown, null, undefined>) {
    for (let partI = 0; partI < vars.maxParts; partI++) {

        let [leftStartX, leftRectWidth] = getRectGlyphXPositions(true)
        gElement.append("rect")
            .attr("x", leftStartX)
            .attr("width", leftRectWidth)
            .attr("class", "glyphRectangle leftRectNumber" + partI)

        let [rightStartX, rightRectWidth] = getRectGlyphXPositions(false)
        gElement.append("rect")
            .attr("x", rightStartX)
            .attr("width", rightRectWidth)
            .attr("class", "glyphRectangle rightRectNumber" + partI);
    }
}

export function updateNodeGlyph(treeSvg: d3.Selection<d3.BaseType, unknown, HTMLElement, undefined>) {
    const gElements = treeSvg
        .selectAll(".node")
        .selectAll("g");

    gElements.each(function () {
        const nodeId = parseInt(d3.select(this).attr("id"));

        for (let partI = 0; partI < vars.maxParts; partI++) {
            const leftRectI = d3.select(this).select(".leftRectNumber" + partI);
            updateRect(leftRectI, partI, nodeId, true, true);

            const rightRectI = d3.select(this).select(".rightRectNumber" + partI);
            updateRect(rightRectI, partI, nodeId, true, false);
        }
    });
}

function updateRect(rect: d3.Selection<d3.BaseType, unknown, null, undefined>, partIndex: number, nodeId: number, isRepTree: boolean, isLeftRect: boolean) {
    const color = getPartColor(partIndex, isLeftRect);
    const [y, height] = getRectGlyphYPositions(nodeId, partIndex, isRepTree, isLeftRect);
    rect.attr("y", y)
        .attr("height", height)
        .attr("fill", color)

}

export function getPartColor(index: number, isLeftChart: boolean) {
    if (isLeftChart) {
        return vars.currentLeftColorScheme[index];
    } else {
        return vars.currentRightColorScheme[index];
    }
}


function getRectGlyphXPositions(isLeftChart: boolean) {
    let startX = getStartX(isLeftChart);
    let rectWidth = vars.nodeBaseSize;

    return [startX, rectWidth];
}


function getRectGlyphYPositions(id: number, partIndex: number, isRepTree: boolean, isLeftChart: boolean) {

    const partRange = getPartPercentages(id, partIndex, isRepTree, isLeftChart);
    const rectSize = vars.nodeBaseSize * 2; //nodeBaseSize is radius

    const y1 = partRange[0] * rectSize - rectSize / 2;
    const y2 = partRange[1] * rectSize - rectSize / 2;
    const rectHeight = y2 - y1;

    return [y1, rectHeight];
}


function getStartX(isLeftChart: boolean) {
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
function getPartPercentages(id: number, partIndex: number, isRepTree: boolean, isLeftChart: boolean) {
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



export function getPartCounts(id: number, isRepTree: boolean, isLeftChart: boolean) {
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
        //get value of all nodes represented by this id
        values = getMetaDataValuesFromRepTrees(attributeName, id, vars.currentEditDistance, vars.locationToVisualize, vars.startDate, vars.endDate);
    } else {
        values = [getMetaDataValueFromId(attributeName, id)]; //put into arrow for consistency
    }


    for (let val of values) {
        const index = getIndexInColorScheme(val, colorSchemeType, colorSchemeValues)
        partCounts[index]++;
    }
    return partCounts;
}