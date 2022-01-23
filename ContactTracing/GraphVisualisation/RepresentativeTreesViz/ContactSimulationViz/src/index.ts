import { preprocessData } from "./dataQueries"
import * as d3 from 'd3';
import { initGridMap, updateGridMapFromTrees } from './GridMap';
import { createSidePanel } from './sidePanel';
import { initTreeGrid } from "./representativeGraph";
import { updateAll} from "./updateFunctions";
import { noneColorScheme } from './ColorSchemes';
import { vars } from "./vizVariables";


const repTreesDataInputLocation = "../data/TTPData/RepTrees.json";
const allTreesDataInputLocation = "../data/TTPData/AllTrees.json";
const metaDataInputLocation = "../data/TTPData/NodesAndMeta.json";
const gridNamesInputLocation = "../data/TTPData/WalesGridmapCoordinates.csv";





//Policies
export let policyDataPresent = false; //Whether policy data is present

//End policies





export let repTreesData, allTreesData, metaData, gridNames;


//load in all the data
d3.json(repTreesDataInputLocation).then(function (repTreesDataInput) {
    d3.json(allTreesDataInputLocation).then(function (allTreesDataInput) {
        d3.json(metaDataInputLocation).then(function (metaDataInput) {
            d3.text(gridNamesInputLocation).then(function (gridNamesInput) {
                repTreesData = repTreesDataInput;
                allTreesData = allTreesDataInput;
                metaData = metaDataInput;

                //Not the way this should be done, but breaks cyclic dependency. Needs to be refactored properly
                vars.currentLeftColorScheme = noneColorScheme;
                vars.currentRightColorScheme = noneColorScheme;

                preprocessData(repTreesData, allTreesData, metaData);

                gridNames = d3.csvParseRows(gridNamesInput)

                if ("policies" in metaData[0]) {
                    policyDataPresent = true;
                } else {
                    console.log("Policies are disabled. Need to be tested and likely reimplemented")
                }


                mainRepresentativeGraph();
                updateAll(); //update to use slider values


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