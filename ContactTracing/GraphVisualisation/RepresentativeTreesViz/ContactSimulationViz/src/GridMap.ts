import * as d3 from 'd3';

let gridToSvgMap = new Map();


export function initGridMap(gridNames) {
    const sidePanelDiv = d3.select("#sidePanel");
    const gridMapDiv = sidePanelDiv.append("div").attr("id", "gridmap");
    const gridMapGrid = gridMapDiv.append("svg").attr("id", "gridmapGrid");



    const rows = gridNames.length;
    const columns = gridNames[0].length;

    const topCellWidth = 0.6 / columns;
    const topCellHeight = 0.6 / rows;

    const spacing = Math.min(topCellWidth / columns * 0.5, topCellHeight / rows * 0.5);

    const botCellWidth = topCellWidth * topCellWidth;
    const botCellHeight = topCellHeight * topCellHeight;

    //make the svg fit the grid exactly. Spacing 
    gridMapGrid.attr("viewBox", [0, 0, 1 + spacing * (columns - 1), 1 + spacing * (rows - 1)])

    for (let rowI = 0; rowI < rows; rowI++) {
        for (let columnI = 0; columnI < columns; columnI++) {
            //make top level cell
            const originName = gridNames[rowI][columnI];
            if (originName !== "Empty") {
                const topX = columnI * (topCellWidth + spacing);
                const topY = rowI * (topCellHeight + spacing);

                //draw top level
                const topSquareGroupSvg = generateSquareGroup(gridMapGrid, topX, topY, topCellWidth, topCellHeight, "topLevelGridCell");
                gridToSvgMap.set(originName, topSquareGroupSvg);

            }
        }
    }
}



function generateSquareGroup(svg, x, y, width, height, className) {
    const g = svg.append("g")
    const square = g.append("rect")
        .attr("x", x)
        .attr("y", y)
        .attr("width", width)
        .attr("height", height)
        .attr("class", className)
        .attr("fill", "#FEFEFE"); //default color

    const text = g.append("text")
        .attr("x", x + width / 2)
        .attr("y", y + height / 2)
        .attr("text-anchor", "middle")
        .attr("dominant-baseline", "middle")
        .attr("font-size", "0.002em")


    return g;
}


/**
 * Takes as input a number of d3 trees, and updates the the od-map based on the frequence 
 * @param {*} trees 
 */
function updateGridMapFromTrees(startTime, endTime) {
    let gridCount = new Map();

    let totalCount = 0;
    for (let metaData of metaDataFromNodeById.values()) {
        const name = metaData.location;

        //Only show values between the start and end time
        const positiveTestTime = metaData.positiveTestTime;
        if (positiveTestTime < startTime || positiveTestTime > endTime) {
            continue;
        }


        let count = 0;
        if (gridCount.has(name)) {
            count = gridCount.get(name);
        }
        count = count + 1;
        gridCount.set(name, count);

        totalCount++;
    }
    console.log("total nodes in grid: " + totalCount);


    updateGridMapFromMap(gridCount);
}


function updateGridMapFromMap(gridCount) {
    const values = Array.from(gridCount.values())
    const [colorScheme, colorSchemeValues] = getColorScheme("integer", values);

    const maxVal = Math.max(...values)
    for (const [name, value] of gridCount.entries()) {
        const colorIndex = getIndexInColorScheme(value, "integer", colorSchemeValues)
        const color = colorScheme[colorIndex];
        updateGridMap(name, color, value);
    }
}


function updateGridMap(name, color, value) {
    if (gridToSvgMap.has(name)) {
        gridToSvgMap.get(name).select("rect").attr("fill", color)
        gridToSvgMap.get(name).select("text").text(value)
    } else {
        // console.log("can't find " + name);
    }
}