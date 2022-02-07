import * as d3 from 'd3';
import { vars } from './vizVariables';
import { getAmountOfTreesRepresentedById } from './dataQueries'
import { initNodeGlyph, updateNodeGlyph } from './nodeViz';



/**
 * 
 * @param {*} divToAddTo 
 * @param {*} root 
 * @param {*} treeId 
 * @param {if true, treats this tree as a representative tree. If false, does not take the representations into account} isRepTree 
 * @returns 
 */
export function initSingleTree(divToAddTo: d3.Selection<d3.BaseType, unknown, HTMLElement, undefined>, root: d3.HierarchyPointNode<unknown>, treeId: number, isRepTree: boolean) {

    let scaleFactor = 1;
    let repAmount = 0;
    if (isRepTree) {
        repAmount = getAmountOfTreesRepresentedById(treeId, vars.currentEditDistance);
        scaleFactor = getScaleFactorByRepAmount(repAmount);
    }

    const width = getDisplayWidth(root);
    const height = getDisplayHeight(root);

    const treeSvgDiv = divToAddTo
        .insert("div")
        .attr("id", "tid" + treeId)
        .attr("class", "divsvgtree");

    //make the tree itself
    const treeSvg = treeSvgDiv.insert("svg")
        .attr("viewBox", [0, 0, width, height])
        .attr("width", width * scaleFactor)
        .attr("height", height * scaleFactor)
        .data(root) //bind the data

    //add a background so everything is clickable
    const background = treeSvg.append("g")
        .append("rect")
        .attr("class","svgbackground")
        .attr("x", 0)
        .attr("y", 0)
        .attr("width", width)
        .attr("height", height)
        // .style("opacity", 0.0) //make it invisible. TODO: Check performance issues


    const g = treeSvg.append("g")
        .attr("transform", `translate(${vars.marginWithinTree / 2},${vars.marginWithinTree / 2})`); //make sure no clipping occurs

    const link = g.append("g") //links
        .attr("class", "edge")
        .selectAll("path")
        .data(root.links())
        .join("path") //@ts-ignore
        .attr("d", d3.linkVertical()//@ts-ignore
            .x(d => d.x)//@ts-ignore
            .y(d => d.y))
        .attr("stroke-width", vars.linkBaseSize);

    const node = g.append("g") //nodes
        .attr("class", "node")
        .selectAll("g")
        .data(root.descendants())
        .join("g")
        .attr("transform", d => `translate(${d.x},${d.y})`)
        .attr("id", function (d) {//@ts-ignore
            return d.data.id
        })

    //Init glyphs for each node in the tree
    node.each(function (d) {
        initNodeGlyph(d3.select(this))
    })

    //init representative tree text
    treeSvgDiv.append("text")
        .attr("class", "textRepAmount")
        .attr("font-size", vars.fontSizeRepAmount)
        .text(repAmount)

    return treeSvgDiv;
}

export function updateTree(treeSvgDiv: d3.Selection<d3.BaseType, unknown, HTMLElement, undefined>, isRepTree: boolean) {

    const treeSvg =  treeSvgDiv.select("svg");
    //@ts-ignore
    const root: d3.HierarchyPointNode<unknown> = treeSvg.data()[0]
    const treeId = Number.parseInt(treeSvgDiv.attr("id").substring(3));


    let repAmount: number = null;
    let scaleFactor = 1;
    if (isRepTree) {
        repAmount = getAmountOfTreesRepresentedById(treeId, vars.currentEditDistance);
        scaleFactor = getScaleFactorByRepAmount(repAmount);
    }

    //update width and height
    const width = getDisplayWidth(root);
    const height = getDisplayHeight(root);

    treeSvg.attr("width", width * scaleFactor)
        .attr("height", height * scaleFactor)

    if (isRepTree) {
        updateTreeRepNumber(treeSvgDiv, repAmount);
    }

    updateNodeGlyph(treeSvg);
}

function updateTreeRepNumber(treeSvgDiv: d3.Selection<d3.BaseType, unknown, HTMLElement, undefined>, repAmount: number) {

    //add how many trees this node represents if the data is present 
    const text = treeSvgDiv.select("text")
        .text(repAmount)

    return text;
}


function getScaleFactorByRepAmount(repAmount: number) {
    if (repAmount == 0) {
        repAmount = 1; //prevent taking the log of 0
    }
    const scaleFactor = 1 + Math.log10(repAmount);
    return scaleFactor
}

/**
 * Returns the width of the svg as it will be rendered on screen for a single tree
 * @param {} treeRoot 
 * @returns 
 */
function getDisplayWidth(treeRoot: d3.HierarchyPointNode<unknown>) {
    const width = getWidth(treeRoot) + vars.marginWithinTree; //TODO: Adjust width and offset for label placement
    return width;
}

function getWidth(treeRoot: d3.HierarchyPointNode<unknown>) {
    let maxX = -Infinity;
    let minX = Infinity;
    treeRoot.each(d => {
        if (d.x > maxX) maxX = d.x;
        if (d.x < minX) minX = d.x;
    });
    return maxX - minX;
}


/**
 * Returns the height of the svg as it will be rendered on screen for a single tree
 * @param {} treeRoot 
 * @returns 
 */
function getDisplayHeight(treeRoot: d3.HierarchyPointNode<unknown>) {
    return getHeight(treeRoot) + vars.marginWithinTree + vars.fontSizeRepAmount;
}

function getHeight(treeRoot: d3.HierarchyPointNode<unknown>) {
    let maxY = -Infinity;
    let minY = Infinity;
    treeRoot.each(d => {
        if (d.y > maxY) maxY = d.y;
        if (d.y < minY) minY = d.y;
    });
    return maxY - minY;
}
