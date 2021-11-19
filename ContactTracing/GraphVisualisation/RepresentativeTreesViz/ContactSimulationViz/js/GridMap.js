let odToSvgMap = new Map();


function initGridMap(gridNames) {
    const rows = gridNames.length;
    const columns = gridNames[0].length;

    const topCellWidth = 1 / columns;
    const topCellHeight = 1 / rows;

    const spacing = Math.min(topCellWidth / columns * 0.5, topCellHeight / rows * 0.5);

    const botCellWidth = topCellWidth * topCellWidth;
    const botCellHeight = topCellHeight * topCellHeight;

    const gridMapGrid = d3.select("#gridmapGrid");
    //make the svg fit the grid exactly. Spacing 
    gridMapGrid.attr("viewBox", [0, 0, 1 + spacing * (columns - 1), 1 + spacing * (rows - 1)])

    for (let rowI = 0; rowI < rows; rowI++) {
        for (let columnI = 0; columnI < columns; columnI++) {
            //make top level cell
            const originName = gridNames[rowI][columnI];
            if (originName !== "Empty") {
                const topX = columnI * (topCellWidth + spacing);
                const topY = rowI * (topCellHeight + spacing);

                //Draw lower level first to get correct outlines
                //make lower level cell
                for (let rowI2 = 0; rowI2 < rows; rowI2++) {
                    for (let columnI2 = 0; columnI2 < columns; columnI2++) {
                        const destinationName = gridNames[rowI2][columnI2];
                        if (destinationName !== "Empty") {
                            const botX = topX + (columnI2 * botCellWidth);
                            const botY = topY + (rowI2 * botCellHeight);

                            const bottomSquareSvg = generateSquare(gridMapGrid, botX, botY, botCellWidth, botCellHeight, "bottomLevelGridCell");

                            const name = originName + "-" + destinationName;
                            odToSvgMap.set(name, bottomSquareSvg);
                        }
                    }
                }
                //draw top level
                const topSquareSvg = generateSquare(gridMapGrid, topX, topY, topCellWidth, topCellHeight, "topLevelGridCell");
            }
        }
    }
}



function generateSquare(svg, x, y, width, height, className) {
    const square = svg.append("rect")
        .attr("x", x)
        .attr("y", y)
        .attr("width", width)
        .attr("height", height)
        .attr("class", className)
        .attr("fill", "lightgray"); //default color

    return square;
}

function updateOdMap(originDestinationName, color) {
    if (odToSvgMap.has(originDestinationName)) {
        odToSvgMap.get(originDestinationName).attr("fill", color)
    }
}

function updateOdMapFromMap(originDestinationValueMap) {
    const values = Array.from(originDestinationValueMap.values())

    const minValue = Math.min(...values);
    const maxValue = Math.max(...values);

    const [colorScheme, colorSchemeValues] = getColorScheme("integer", values);


    for (const [originDestination, value] of originDestinationValueMap.entries()) {
        const colorIndex = getIndexInColorScheme(value, "integer", colorSchemeValues)
        const color = colorScheme[colorIndex];
        updateOdMap(originDestination, color);
    }
}

/**
 * Takes as input a number of d3 trees, and updates the the od-map based on the frequence 
 * @param {*} trees 
 */
function updateODMapFromTrees(trees) {
    let odCount = new Map();


    for (let tree of trees) {
        const treeRoot = d3.hierarchy(tree);
        const links = treeRoot.links();
        for (let link of links) {
            const originId = link.source.data.id;
            const destinationid = link.target.data.id;

            //TODO: Make location instead of attribute1
            const origin = getMetaDataValueFromId("Location", originId);
            const destination = getMetaDataValueFromId("Location", destinationid);
            const name = origin + "-" + destination;


            let count = 0;
            if (odCount.has(name)) {
                count = odCount.get(name);
            }
            count = count + 1;
            odCount.set(name, count);
        }
    }
    updateOdMapFromMap(odCount);
}