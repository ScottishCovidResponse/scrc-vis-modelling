const barChartHeight = 100;

function createComponentBarChart(divToAddTo) {

    let leftColors = [];
    let rightColors = [];
    for (let i = 0; i < maxParts; i++) {
        leftColors[i] = getPartColor(i, true);
        rightColors[i] = getPartColor(i, false);
    }

    //get the distribution
    let leftValues = new Array(maxParts).fill(0);
    let rightValues = new Array(maxParts).fill(0);
    metaDataFromNodeById.forEach((metaData, id) => {
        //only count it this node is at the right depth
        const nodeDepth = metaData.depth;

        if (currentLeftDistributionSelection.includes("All") || currentLeftDistributionSelection.includes(nodeDepth)) {
            const partCountsLeft = getPartCounts(id, false, true);
            for (let i = 0; i < maxParts; i++) {
                leftValues[i] += partCountsLeft[i];
            }
        }

        if (currentRightDistributionSelection.includes("All") || currentRightDistributionSelection.includes(nodeDepth)) {
            const partCountsRight = getPartCounts(id, false, false);
            for (let i = 0; i < maxParts; i++) {
                rightValues[i] += partCountsRight[i];
            }
        }
    })

    //add an additional normalizing step as we are only showing the values from a single level which is confusing.
    //Bar charts need to display height proportional to the total.
    let totalNodes;
    if (normalizeComponentChart) { //if normalize is true, simply show each level by itself
        totalNodes = -1;
    } else {
        totalNodes = metaDataFromNodeById.size;
    }


    const barchartsDiv = divToAddTo.append("div").attr("class", "barChartsContainer")

    const leftBarChartG = barchartsDiv.append("svg")
        .attr("class", "barChartSvg")
        .attr("height", barChartHeight)
        .append("g")

    createBarChart(leftBarChartG, barChartHeight, leftValues, leftColors, totalNodes)

    const rightBarChartG = barchartsDiv.append("svg")
        .attr("class", "barChartSvg")
        .attr("height", barChartHeight)
        .append("g")

    createBarChart(rightBarChartG, barChartHeight, rightValues, rightColors, totalNodes)

}



function createBarChart(gElement, totalHeight, dataValues, colors, sum) {
    const parts = dataValues.length;
    if (sum === undefined || sum == -1) { //either not given or explicitly set to not use. Otherwise it's a value
        sum = dataValues.reduce((accumulator, currentVal) => accumulator + currentVal)
    }


    let currentY = 0;
    for (let partI = 0; partI < parts; partI++) {
        const height = dataValues[partI] / sum * totalHeight;
        const color = colors[partI];

        if (height > 0) { //only add rectangles that have a height
            gElement.append("rect")
                .attr("x", 0)
                .attr("y", currentY)
                .attr("height", height)
                .attr("fill", color)
                .attr("class", "barChartRectangle")

            currentY += height;
        }
    }
}