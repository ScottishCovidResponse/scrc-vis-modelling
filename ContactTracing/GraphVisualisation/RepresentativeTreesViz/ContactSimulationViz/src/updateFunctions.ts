import * as d3 from "d3";
import { metaData, repTreesData } from "./index";
import { getColorScheme } from "./ColorSchemes";
import { getAmountOfTreesRepresentedById, getMetaDataValues, metaDataFromNodeById } from "./dataQueries";
import { createScentedRtLineChart } from "./LineChart";
import { vars } from "./vizVariables";
import { updateColorLegend } from "./sidePanel";
import { generateTreeGrid, resizeSVG, treeBaseHeightById, treeBaseWidthById, treeOrder } from "./representativeGraph";
import { createComponentBarChart } from "./BarChart";
import { updateNodeGlyphs } from "./nodeViz";
import { removeAllPopups } from "./popup";
import { getScaleFactorByRepAmount } from "./treeLayout";
import { calculateOffsets } from "./offsetCalculator";

let recalculate = false; //Holds whether we need to recalculate the tree grid. Can happen in case of node size change or data change

export function setRecalculate() { recalculate = true }

/**
 * Updates the visualization without changing the layout of the trees
 */
export function updateSliderPreview() {
    updateRepresentationText();

    updateScentWidget(vars.currentEditDistance)
}


export function updateAll() {
    let idsToHide = getIdsToHide(vars.currentEditDistance)
    hideTrees(idsToHide);


    updateSliderPreview();
    updateColors();
    if (recalculate) { //if we need to reinitialize the grid
        d3.select("#treeGrid").selectAll("*").remove();
        generateTreeGrid(repTreesData);
        //update the position without animating as we are redrawing the tree
        updatePositions(false);
    } else {
        //only moving and recoloring. Update via position
        updatePositions(true);
    }
    //Update the positions as well as ensure the right trees are hiden.



    updateGlobalChart();
    changeNoLongerPending();
}


function updateColors() {
    updateColorSchemes()
    updateColorLegend(); //Make sure color legend is up to date
    updateNodeGlyphs(true); //update the glyphs for the visible trees. 
}

function updateColorSchemes() {

    //Need to get the values as this determines which bins we have for the colors
    let leftValues = getMetaDataValues(vars.currentLeftAttributeName, metaData);
    [vars.currentLeftColorScheme, vars.currentLeftColorSchemeValues] = getColorScheme(vars.currentLeftAttributeType, leftValues)

    if (vars.currentLeftAttributeType == "integer" || vars.currentLeftAttributeType == "date") {
        //set the bounds for the bins
        let minLeftVal = Number.MAX_VALUE;
        let maxLeftVal = Number.MIN_VALUE;
        for (let val of leftValues) {
            minLeftVal = Math.min(minLeftVal, val);
            maxLeftVal = Math.max(maxLeftVal, val);
        }
        vars.currentLeftAttributeBounds = [minLeftVal, maxLeftVal];
    }

    let rightValues = getMetaDataValues(vars.currentRightAttributeName, metaData);
    [vars.currentRightColorScheme, vars.currentRightColorSchemeValues] = getColorScheme(vars.currentRightAttributeType, rightValues)

    if (vars.currentRightAttributeType == "integer" || vars.currentRightAttributeType == "date") {
        //set the bounds for the bins
        let minRightVal = Number.MAX_VALUE;
        let maxRightVal = Number.MIN_VALUE;
        for (let val of rightValues) {
            minRightVal = Math.min(minRightVal, val);
            maxRightVal = Math.max(maxRightVal, val);
        }
        vars.currentRightAttributeBounds = [minRightVal, maxRightVal];
    }
}

export function updatePositions(animate = true) {
    removeAllPopups(); //remove all popups as we are changing the layout and possibly hiding trees/nodes
    let idsToHide = getIdsToHide(vars.currentEditDistance);

    updateTreesAnimated(idsToHide, animate);
}

export function updateGlobalChart() {
    //TODO: Not optimized at all, but works
    const distributionDiv = d3.select("#sidePanel").select("#distributionChartPanel");
    distributionDiv.select(".barChartsContainer").remove()

    createComponentBarChart(distributionDiv);
}


function changeNoLongerPending() {
    const recalcButton = d3.select("#sidePanel").select(".recalculateDiv").select("#recalculateButton")
    recalcButton.classed("disabled", true)
}

export function changePending() {
    const recalcButton = d3.select("#sidePanel").select(".recalculateDiv").select("#recalculateButton")
    recalcButton.classed("disabled", false)
}

function hideTrees(idsToHide) {
    d3.select("#treeGrid")
        .selectAll(".svgtree")
        .attr("class", function () {
            const treeId = parseInt(d3.select(this).attr('id').substring(3));
            //substring 3 as id is "tidXXX" where XXX is a number
            if (idsToHide.includes(treeId)) {
                return "svgtree hidden"; //todo: Only change hidden of visible without needing to specify the rest
            } else {
                return "svgtree visible";
            }
        })
}

