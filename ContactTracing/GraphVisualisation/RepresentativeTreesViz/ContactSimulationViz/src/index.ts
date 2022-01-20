import { preprocessData } from "./dataQueries"
import * as d3 from 'd3';
import { initGridMap, updateGridMapFromTrees } from './GridMap';
import { createSidePanel } from './sidePanel';
import { generateTreeGrid } from "./representativeGraph";
import { updateAll, updatePositions } from "./updateFunctions";
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
    generateTreeGrid(repTreesData);
    window.addEventListener('resize', function() { updatePositions() }, true);

}


// sliderValue = 1628985600;

// function createTimeSlider(selectorDiv) {

//     createSlider(selectorDiv, "TimeSlider", "time", 1628985600, 1635638400, sliderValue)

//     d3.select("#TimeSlider")
//         .on("input", function() {
//             updateSlider(this.value)
//         })

//     document.addEventListener('keypress', function(e) {
//         console.log("pressed");
//         sliderValue = parseInt(sliderValue) + 60 * 60 * 24;
//         updateSlider(sliderValue)
//     })

// }

// function updateSlider(value) {
//     sliderValue = value;

//     let selectedTime = parseInt(value); //keep the value up to date
//     updateGridMapFromTrees(selectedTime, selectedTime + (60 * 60 * 24 * 7)); //week time view

//     console.log(selectedTime)
//     let d = new Date(0);
//     d.setUTCSeconds(value)

//     let month = d.getMonth() + 1;
//     if (month.toString().length == 1) {
//         month = "0" + month;
//     }
//     let day = d.getDate();
//     if (day.toString().length == 1) {
//         day = "0" + day;
//     }
//     let year = d.getFullYear();

//     d3.select("#TimeSliderNumber").text(day + "." + month + "." + year);
//     d3.select("#TimeSlider").property("value", parseInt(sliderValue))
// }


// function createSlider(divToAppendTo, id, text, minVal, maxVal, initVal) {

//     const sliderDiv = divToAppendTo
//         .insert("div") //insert sliderdiv before svg
//         .attr("id", id + "div")
//         .attr("class", "sliderdiv")

//     //text above slider
//     sliderDiv.append("p")
//         .attr("class", "text title")
//         .text(text)

//     //slider itself
//     const slideContainer = sliderDiv.append("div")
//         .attr("class", "slidecontainer")

//     slideContainer.append("input")
//         .attr("type", "range")
//         .attr("class", "slider")
//         .attr("id", id)
//         .attr("min", minVal)
//         .attr("max", maxVal)
//         .attr("value", initVal)

//     //attach the number behind the slider
//     slideContainer.append("div")
//         .attr("class", "slidernumber")
//         .attr("id", id + "Number")
//         .text(initVal)
// }