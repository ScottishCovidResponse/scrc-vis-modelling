import * as d3 from "d3";
import { metaData, repTreesData } from "./index";
import { getColorScheme } from "./ColorSchemes";
import { getAmountOfTreesRepresentedById, getMetaDataValues, metaDataFromNodeById } from "./dataQueries";
import { createScentedRtLineChart } from "./LineChart";
import { vars } from "./vizVariables";
import { updateColorLegend } from "./sidePanel";
import { createComponentBarChart } from "./BarChart";
import { removeAllPopups } from "./popup";
import { updateGridMapFromTrees } from "./GridMap";
import { updateTrees } from "./representativeGraph";

let recalculate = false; //Holds whether we need to recalculate the tree grid. Can happen in case of node size change or data change

export function setRecalculate() { recalculate = true }

/**
 * Updates the visualization without changing the layout of the trees
 */
export function updateSliderPreview() {
    updateScentWidget(vars.currentEditDistance)
}


export function updateAll() {
    let idsToHide = getIdsToHide(vars.currentEditDistance)
    hideTrees(idsToHide);


    updateSliderPreview();
    updateSidebarColors();

    updateTrees();


    updateGlobalChart();
    changeNoLongerPending();
    updateGridMapFromTrees(vars.startDate, vars.endDate);
}


function updateSidebarColors() {
    updateColorSchemes()
    updateColorLegend(); //Make sure color legend is up to date
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

function hideTrees(idsToHide: number[]) {
    d3.select("#treeGrid")
        .selectAll(".divsvgtree")//@ts-ignore
        .attr("class", function () {
            const div = d3.select(this);
            const treeId = parseInt(div.attr('id').substring(3));
            //substring 3 as id is "tidXXX" where XXX is a number
            if (idsToHide.includes(treeId)) {
                div.classed("visible", false)
            } else {
                div.classed("visible", true)
            }
        })
}



function updateScentWidget(distance: number) {
    //delete old
    d3.select("#RtScentedChart").remove();
    //make new
    createScentedRtLineChart(d3.select("#DistanceSliderdiv"), distance, repTreesData);
}


/**
 * Hide either when they are not represnted at the edit distance or if they have no nodes with the right location
 * @param editDistance 
 * @returns 
 */
function getIdsToHide(editDistance: number) {

    let idsToHide = [];
    for (let i = 0; i < repTreesData.length; i++) {
        const repData = repTreesData[i];
        let id = repData.id;

        // //quick determine if we can hide this tree by edit distance along
        if (repData.maxEditDistance < editDistance) {
            idsToHide.push(id);
            continue;
        }


        //only show trees in the represented range
        // const metaData = metaDataFromNodeById.get(id);
        // if ( metaData.positiveTestTime < vars.startDate  || metaData.positiveTestTime > vars.endDate) {
        //     idsToHide.push(id);
        //     // console.log(vars.startDate)
        //     // console.log(vars.endDate)
        //     // console.log(metaData.positiveTestTime);
        //     continue;
        // }


        //repIData.editDistance <= editDistance
        //These are represnted

        //trees always represent themselves
        if (getAmountOfTreesRepresentedById(id, editDistance, vars.locationToVisualize, vars.startDate, vars.endDate) == 0) {
            idsToHide.push(id);
            continue;
        }

    }
    console.log("TODO: Hide complete trees that are not in the right range")
    return idsToHide;
}