function updateRepresentationText() {
    d3.select("#treeGrid")
        .selectAll(".svgtree")
        .selectAll(".textG")
        .select("text")
        .text(function () {
            const treeId = parseInt(d3.select(this).node().parentNode.parentNode.getAttribute("id").substring(3))
            const repAmount = getAmountOfTreesRepresentedById(treeId, vars.currentEditDistance, vars.locationToVisualize);
            return repAmount;
        })
}


function updateScentWidget(distance) {
    //delete old
    d3.select("#RtScentedChart").remove();
    //make new
    createScentedRtLineChart(d3.select("#DistanceSliderdiv"), distance, repTreesData);
}

/**
 * Animates the changes in the trees
 * @param {*} idsToHide 
 * @param {if false, no animation will be used} animate 
 */
function updateTreesAnimated(idsToHide, animate = true) {
    const updatedPlacement = recalculatePlacement(idsToHide);
    const newWidths = updatedPlacement[0];
    const newHeights = updatedPlacement[1];
    const offSets = updatedPlacement[2]


    let transitionTime = 1000;
    if (animate == false) {
        transitionTime = 0;
    }

    animateChanges(newWidths, newHeights, offSets, transitionTime)
}

function recalculatePlacement(idsToHide) {

    //get the new widths and offsets
    const newWidths = [];
    const newHeights = [];
    const newHorizontalMargins = [];

    for (let i = 0; i < treeOrder.length; i++) {
        const id = treeOrder[i];

        const repAmount = getAmountOfTreesRepresentedById(id, vars.currentEditDistance, vars.locationToVisualize);

        let width = treeBaseWidthById.get(id); //get base width
        let height = treeBaseHeightById.get(id); //get base height
        let horizontalMargin = vars.horizontalMarginBetweenTrees;

        let scaleFactor; //how much to scale the trees by
        if (idsToHide.includes(id)) { //tree will be hidden, shrink it
            scaleFactor = vars.hiddenTreesScalingFactor;
            horizontalMargin = horizontalMargin * scaleFactor; //shrink margin only if hidden
        } else { //otherwise scale tree
            //hidden trees cannot have a repAmount more than 1
            //scale trees according to repamount
            scaleFactor = getScaleFactorByRepAmount(repAmount);
        }
        width = width * scaleFactor;
        height = height * scaleFactor;



        newWidths[i] = width;
        newHeights[i] = height;
        newHorizontalMargins[i] = horizontalMargin;
    }

    const div = d3.select("#treeGridDiv");
    let targetContainerWidth = div.node().clientWidth;


    const offSets = calculateOffsets(newWidths, newHeights, newHorizontalMargins, targetContainerWidth);

    return [newWidths, newHeights, offSets];
}


function animateChanges(widthArray, heightArray, offsetArray, transitionTime) {

    const widthMap = new Map();
    const heightMap = new Map();
    const xOffsetMap = new Map();
    const yOffsetMap = new Map();


    for (let i = 0; i < treeOrder.length; i++) {
        const id = treeOrder[i];
        xOffsetMap.set(id, offsetArray[i][0]);
        yOffsetMap.set(id, offsetArray[i][1]);
        widthMap.set(id, widthArray[i]);
        heightMap.set(id, heightArray[i]);
    }


    //Note, d3 using svg is not fast enough to animate the amount of elements we are using. 
    //TODO: Trim down glyphs to only contains parts that exist. Needs a refactor. If we want to animate glyps, it needs a different technology (canvas?)

    d3.select("#treeGrid")
        .interrupt()
        .transition()
        .duration(transitionTime)
        .selectAll("svg")
        .attr("width", function () { //update the width. Can increase or decrease.
            const treeId = parseInt(d3.select(this).attr('id').substring(3))
            const width = widthMap.get(treeId);
            return width;
        })
        .attr("height", function () { //update the height. Can increase or decrease.
            const treeId = parseInt(d3.select(this).attr('id').substring(3))
            const height = heightMap.get(treeId);
            return height;
        })
        .attr("x", function () { //update x
            const treeId = parseInt(d3.select(this).attr('id').substring(3))
            const xOffset = xOffsetMap.get(treeId);
            return xOffset;
        })
        .attr("y", function () { //update y
            const treeId = parseInt(d3.select(this).attr('id').substring(3))
            const yOffset = yOffsetMap.get(treeId);
            return yOffset;
        })
        .end()
        .then(() => {
            const svg = d3.select("#treeGrid");
            resizeSVG(svg);
        })
        .catch((error) => {
            // console.error(error);
            //ignore the error. Can come from it being interrupted in which
            //case there is no need to resize
        })

}



/**
 * Hide either when they are not represnted at the edit distance or if they have no nodes with the right location
 * @param editDistance 
 * @returns 
 */
function getIdsToHide(editDistance: number) {
    let idsToHide = [];
    for (let i = 0; i < repTreesData.length; i++) {
        let id = repTreesData[i].id;
        // //quick determine if we can hide this tree by edit distance along
        if (repTreesData[i].maxEditDistance < editDistance) {
            idsToHide.push(id);
            continue;
        }

         //repIData.editDistance <= editDistance
         //These are represnted

        //trees always represent themselves
        if (getAmountOfTreesRepresentedById(id, editDistance, vars.locationToVisualize) == 0) {
            idsToHide.push(id);
        }

    }
    return idsToHide;
}