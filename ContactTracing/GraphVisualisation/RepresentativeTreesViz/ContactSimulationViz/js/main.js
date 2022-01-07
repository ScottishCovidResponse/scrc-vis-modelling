//TODO: Convert everything to typescript with npm and webpack

const repTreesDataInputLocation = "data/TTPData/RepTrees.json";
const allTreesDataInputLocation = "data/TTPData/AllTrees.json";
const metaDataInputLocation = "data/TTPData/NodesAndMeta.json";
const gridNamesInputLocation = "data/TTPData/WalesGridmapCoordinates.csv";

//Visual variables
//Variables for the tree visualization
let nodeBaseSize = 8; //radius of the node
let linkBaseSize; //Width of links
let verNodeSpace; //vertical space between nodes
let horNodeSpace; //horitonzal space between nodes
let marginWithinTree; //margin between the trees
let horizontalMarginBetweenTrees; //Horizontal space between trees.
let fontSizeRepAmount; //Base font size for the number that tells how much is represented

function setVizSizes(nodeSize) {
    nodeBaseSize = nodeSize;
    linkBaseSize = nodeSize / 2; //constant link size depending on node size
    verNodeSpace = nodeSize * 2 + 3; //Vertical space between nodes. *2 as this is the diamater of a node. 
    horNodeSpace = nodeSize * 2 + 2; // Horizontal space between nodes. *2 as this is the diamater of a node.
    marginWithinTree = nodeSize * 2; //Makes sure the tree doesn't get clipped
    horizontalMarginBetweenTrees = nodeBaseSize * 2;
    // fontSizeRepAmount = nodeSize * 2; //Base font size for the number that tells how much is represented
    fontSizeRepAmount = 3;
}
//Space for the layout
const verticalMarginBetweenTrees = 4; //Vertical space between trees.
const hiddenTreesScalingFactor = 0.001; //how much the trees that are represented by other trees are scaled down

const initEditDistanceSliderVal = 15; //start the slider at 0
var currentEditDistance = initEditDistanceSliderVal; //Current edit distance


var treeOrder = []; //order of the trees in the viz
var treeBaseWidthById = new Map(); //Base width of the tree by id of the root. Uses nodes of {@code nodeBaseSize} size
var treeBaseHeightById = new Map(); //Base  height of the tree by id of the root. Uses nodes of {@code nodeBaseSize} size

const popupWidth = 500; //width of the popup when clicking a node to see which trees it represents.

var repTreeById = new Map(); //holds the representative tree data by id of the root
var repNodeById = new Map(); //holds all node data represented by the specified node
var allTreeById = new Map(); //holds the alltree data by id of the root
var metaDataFromNodeById = new Map(); //holds the meta data for each node by id of the node.


const maxParts = 7; //How many different parts we can have at maximum in the glyph.

const categoricalColorScheme = ["#a6cee3", "#1f78b4", "#b2df8a", "#33a02c", "#fb9a99", "#e31a1c", "#fdbf6f"];
const integerColorScheme = ["#feedde", "#fdd0a2", "#fdae6b", "#fd8d3c", "#f16913", "#d94801", "#8c2d04"]
const noneColorScheme = ["#888888"];


var currentLeftAttributeName = "None"; //What we are currently coloring the nodes by for the left sides of the glyphs
var currentLeftAttributeType = "None" //what the type is of the variable we are coloring for the left side of the glyps
var currentLeftColorScheme = noneColorScheme //the {maxParts} colors we are using in order.
var currentLeftColorSchemeValues = []; //The values used to determine which color to pick for the left side of the glyhp. For integers, these holds the upper bounds of the bin partitions. For categorical, these holds the 9 most frequent value names and "other" in bin 10
var currentLeftAttributeBounds = [-Infinity, Infinity]; //Holds the values for the minimum and maximum value for the current integer attribute

var currentRightAttributeName = "None"; //What we are currently coloring the nodes by for the right sides of the glyphs
var currentRightAttributeType = "None" //what the type is of the variable we are coloring for the right side of the glyps
var currentRightColorScheme = noneColorScheme //the {maxParts} colors we are using in order.
var currentRightColorSchemeValues = []; //The values used to determine which color to pick for the right side of the glyhp. For integers, these holds the upper bounds of the bin partitions. For categorical, these holds the 9 most frequent value names and "other" in bin 10
var currentRightAttributeBounds = [-Infinity, Infinity]; //Holds the values for the minimum and maximum value for the current integer attribute



//Policies
var policyDataPresent = false; //Whether policy data is present

var currentLeftPolicy = "1a"; //what the current policy is for the left sides of the glyphs
var currentRightPolicy = "1a"; //what the current policy is for the left sides of the glyphs
var splitPolicy = false; //Whether to split to policy into infection route prevent and contact avoided.

