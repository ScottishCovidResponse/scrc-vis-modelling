import * as d3 from 'd3';
import { showRtOfTreesRepresented } from './popup';
import { initSingleTree, updateTree } from './treeLayout';
import { vars } from './vizVariables';

// export var treeBaseWidthById = new Map(); //Base width of the tree by id of the root. Uses nodes of {@code nodeBaseSize} size
// export var treeBaseHeightById = new Map(); //Base  height of the tree by id of the root. Uses nodes of {@code nodeBaseSize} size



export function initTreeGrid(repTreesData) {
    console.log("Init tree grid")
    //get the svg grid where the trees will be added to.
    //using svg instead of flexbox for animations purposes.
    const treeGridDiv = d3.select("#treeGridDiv");
    const treeRoots = getTreeRoots(repTreesData);

    console.log("Creating a tree for every treeroot")
    for (let i = 0; i < treeRoots.length; i++) {
        const treeRoot = treeRoots[i];
        const id = treeRoot.data.id;

        const treeSvg = initSingleTree(treeGridDiv, treeRoot, id, true);
        treeSvg.on("click", function (event) {
            showRtOfTreesRepresented(event, treeRoot) 
        }) //TODO: Change click function to work on all of svg, not just nodes.

    }
}

export function updateTrees() {
    //do not animate these. d3 animations break down at around 20000 svg elements. The largest tree alone has 100 nodes with 10 parts each.
    d3.select("#treeGridDiv")
        .selectAll(".divsvgtree")
        .each(function (d) {
            //only update trees that are not hidden, don't need to update the rest.
            if (d3.select(this).classed("hidden") == false) {
                updateTree(d3.select(this), true);
            }
        })
}


export function getTreeRoots(treeData) {
    let treeRoots = [];
    for (let i = 0; i < treeData.length; i++) {
        const treeRoot = getTree(treeData[i]);
        treeRoots[i] = treeRoot;
    }
    return treeRoots;
}


/**
 * Returns a tree layout of the data with the correct nodesizes and all positive coordinates.
 * @param {*} data 
 * @returns 
 */
function getTree(data) {
    const dataRoot = d3.hierarchy(data);
    const treeRoot = d3.tree()
        .nodeSize([vars.horNodeSpace, vars.verNodeSpace])
        (dataRoot)


    moveTreeToFirstQuadrantAndInvert(treeRoot);
    return treeRoot;
}




/**
 * Moves the position of the nodes in the tree with root {@code root} 
 * such that it is completely in the first quadrant with at least one node with x=0 and one node with y=0.
 * Additionally ensure the tree grows towards the top.
 * @param {The tree we are moving} root 
 */
function moveTreeToFirstQuadrantAndInvert(root: d3.HierarchyPointNode<unknown>) {
    //invert tree
    root.each(d => {
        d.y = -d.y;
    });

    //put back into  quadrant 1 starting at 0,0
    let minY = Infinity;
    let minX = Infinity;
    root.each(d => {
        if (minY > d.y) minY = d.y;
        if (minX > d.x) minX = d.x;
    });
    root.each(d => {
        d.x -= minX;
        d.y -= minY;
    });
}
