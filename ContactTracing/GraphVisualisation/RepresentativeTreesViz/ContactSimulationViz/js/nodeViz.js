/**
 * Creates the stacked chart glyph for each node
 * @param {*} gElement 
 * @param {*} nodeId 
 * @param {*} isRepTree 
 */
function makeNodeGlyph(gElement, nodeId, isRepTree) {
    //make left chart
    makeStackedChart(gElement, nodeId, isRepTree, true);

    //make right chart
    makeStackedChart(gElement, nodeId, isRepTree, false);
}

function makeStackedChart(gElement, nodeId, isRepTree, isLeftChart) {
    let [startX, rectWidth] = getRectGlyphXPositions(isLeftChart)

    for (let partI = 0; partI < maxParts; partI++) {
        constructRect(gElement, nodeId, isRepTree, isLeftChart, partI, startX, rectWidth);
    }
}


function constructRect(gElement, nodeId, isRepTree, isLeftChart, partIndex, startX, rectWidth) {

    const color = getPartColor(partIndex, isLeftChart);
    const [y, height] = getRectGlyphYPositions(nodeId, partIndex, isRepTree, isLeftChart);

    if (height > 0) { //only add rectangles that have a height
        gElement.append("rect")
            .attr("x", startX)
            .attr("y", y)
            .attr("width", rectWidth)
            .attr("height", height)
            .attr("fill", color)
            .attr("class", "glyphRectangle")
    }
}

function updateNodeGlyphs(isRepTree) {
    const gElements = d3.select("#treeGrid") //do not animate these. d3 animations break down at around 20000 svg elements. The largest tree alone has 100 nodes with 10 parts each.
        .selectAll(".svgtree.visible")
        .selectAll(".node")
        .selectAll("g");

    gElements.selectAll("*").remove(); //remove all rectangles so we can add only those that are needed again

    gElements.each(function() {
        const nodeId = parseInt(d3.select(this).attr("id"));
        makeNodeGlyph(d3.select(this), nodeId, isRepTree)
    });
}

function getPartColor(index, isLeftChart) {
    let color;
    if (isLeftChart) {
        color = currentLeftColor;
    } else {
        color = currentRightColor;
    }

    if (color == "Infector State") { //Color part by infector state
        return infectionColorScheme[index];
    }
    if (color == "None") {
        return noneColorScheme[index];
    }
    if (color == "Infection Location") {
        return locationColorScheme[index];
    }
    if (color == "Age") {
        return ageColorScheme[index];
    }
    if (color == "Infection Time") {
        return infectionTimeColorScheme[index];
    }
    console.error("No valid color selected")
}



/**
 * returns [startPercentage,endPercentage] that indicates how much of the value this part has. 
 * @param {*} id 
 * @param {*} partIndex 
 * @param {*} isRepTree 
 * @param {*} isLeftChart
 * @returns 
 */
function getPartPercentages(id, partIndex, isRepTree, isLeftChart) {
    const counts = getPartCounts(id, isRepTree, isLeftChart);

    let startValue = 0; //value of all parts up to index {partIndex}
    let sum = 0;
    for (let i = 0; i < counts.length; i++) {
        sum += counts[i];
        if (i < partIndex) {
            startValue += counts[i];
        }
    }

    if (sum == 0) {
        console.log("Shouldn't happen. Something went wrong in data reading/parsing")
        return [0, 0];
    }

    const startPercentage = startValue / sum;

    const value = counts[partIndex];
    const endPercentage = (startValue + value) / sum;

    return [startPercentage, endPercentage];
}



function getPartCounts(id, isRepTree, isLeftChart) {
    let partCounts = new Array(maxParts).fill(0); //array length equal to amount of parts. Fill them in one by one

    let counts, color, policy, appPercentage;

    if (isLeftChart) { //get the right data
        color = currentLeftColor;
        policy = currentLeftPolicy
        appPercentage = currentLeftAppPercentage;
    } else {
        color = currentRightColor;
        policy = currentRightPolicy
        appPercentage = currentRightAppPercentage;
    }


    //get the array, some will have fewer values which we will pad. Each will have how many nodes are "saved" as the first entry
    if (color == "Infector State") { //Color part by infector state
        counts = infectorStateCount(id, isRepTree, policy, appPercentage);
    } else if (color == "None") {
        counts = noneCount(id, isRepTree, policy, appPercentage);
    } else if (color == "Infection Location") {
        counts = locationCount(id, isRepTree, policy, appPercentage);
    } else if (color == "Age") {
        counts = ageCount(id, isRepTree, policy, appPercentage);
    } else if (color == "Infection Time") {
        counts = timeCount(id, isRepTree, policy, appPercentage);
    } else {
        console.error(currentLeftColor + "is not a valid node color and parts cannot be drawn");
        counts = [0];
    }

    for (let i = 0; i < counts.length; i++) {
        partCounts[i] = counts[i];
    }
    return partCounts;
}


function getRectGlyphXPositions(isLeftChart) {
    let startX = getStartX(isLeftChart);
    let rectWidth = nodeBaseSize;

    return [startX, rectWidth];
}


function getRectGlyphYPositions(id, partIndex, isRepTree, isLeftChart) {

    const partRange = getPartPercentages(id, partIndex, isRepTree, isLeftChart);
    const rectSize = nodeBaseSize * 2; //nodeBaseSize is radius

    const y1 = partRange[0] * rectSize - rectSize / 2;
    const y2 = partRange[1] * rectSize - rectSize / 2;
    const rectHeight = y2 - y1;

    return [y1, rectHeight];
}


function isRectIndexFromLeftChart(rectIndex) {
    return rectIndex < maxParts;
}




function getStartX(isLeftChart) {
    if (isLeftChart) {
        return -nodeBaseSize;
    } else {
        return 0;
    }
}