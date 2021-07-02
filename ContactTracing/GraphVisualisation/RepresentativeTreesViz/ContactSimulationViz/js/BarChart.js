const barChartHeight = 100;

function createComponentChart(divToAddTo) {

    let leftColors = [];
    let rightColors = [];
    for (let i = 0; i < maxParts; i++) {
        leftColors[i] = getPartColor(i, true);
        rightColors[i] = getPartColor(i, false);
    }

    let leftValues = new Array(maxParts).fill(0);
    let rightValues = new Array(maxParts).fill(0);
    metaDataFromNodeById.forEach((metaData, id) => {
        const partCountsLeft = getPartCounts(id, false, true);
        for (let i = 0; i < maxParts; i++) {
            leftValues[i] += partCountsLeft[i];
        }
        const partCountsRight = getPartCounts(id, false, false);
        for (let i = 0; i < maxParts; i++) {
            rightValues[i] += partCountsRight[i];
        }
    })

    divToAddTo.append("p").attr("class", "title text").text("Distribution of properties")

    const barchartsDiv = divToAddTo.append("div").attr("class", "barChartsContainer")


    const leftBarChartG = barchartsDiv.append("svg").attr("class", "barChartSvg").append("g")
    createBarChart(leftBarChartG, barChartHeight, leftValues, leftColors)

    const rightBarChartG = barchartsDiv.append("svg").attr("class", "barChartSvg").append("g")
    createBarChart(rightBarChartG, barChartHeight, rightValues, rightColors)

}



function createBarChart(gElement, totalHeight, dataValues, colors) {
    const parts = dataValues.length;
    const sum = dataValues.reduce((accumulator, currentVal) => accumulator + currentVal)

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