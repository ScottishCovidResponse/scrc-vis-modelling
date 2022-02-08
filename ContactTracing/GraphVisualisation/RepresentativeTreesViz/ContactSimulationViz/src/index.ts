import { preprocessData } from "./dataQueries"
import * as d3 from 'd3';
import { initGridMap, updateGridMapFromTrees } from './GridMap';
import { createSidePanel } from './sidePanel';
import { initTreeGrid } from "./representativeGraph";
import { updateAll } from "./updateFunctions";
import { noneColorScheme } from './ColorSchemes';
import { vars } from "./vizVariables";
import { generateDensityPlot } from "./DensityPlot.ts";


const repTreesDataInputLocation = "../data/TTPData08-02Update/RepTrees.json";
const allTreesDataInputLocation = "../data/TTPData08-02Update/AllTrees.json";
const metaDataInputLocation = "../data/TTPData08-02Update/NodesAndMeta.json";
const gridNamesInputLocation = "../data/TTPData08-02Update/WalesGridmapCoordinates.csv";





//Policies
export let policyDataPresent = false; //Whether policy data is present

//End policies
export let repTreesData, allTreesData, metaData, gridNames: string[][];

console.log("Testing DensityPlot");

// const testDiv = d3.select("#treeGridDiv");
// generateDensityPlot(testDiv);

//load in all the data
d3.json(repTreesDataInputLocation).then(function (repTreesDataInput) {
    d3.json(allTreesDataInputLocation).then(function (allTreesDataInput) {
        d3.json(metaDataInputLocation).then(function (metaDataInput) {
            d3.text(gridNamesInputLocation).then(function (gridNamesInput) {
                repTreesData = repTreesDataInput;
                allTreesData = allTreesDataInput;
                metaData = metaDataInput;
                gridNames = d3.csvParseRows(gridNamesInput)


                //Shouldn't be neededm but keeping it in for now due to time-constraints
                console.log("Reptrees of maxeditdistance = 0 are included. Need to remove those from data in earlier phase.")
                let repTreesDataFiltered = [];
                for (let i = 0; i < repTreesData.length; i++) {
                    const repTree = repTreesData[i];
                    if (repTree.maxEditDistance > 0) {
                        // if (repTree.children.length != 0) { //Speedup option
                        repTreesDataFiltered.push(repTree);
                        // }
                    }
                }
                repTreesData = repTreesDataFiltered;


                //Not the way this should be done, but breaks cyclic dependency. Needs to be refactored properly
                vars.currentLeftColorScheme = noneColorScheme;
                vars.currentRightColorScheme = noneColorScheme;

                preprocessData(repTreesData, allTreesData, metaData);


                if ("policies" in metaData[0]) {
                    policyDataPresent = true;
                } else {
                    console.log("Policies are disabled. Need to be tested and likely reimplemented")
                }

                mainRepresentativeGraph();
                console.log("initial update")
                updateAll(); //update to use slider values

                console.log("Gridmap init")
                initGridMap(gridNames);
                updateGridMapFromTrees(vars.startDate, vars.endDate);
            });
        });
    });
});

function mainRepresentativeGraph() {
    createSidePanel(repTreesData);
    initTreeGrid(repTreesData);
}