const popupPadding = 3*2;//even number for even padding


function showTreesRepresented(event, treeRoot) {

    const id = treeRoot.data.id;

    if (!d3.select("#treeGrid").select("#popupTreeGrid" + treeRoot.data.id).empty()) {
        //already have it, remove it.
        removePopup(id);
        return;
    }

    //add an svg to the treegridSVG at the correct position
    const popupTreeGridSvg = d3.select("#treeGrid").append("svg")
        .attr("class", "popupTreeGrid")
        .attr("id", "popupTreeGrid" + id)
        .on("click", function () { removePopup(id) })//add a removal function


    const treesRepresented = getTreeHierarchiesRepresented(id);

    //use function from offsetCalculator to calculate the offests
    let offSets = getOffSets(treesRepresented, popupWidth,false);

    addPadding(offSets);//add padding to tree positions

    let maxHeight = 0;
    let maxWidth = 0;

    for (let i = 0; i < treesRepresented.length; i++) {
        const xOffset = offSets[i][0];
        const yOffset = offSets[i][1];
        const repTreeRoot = treesRepresented[i];
        const idI = repTreeRoot.data.id;

        //use function from treeLayout to layout a single tree
        createSingleTree(popupTreeGridSvg, xOffset, yOffset, repTreeRoot, idI);

        //use helper function from representativeGraph to get width and height
        const height = yOffset + getDisplayHeight(repTreeRoot);
        maxHeight = Math.max(maxHeight, height);

        const width = xOffset + getDisplayWidth(repTreeRoot);
        maxWidth = Math.max(maxWidth, width);
    }

    //add adding
    maxWidth += popupPadding;
    maxHeight += popupPadding;

    //assign the width and height to the svg
    popupTreeGridSvg.attr("width", maxWidth);
    popupTreeGridSvg.attr("height", maxHeight);


    attachPopupFrameAndPosition(event, popupTreeGridSvg, maxWidth, maxHeight);
}



function getTreeHierarchiesRepresented(id) {

    const treesRepresented = getTreesRepresentedById(id,currentEditDistance)
    
    //use function from representativeGraph to get the tree layouts
    let treeRoots = getTreeRoots(treesRepresented);
    return treeRoots;
}

function removeAllPopups() {
    //remove the popup
    d3.select("#treeGrid").selectAll(".popupTreeGrid").remove();
}

function removePopup(id) {
    d3.select("#treeGrid").select("#popupTreeGrid" + id).remove();
}

function attachPopupFrameAndPosition(event, svg, width, height) {

    svg.append("rect")
        .lower()
        .attr("class", "popupFrame")
        .attr("width", width)
        .attr("height", height);

    let x = event.offsetX;
    let y = event.offsetY;

    let targetSVGWidth = d3.select("#treeGrid").node().clientWidth;

    //mirror the popup if it doesn't fit
    if ((x + width) > targetSVGWidth) {
        x = x - width;
    }
    if ((y + height) > window.innerHeight) {
        y = y - height;
    }



    svg.attr("x", x)
        .attr("y", y);
}

function addPadding(offSets) {
    for (let i = 0; i < offSets.length; i++) {
        offSets[i][0] += popupPadding / 2;
        offSets[i][1] += popupPadding / 2;
    }
}
