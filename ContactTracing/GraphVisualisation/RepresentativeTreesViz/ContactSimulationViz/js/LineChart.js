//Simple line chart including dataprocessing for underneath the R_t slider.

const treesPerSize = []; //for each R_t value (represented by the index), holds how many trees there are

function createScentedRtLineChart(chartDiv, scentIndex) {

    for (let i = 0; i < 100; i++) {
        treesPerSize[i] = 0;
    }

    for (let treeI = 0; treeI < repTreesData.length; treeI++) {
        //tree exists up to maxEditDistance
        for (let i = 0; i < repTreesData[treeI].maxEditDistance; i++) {
            treesPerSize[i] = treesPerSize[i] + 1;
        }
    }
    const width = 150 - 25; //offset by 25 for the length of the slider knob
    const height = 20;

    const lineChartDiv = chartDiv.append("div")
        .attr("id", "RtScentedChart")
        .attr("class", "LineChart")
        .style("margin-left", "12.5px")

    createLineChart(lineChartDiv, width, height, treesPerSize, scentIndex);
}


/**
 * 
 * @param {*} chartDiv 
 * @param {*} usableWidth 
 * @param {*} usableHeight 
 * @param {series of values corresponding to y axis} inputData 
 * @param {dataIndex for what is currently selected} scentIndex 
 */
function createLineChart(chartDiv, usableWidth, usableHeight, inputData, scentIndex) {
    const margin = { top: 0, right: 0, bottom: 0, left: 0 };
    const width = usableWidth - margin.left - margin.right;
    const height = usableHeight - margin.top - margin.bottom;

    const svg = chartDiv.append("svg")
        .attr("class", "LineChartSvg")
        .attr("width", width + margin.left + margin.right)
        .attr("height", height + margin.top + margin.bottom)
        .append("g")
        .attr("transform",
            "translate(" + margin.left + "," + margin.top + ")");

    //setup x and y scale functions
    var x = d3.scaleLinear()
        .range([0, width])
        .domain([0, inputData.length - 1])

    var y = d3.scaleLinear()
        .range([height, 0])
        .domain([0, d3.max(inputData)]);

    //x,y functions for the data base on axis
    var shape = d3.line()
        .x(function(d) { return x(d[0]); })
        .y(function(d) { return y(d[1]); });



    //get x,y positions for the SHAPE to fill. If filled=false this is just a line, otherwise we add points to fill underneat
    let data = [];
    let scentData = [];
    const dataLength = inputData.length;

    for (let i = 0; i < dataLength; i++) {
        data[i] = [i, inputData[i]];
    }
    scentData = data.slice(0, scentIndex);

    completeShape(data);
    completeShape(scentData)


    //append shape
    svg.append("path")
        .datum(data)
        .attr("class", "filledShape")
        .attr("d", shape);

    svg.append("path")
        .datum(scentData)
        .attr("class", "filledShapeBlue")
        .attr("d", shape);

}

/**
 * Completes the shape by drawing a value down from the last value, then through the origin, and finally back to the start. Modifies the array
 * @param {array of [x,y] values} data 
 */
function completeShape(data) {
    dataLength = data.length;
    data[dataLength] = [dataLength - 1, 0]; //go to 0 on the y-axis
    data[dataLength + 1] = [0, 0]; //go to 0,0
    data[dataLength + 2] = [0, data[0][1]]; //close the shape
}

function updateScentedLineChart(id, maxDataIndex) {

    for (let i = 0; i < maxDataIndex; i++) {
        data[i] = [i, treesPerSize[i]];
    }
    data[maxDataIndex] = [maxDataIndex - 1, 0]; //go to 0 on the y-axis
    data[maxDataIndex + 1] = [0, 0]; //go to 0,0 on the y-axis
    data[maxDataIndex + 2] = [0, inputData[0]]; //close the shape

    const svgLine = svg.append("path")
        .datum(data2)
        .attr("class", "scentedLine")
        .attr("d", line)
}