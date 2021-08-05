function createStackedAreaChart(chartDiv, usableWidth, usableHeight, data) {

    const margin = { top: 10, right: 30, bottom: 30, left: 50 };
    const width = usableWidth - margin.left - margin.right;
    const height = usableHeight - margin.top - margin.bottom;

    const svg = chartDiv.append("svg")
        .attr("width", width + margin.left + margin.right)
        .attr("height", height + margin.top + margin.bottom)
        .append("g")
        .attr("transform",
            "translate(" + margin.left + "," + margin.top + ")");


    const keys = distributionChartColorSchemeOrder
    const colors = distributionChartColorScheme;


    //TODO: Ensure that the max is done correctly

    const stackedData = d3.stack()
        .keys(keys)
        (data)

    const x = d3.scaleLinear()
        .domain([0, d3.max(data, d => d.time)]).nice()
        .range([0, width])

    svg.append("g")
        .attr("transform", "translate(0," + height + ")") //position axis at the bottom
        .call(d3.axisBottom(x));

    const y = d3.scaleLinear()
        .domain([0, d3.max(stackedData[stackedData.length - 1], d => d[1])]).nice()
        .range([height, 0])
    svg.append("g")
        .call(d3.axisLeft(y));


    const areaChart = svg.append("g");

    const area = d3.area()
        .x(function(d) { return x(d.data.time); })
        .y0(function(d) { return y(d[0]); })
        .y1(function(d) { return y(d[1]); })

    //add the area
    areaChart
        .selectAll("compartments")
        .data(stackedData)
        .enter()
        .append("path")
        .style("fill", function(d) {
            const index = keys.indexOf(d.key);
            return colors[index];
        })
        .attr("d", area)
}