var currentLeftAppPercentage = "100"; //How many people have the app
var currentRightAppPercentage = "100";
//End policies


var currentLeftDistributionSelection = "All"; //which levels of the distribution we are currently showing
var currentRightDistributionSelection = "All"; //which levels of the distribution we are currently showing
var normalizeComponentChart = false; //whether we normalize the bar chart against the total amount of nodes or not.

var sortEnabled = false;
var sortBy = "Tree size";


var recalculate = false; //Holds whether we need to recalculate the tree grid. Can happen in case of node size change or data change


//Metadata
var metaDataNames = []; //names of the metadatafields
var metaDataTypes = []; //types of the metadatafield


var repTreesData, allTreesData, metaData, gridNames;
var d3;


//Load in all the javascript files
requirejs(["js/d3/d3.js", "js/ColorSchemes.js", "js/BarChart.js", "js/LineChart.js", "js/StackedAreaChart.js", "js/dataQueries.js", "js/stateCounters.js", "js/nodeViz.js", "js/sidePanel.js", "js/treeLayout.js", "js/representativeGraph.js", "js/popup.js", "js/updateFunctions.js", "js/offsetCalculator.js", "js/GridMap.js"], function(d3Var) {
    //load in all the data
    d3 = d3Var;
    d3.json(repTreesDataInputLocation).then(function(repTreesDataInput) {
        d3.json(allTreesDataInputLocation).then(function(allTreesDataInput) {
            d3.json(metaDataInputLocation).then(function(metaDataInput) {
                d3.text(gridNamesInputLocation).then(function(gridNamesInput) {
                    repTreesData = repTreesDataInput;
                    allTreesData = allTreesDataInput;
                    metaData = metaDataInput;

                    preprocessData();

                    gridNames = d3.csvParseRows(gridNamesInput)

                    if ("policies" in metaData[0]) {
                        policyDataPresent = true;
                    } else {
                        console.log("Policies are disabled. Need to be tested and likely reimplemented")
                    }



                    setVizSizes(nodeBaseSize);
                    mainRepresentativeGraph();
                    updateAll(); //update to use slider values

                    // preprocessData();

                    initGridMap(gridNames);
                    const june1 = 1622505600;
                    const june28 = 1623456000;
                    const december1 = 1638316800;

                    // createTimeSlider(d3.select("#sidePanel"));
                    // updateODMapFromTrees(allTreesData, august15, september12);
                    updateGridMapFromTrees(june1, december1);
                });
            });
        });
    });
});

sliderValue = 1628985600;

function createTimeSlider(selectorDiv) {

    createSlider(selectorDiv, "TimeSlider", "time", 1628985600, 1635638400, sliderValue)

    d3.select("#TimeSlider")
        .on("input", function() {
            updateSlider(this.value)
        })

    document.addEventListener('keypress', function(e) {
        console.log("pressed");
        sliderValue = parseInt(sliderValue) + 60 * 60 * 24;
        updateSlider(sliderValue)
    })

}

function updateSlider(value) {
    sliderValue = value;

    let selectedTime = parseInt(value); //keep the value up to date
    updateGridMapFromTrees(selectedTime, selectedTime + (60 * 60 * 24 * 7)); //week time view

    console.log(selectedTime)
    let d = new Date(0);
    d.setUTCSeconds(value)

    let month = d.getMonth() + 1;
    if (month.toString().length == 1) {
        month = "0" + month;
    }
    let day = d.getDate();
    if (day.toString().length == 1) {
        day = "0" + day;
    }
    let year = d.getFullYear();

    d3.select("#TimeSliderNumber").text(day + "." + month + "." + year);
    d3.select("#TimeSlider").property("value", parseInt(sliderValue))
}


function createSlider(divToAppendTo, id, text, minVal, maxVal, initVal) {

    const sliderDiv = divToAppendTo
        .insert("div") //insert sliderdiv before svg
        .attr("id", id + "div")
        .attr("class", "sliderdiv")

    //text above slider
    sliderDiv.append("p")
        .attr("class", "text title")
        .text(text)

    //slider itself
    const slideContainer = sliderDiv.append("div")
        .attr("class", "slidecontainer")

    slideContainer.append("input")
        .attr("type", "range")
        .attr("class", "slider")
        .attr("id", id)
        .attr("min", minVal)
        .attr("max", maxVal)
        .attr("value", initVal)

    //attach the number behind the slider
    slideContainer.append("div")
        .attr("class", "slidernumber")
        .attr("id", id + "Number")
        .text(initVal)
}



function mainRepresentativeGraph() {
    createSidePanel()
    generateTreeGrid();
    window.addEventListener('resize', function() { updatePositions() }, true);

}