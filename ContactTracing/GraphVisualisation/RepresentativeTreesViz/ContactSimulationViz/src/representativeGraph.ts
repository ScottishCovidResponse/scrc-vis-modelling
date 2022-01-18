import * as d3 from 'd3';
import { showTreesRepresented } from './popup';
import { createSingleTree, getDisplayHeight, getDisplayWidth, getOffSets, getTreeRoots } from './treeLayout';

export var treeOrder = []; //order of the trees in the viz
export var treeBaseWidthById = new Map(); //Base width of the tree by id of the root. Uses nodes of {@code nodeBaseSize} size
export var treeBaseHeightById = new Map(); //Base  height of the tree by id of the root. Uses nodes of {@code nodeBaseSize} size


export function generateTreeGrid(repTreesData) {
    //get the svg grid where the trees will be added to.
    //using svg instead of flexbox for animations purposes.
    const treeGridSVG = d3.select("#treeGrid");
    const div = d3.select("#treeGridDiv");


    let targetContainerWidth = div.node().clientWidth;

    const treeRoots = getTreeRoots(repTreesData);
    setBaseWidthAndHeightById(treeRoots); //used later when scaling nodes


    const offSets = getOffSets(treeRoots, treeBaseWidthById, treeBaseHeightById, targetContainerWidth, true);

    for (let i = 0; i < treeRoots.length; i++) {
        const xOffset = offSets[i][0];
        const yOffset = offSets[i][1];
        const treeRoot = treeRoots[i];
        const id = treeRoot.data.id;

        const treeSvg = createSingleTree(treeGridSVG, xOffset, yOffset, treeRoot, id, true);
        treeSvg.on("click", function (event) { showTreesRepresented(event, treeRoot) }) //TODO: Change click function to work on all of svg, not just nodes.

    }


    //size treegridsvg according to it's bounding box
    resizeSVG(treeGridSVG);
}




/**
 * Sets the width and height of the svg element to fit the content
 * @param {The d3 svg element we are resizing} svg 
 */
export function resizeSVG(svg) {
    //size svg according to it's bounding box
    const bbox = svg.node().getBBox();
    svg.attr("width", bbox.width);
    svg.attr("height", bbox.height);
}


function setBaseWidthAndHeightById(treeRoots) {
    for (let i = 0; i < treeRoots.length; i++) {
        const treeRoot = treeRoots[i];
        const width = getDisplayWidth(treeRoot);
        const height = getDisplayHeight(treeRoot);

        treeBaseWidthById.set(treeRoot.data.id, width);
        treeBaseHeightById.set(treeRoot.data.id, height);
        treeOrder[i] = treeRoot.data.id;
    }
}