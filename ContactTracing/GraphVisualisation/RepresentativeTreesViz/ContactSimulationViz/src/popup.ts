import * as d3 from "d3";
import { getTreesRepresentedById, getMetaDataValueFromId } from './dataQueries';
import { makeRtPlot } from "./DensityPlot";
import { getTreeRoots } from "./representativeGraph";

import { vars } from "./vizVariables";

const popupWidth = 500; //width of the popup when clicking a node to see which trees it represents.
const popupHeight = 200; //height of the popup

//getting the width of the page
function getPageWidth() {
    return Math.max(
        document.body.scrollWidth,
        document.documentElement.scrollWidth,
        document.body.offsetWidth,
        document.documentElement.offsetWidth,
        document.documentElement.clientWidth
    );
}

//getting the height of the page
function getPageHeight() {
    return Math.max(
        document.body.scrollHeight,
        document.documentElement.scrollHeight,
        document.body.offsetHeight,
        document.documentElement.offsetHeight,
        document.documentElement.clientHeight
    );
}


// let popupVisible = false;

export function showRtOfTreesRepresented(event, treeRoot) {
    const id = treeRoot.data.id;

    let popupDiv = d3.select("#popup");

    let x = event.pageX;
    let y = event.pageY + 20; //put it slightly below

    const pageWidth = getPageWidth();
    const pageHeight = getPageHeight();

    if (x + popupWidth > pageWidth) {
        x = pageWidth - popupWidth - 10;
    }
    if (y + popupHeight > pageHeight) {
        y = pageHeight - popupHeight - 10;
    }

    popupDiv
        .on("click", function () { removePopup() })//add a removal function
        .style("left", x + "px")
        .style("top", y + "px")
        .style("width", popupWidth + "px")
        .style("height", popupHeight + "px")
        .style("display","flex")

    const treesRepresented = getTreeHierarchiesRepresented(id);

    let data: number[][] = [];
    for (let i in treesRepresented) {
        let tree = treesRepresented[i];
        let rtDistance = getRtDistance(tree)
        data[i] = rtDistance;
    }

    console.log("Might need to fill data")

    makeRtPlot(popupDiv, data,popupWidth,popupHeight)
}

function getDaysPassed(node: d3.HierarchyNode<unknown>, rootTime: number): number {
    let id = node.data.id;
    let infectionTime = getMetaDataValueFromId("positiveTestTime", id);
    let timeDiff = infectionTime - rootTime;
    let daysPassed = Math.round(timeDiff / 60 / 60 / 24);//seconds to days
    return daysPassed;
}

function getRtDistance(rootNode: d3.HierarchyNode<unknown>): number[] {
    let nodes = rootNode.descendants();

    let rootId = rootNode.data.id;
    let rootTime = getMetaDataValueFromId("positiveTestTime", rootId);


    let nodesInfectedAtDay: number[] = []
    let childCountOfNodesInfectedAtDay: number[] = [];

    let maxDay = 0;

    for (let i in nodes) {
        let node = nodes[i];
        let childCount = 0;
        if (node.children != undefined) {
            childCount = node.children.length;
        }
        let daysPassed = getDaysPassed(node, rootTime);

        maxDay = Math.max(maxDay, daysPassed);

        //add 1 to the nodesInfectedAtDay
        if (nodesInfectedAtDay[daysPassed] == undefined) {
            nodesInfectedAtDay[daysPassed] = 0;
        }
        nodesInfectedAtDay[daysPassed] = nodesInfectedAtDay[daysPassed] + 1;

        //add childCount to the amount of children
        if (childCountOfNodesInfectedAtDay[daysPassed] == undefined) {
            childCountOfNodesInfectedAtDay[daysPassed] = 0;
        }
        childCountOfNodesInfectedAtDay[daysPassed] = childCountOfNodesInfectedAtDay[daysPassed] + childCount;
    }

    let rtValues: number[] = [];
    for (let i = 0; i <= maxDay; i++) {
        if (nodesInfectedAtDay[i] == 0 || nodesInfectedAtDay[i] == undefined) { //prevent divide by 0 errors
            rtValues[i] = 0;
        } else {
            rtValues[i] = childCountOfNodesInfectedAtDay[i] / nodesInfectedAtDay[i];
        }
    }
    return rtValues;
}




function getTreeHierarchiesRepresented(id) {

    const treesRepresented = getTreesRepresentedById(id, vars.currentEditDistance)

    //use function from representativeGraph to get the tree layouts
    let treeRoots = getTreeRoots(treesRepresented);
    return treeRoots;
}


function removePopup() {
    d3.select("#popup").style("display", "none");
}
