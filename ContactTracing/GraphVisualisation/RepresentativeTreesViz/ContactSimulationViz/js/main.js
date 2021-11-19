//TODO: Convert everything to typescript with npm and webpack

const allTreesDataInputLocation2 = "E:/TTP/TTPDataPlus/AllTrees.json";
// const allTreesDataInputLocation2 = "data/RealData/AllTrees.json";
const metaDataInputLocation2 = "data/RealData/NodesAndMeta.json";
const gridNamesInputLocation = "data/RealData/WalesGridmapCoordinates.csv";



var allTreesData, metaData, gridNames;
var d3;

var allTreeById = new Map(); //holds the alltree data by id of the root
var metaDataFromNodeById = new Map(); //holds the meta data for each node by id of the node.

const categoricalColorScheme = ["#a6cee3", "#1f78b4", "#b2df8a", "#33a02c", "#fb9a99", "#e31a1c", "#fdbf6f", "#ff7f00", "#cab2d6", "#6a3d9a"];
// const integerColorScheme = ["#fee0d2", "#fcbba1", "#fc9272", "#fb6a4a", "#ef3b2c", "#cb181d", "#a50f15"];
const integerColorScheme = ["#fc9272", "#fb6a4a", "#ef3b2c", "#cb181d", "#a50f15"];
const noneColorScheme = ["#888888"];

//Metadata
var metaDataNames = []; //names of the metadatafields
var metaDataTypes = []; //types of the metadatafield

//Load in all the javascript files
requirejs(["js/d3/d3.js", "js/ColorSchemes.js", "js/dataQueries.js", "js/GridMap.js"], function(d3Var) {
    //load in all the data
    d3 = d3Var;
    // d3.json(allTreesDataInputLocation2).then(function(allTreesDataInput) {
    d3.json(metaDataInputLocation2).then(function(metaDataInput) {
        d3.text(gridNamesInputLocation).then(function(gridNamesInput) {
            allTreesDataInput = read()

            allTreesData = allTreesDataInput;
            metaData = metaDataInput;

            gridNames = d3.csvParseRows(gridNamesInput)

            preprocessData();

            initGridMap(gridNames);

            // testMap = new Map();
            // testMap.set("Conwy-Powys", 2)
            // testMap.set("Powys-Conwy", 5)
            // updateOdMapFromMap(testMap);

            updateODMapFromTrees(allTreesData);
            // });
        });
    });
});