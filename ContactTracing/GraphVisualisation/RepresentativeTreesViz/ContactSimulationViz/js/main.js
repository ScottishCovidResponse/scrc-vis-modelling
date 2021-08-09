const repTreesDataInputLocation = "data/RealData/RepTrees.json";
const allTreesDataInputLocation = "data/RealData/AllTrees.json";
const metaDataInputLocation = "data/RealData/NodesAndMeta.json";

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
    fontSizeRepAmount = nodeSize * 2; //Base font size for the number that tells how much is represented

}


//Space for the layout
const verticalMarginBetweenTrees = 4; //Vertical space between trees.
const hiddenTreesScalingFactor = 0.001; //how much the trees that are represented by other trees are scaled down






const initEditDistanceSliderVal = 15; //start the slider at 0


const popupWidth = 500; //width of the popup when clicking a node to see which trees it represents.

var treeOrder = []; //order of the trees in the viz
var treeBaseWidthById = new Map(); //Base width of the tree by id of the root. Uses nodes of {@code nodeBaseSize} size
var treeBaseHeightById = new Map(); //Base  height of the tree by id of the root. Uses nodes of {@code nodeBaseSize} size

var repTreeById = new Map(); //holds the representative tree data by id of the root
var repNodeById = new Map(); //holds all node data represented by the specified node
var allTreeById = new Map(); //holds the alltree data by id of the root
var metaDataFromNodeById = new Map(); //holds the meta data for each node by id of the node.


var currentEditDistance = initEditDistanceSliderVal; //Current edit distance

//color schemes
const maxParts = 10; //How many different parts we can have at maximum in the glyph.
const categoricalColorScheme = ["#a6cee3", "#1f78b4", "#b2df8a", "#33a02c", "#fb9a99", "#e31a1c", "#fdbf6f", "#ff7f00", "#cab2d6", "#6a3d9a"];
const noneColorScheme = ["#888888"];


var currentLeftAttributeName = "None"; //What we are currently coloring the nodes by for the left sides of the glyphs
var currentLeftAttributeType = "None" //what the type is of the variable we are coloring for the left side of the glyps
var currentLeftColorScheme = noneColorScheme //the {maxParts} colors we are using in order.
var currentLeftColorSchemeValues = []; //The values used to determine which color to pick for the left side of the glyhp. For integers, these holds the upper bounds of the bin partitions. For categorical, these holds the 9 most frequent value names and "other" in bin 10

var currentRightAttributeName = "None"; //What we are currently coloring the nodes by for the right sides of the glyphs
var currentRightAttributeType = "None" //what the type is of the variable we are coloring for the right side of the glyps
var currentRightColorScheme = noneColorScheme //the {maxParts} colors we are using in order.
var currentRightColorSchemeValues = []; //The values used to determine which color to pick for the right side of the glyhp. For integers, these holds the upper bounds of the bin partitions. For categorical, these holds the 9 most frequent value names and "other" in bin 10



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


var repTreesData, allTreesData, metaData, originalExposedData;
var d3;




//Load in all the javascript files
requirejs(["js/d3/d3.js", "js/ColorSchemes.js", "js/BarChart.js", "js/LineChart.js", "js/StackedAreaChart.js", "js/dataQueries.js", "js/stateCounters.js", "js/nodeViz.js", "js/sidePanel.js", "js/treeLayout.js", "js/representativeGraph.js", "js/popup.js", "js/updateFunctions.js", "js/offsetCalculator.js"], function(d3Var) {
    //load in all the data
    d3 = d3Var;
    d3.json(repTreesDataInputLocation).then(function(repTreesDataInput) {
        d3.json(allTreesDataInputLocation).then(function(allTreesDataInput) {
            d3.json(metaDataInputLocation).then(function(metaDataInput) {
                // d3.csv(originalExposedDataInputLocation).then(function(originalExposedDataInput) {
                repTreesData = repTreesDataInput;
                allTreesData = allTreesDataInput;
                metaData = metaDataInput;

                if ("policies" in metaData[0]) {
                    policyDataPresent = true;
                } else {
                    console.log("Policies are disabled. Need to be test and possibly reimplemented")
                }

                // originalExposedData = originalExposedDataInput;
                setVizSizes(nodeBaseSize);
                mainRepresentativeGraph();
                updateAll(); //update to use slider values
                // });
            });
        });
    });
});


function mainRepresentativeGraph() {
    preprocessData();


    createSidePanel()


    generateTreeGrid();


    window.addEventListener('resize', function() { updatePositions() }, true);

}