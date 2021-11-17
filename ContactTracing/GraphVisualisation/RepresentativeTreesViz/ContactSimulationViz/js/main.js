//TODO: Convert everything to typescript with npm and webpack

const allTreesDataInputLocation2 = "data/RealData/AllTrees.json";
const metaDataInputLocation2 = "data/RealData/NodesAndMeta.json";
const gridNamesInputLocation = "data/RealData/WalesGridmapCoordinates.csv";



var allTreesData2, metaData2, gridNames;
var d3;




//Load in all the javascript files
requirejs(["js/d3/d3.js", "js/ColorSchemes.js", "js/GridMap.js"], function(d3Var) {
    //load in all the data
    d3 = d3Var;
    d3.json(allTreesDataInputLocation2).then(function(allTreesDataInput) {
        d3.json(metaDataInputLocation2).then(function(metaDataInput) {
            d3.text(gridNamesInputLocation).then(function(gridNamesInput) {


                allTreesData2 = allTreesDataInput;
                metaData2 = metaDataInput;

                gridNames = d3.csvParseRows(gridNamesInput)
                initGridMap(gridNames);
            });
        });
    });
});