import * as d3 from 'd3';
import { sort } from 'd3';
import regeneratorRuntime from "regenerator-runtime"; //Needed for generator functions. 

//Paper for the algorithm of calculating it: Visualizing a million time series with the density line chart
//Original github: //https://github.com/domoritz/line-density
//Used code from https://observablehq.com/@twitter/time-series-density-plot



export function makeRtPlot(divToAddTo: d3.Selection<d3.BaseType, unknown, HTMLElement, undefined>, rtValues: number[][], width: number, height: number) {

    //Delete the previous
    d3.select("#canvasContainer").remove();

    let maxX = getReasonableMaxXValue(rtValues);
    //trim data series to the maximal length
    for(let i in rtValues)
    {
        let rtPerTimeStep = rtValues[i];
        let trimmedArray = rtPerTimeStep.slice(0,maxX+1);

        rtValues[i] = trimmedArray;
    }
    

    let density = seriesDensity(width, height)
    // .yDomain([0, maxY]).xDomain(0, maxX)
    let plot = densityPlot(density);
    let divElement = plot(rtValues);

    divToAddTo.node().appendChild(divElement);
}

function getReasonableMaxXValue(rtValues: number[][]): number {

    let lengthCount: number[] = [];
    for (let valueArray of rtValues) {
        for (let i in valueArray) {
            if (lengthCount[i] == undefined) {
                lengthCount[i] = 0;
            }
            if (valueArray[i] != 0) {
                lengthCount[i] = lengthCount[i] + 1;
            }

        }
    }


    let lastNonZeroI = 0;
    let zeroCount = 0;
    for (let i in lengthCount) {
        const val = lengthCount[i];
        if (val == 0) {
            zeroCount++;
        } else {
            zeroCount = 0;
            lastNonZeroI = parseInt(i);
        }

        //encountering too many zero's. Cut it off here as this is a data error.
        if (zeroCount > 5 && lastNonZeroI != 0) {
            return lastNonZeroI;
        }
    }
    //Return one later so the line goes back to zero, but don't go overlength
    return Math.min(lastNonZeroI+1,lengthCount.length);
}


function seriesDensity(xBins: number, yBins: number) {
    let x0 = d => d.x0 || 0,
        ys = d => d,
        xDomain: [number, number],
        yDomain: [number, number],
        arcLengthNormalize = true;

    let ret: Float64Array = (data: number[]) => {
        if (!Number.isInteger(xBins))
            throw new Error(`xBins must be an integer (got ${xBins})`);
        if (!Number.isInteger(yBins))
            throw new Error(`yBins must be an integer (got ${yBins})`);
        if (!xBins || !yBins)
            throw new Error(
                "computing density requires nonzero values for both xBins and yBins."
            );
        return renderSeries(data, ret.options(data));
    }

    ret.options = (data: number[]) => {
        return {
            xBins,
            yBins,
            x0,
            ys,
            xDomain: xDomain || ret.defaultXDomain(data),
            yDomain: yDomain || ret.defaultYDomain(data),
            arcLengthNormalize,
        };
    };
    ret.xBins = function (_) {
        return arguments.length ? ((xBins = _), ret) : xBins;
    };
    ret.yBins = function (_) {
        return arguments.length ? ((yBins = _), ret) : yBins;
    };
    ret.x0 = function (_) {
        return arguments.length ? ((x0 = _), ret) : x0;
    };
    ret.ys = function (_) {
        return arguments.length ? ((ys = _), ret) : ys;
    };
    ret.xDomain = function (_) {
        return arguments.length ? ((xDomain = _), ret) : xDomain;
    };
    ret.yDomain = function (_) {
        return arguments.length ? ((yDomain = _), ret) : yDomain;
    };
    ret.arcLengthNormalize = function (_) {
        return arguments.length ?
            ((arcLengthNormalize = _), ret) :
            arcLengthNormalize;
    };
    // optimization opportunity: compute the extents in one pass
    ret.defaultXDomain = (data: number[]) => [
        d3.min(data, x0),
        d3.max(data, (series, i, a) => x0(series, i, a) + ys(series, i, a).length) -
        1
    ];
    ret.defaultYDomain = (data: number[]) => [
       0,// d3.min(data, series => d3.min(ys(series))),
        d3.max(data, series => d3.max(ys(series)))
    ];
    ret.copy = function (_) {
        return seriesDensity(xBins, yBins)
            .x0(x0)
            .ys(ys)
            .xDomain(xDomain)
            .yDomain(yDomain)
            .arcLengthNormalize(arcLengthNormalize)
    };
    return ret;
}

// high-level convenience for rendering a plot, canvas with axes
function densityPlot(density) {

    //interpolate but use the higher values or oranges
    function interpolateHighOranges(value: number){
        let offset = 0.4;
        let val = offset+value/offset;
        return d3.interpolateOranges(val)
    }

    let interpolator = cacheInterpolator(interpolateHighOranges);
    let color = buf => d3.scaleSequential(d3.extent(buf), interpolator);
    let background = "white";
    let drawAxes = true;
    let xAxisScale, yAxisScale;
    let ret = (...data) => {
        // note: margins take up extra space, in addition to width and height.
        let margin = { left: 30, top: 5, right: 0, bottom: 25 };

        let dense = density.copy();
        let xBins = dense.xBins();
        let yBins = dense.yBins();

        let [width, height] = [xBins, yBins];

        if (drawAxes) {
            // this isn't great, but we need to do this so that everything still fits on screen.
            // The optional preserveCanvasSize option will prevent the canvas from being resized, so that
            // `size` will refer to canvas size rather than the total size take by the chart.
            if (!drawAxes.preserveCanvasSize) {
                width = width - margin.left - margin.right;
                height = height - margin.top - margin.bottom;
            }
        }



        let canvas = d3.create("canvas").node();
        // let canvas = document.getElementById("canvasTest");

        canvas.setAttribute("width", xBins);
        canvas.setAttribute("height", yBins);



        let ctx = canvas.getContext('2d');
        canvas.style.width = `${width}px`;
        canvas.style.height = `${height}px`;
        // canvas.style.imageRendering = 'pixelated'; //No idea why this was in here, likely for truly large data, not needed in our case
        // ctx.imageSmoothingEnabled = false;

        let container = d3.create('div').attr("id", "canvasContainer");

        // We need to know the x and y extents in order to draw axes.
        // If none were passed, compute them, and set them explicitly
        // on our copy of `density` to avoid recomputing them later.
        if (!dense.xDomain()) {
            let domains = data.map(data => dense.defaultXDomain(data));
            dense.xDomain([
                d3.min(domains, values => d3.min(values)),
                d3.max(domains, values => d3.max(values))
            ]);
        }
        if (!dense.yDomain()) {
            let domains = data.map(data => dense.defaultYDomain(data));
            dense.yDomain([
                d3.min(domains, values => d3.min(values)),
                d3.max(domains, values => d3.max(values))
            ]);
        }

        let xAxisG, yAxisG;
        if (drawAxes) {
            container
                .style('position', 'relative')
                .style('width', width + margin.left + margin.right + 'px')
                .style('height', height + margin.bottom + margin.top + 'px');

            let axesSel = container
                .append('svg')
                .attr('width', width + margin.left + margin.right)
                .attr('height', height + margin.bottom + margin.top)
                .style('position', 'absolute')
                .style('z-index', '0')
                .style('overflow', 'visible');

            xAxisG = axesSel
                .append('g')
                .attr('transform', `translate(${margin.left}, ${height + margin.top})`);

            yAxisG = axesSel
                .append('g')
                .attr('transform', `translate(${margin.left}, ${margin.top})`);

            ctx.canvas.style.width = width + 'px';
            ctx.canvas.style.height = height + 'px';

            d3.select(container.node().appendChild(ctx.canvas))
                .style('position', 'absolute')
                .style('z-index', '1')
                .style(
                    'left',
                    `${margin.left + 1 /* avoid overlapping the vertical y axis line */
                    }px`
                )
                .style('top', `${margin.top}px`);
        }

        let render = buffers => {
            if (drawAxes) {
                xAxisG.call(
                    d3.axisBottom(
                        xAxisScale ?
                            xAxisScale.copy().range([0, width]) :
                            d3.scaleLinear(dense.xDomain(), [0, width])
                    )
                );
                yAxisG.call(
                    d3.axisLeft(yAxisScale ?
                        yAxisScale.copy().range([height, 0]) :
                        d3.scaleLinear(dense.yDomain(), [height, 0])
                    )
                );
            }
            let colorScale = color(...buffers);
            let values = new Array(buffers.length).fill(0.0);

            // Determine whether the color scale returns color strings or objects.
            // If the scale returns objects, it is assumed that they have {r, g, b} properties.
            // This allows us to avoid the overhead of parsing color strings.
            let colorScaleReturnsString = typeof colorScale(...values) == 'string';

            // Fill the canvas with the background color, or the zero color for the scale
            // if no background color was specified
            ctx.fillStyle = background || d3.rgb(colorScale(...values)).toString();
            ctx.fillRect(0, 0, xBins, yBins);
            let img = ctx.getImageData(0, 0, xBins, yBins);
            let imgData = img.data;

            for (let x = 0; x < xBins; x++) {
                for (let y = 0; y < yBins; y++) {
                    let draw = false; // whether to draw this pixel
                    // plot data is column-major, image data is row-major
                    for (let i = 0; i < buffers.length; i++) {
                        let value = buffers[i][yBins * x + y];
                        values[i] = value;
                        if (value) draw = true;
                    }
                    if (!draw) continue;

                    let c = colorScaleReturnsString ?
                        d3.rgb(colorScale(...values)) :
                        colorScale(...values);
                    let i = xBins * y + x;
                    imgData[4 * i] = c.r;
                    imgData[4 * i + 1] = c.g;
                    imgData[4 * i + 2] = c.b;
                    imgData[4 * i + 3] = 255 * c.opacity;
                }
            }
            ctx.putImageData(img, 0, 0);
            let node = drawAxes ? container.node() : ctx.canvas;
            return node;
        };

        // idea: something with requestAnimationFrame to do the next batch,
        // making our actual structure push-based...

        let update = (...data) => {
            let results = Array.from(data, data => dense(data));
            return render(results);
        };

        return update(...data);
    };
    // can we make the chart once, then incrementally re-render the data?
    // (keeping the same canvas across datasets)
    ret.density = function (_) {
        return arguments.length ? ((density = _), ret) : density;
    };
    ret.size = function (_) {
        return arguments.length ? ((size = _), ret) : size;
    };
    ret.color = function (_) {
        return arguments.length ? ((color = _), ret) : color;
    };
    ret.background = function (_) {
        return arguments.length ? ((background = _), ret) : background;
    };
    ret.drawAxes = function (_) {
        return arguments.length ? ((drawAxes = _), ret) : drawAxes;
    };
    ret.xAxisScale = function (_) {
        return arguments.length ? ((xAxisScale = _), ret) : xAxisScale;
    };
    ret.yAxisScale = function (_) {
        return arguments.length ? ((yAxisScale = _), ret) : yAxisScale;
    };
    // ...
    return ret;
}


function binScale(a: number, b: number, nbins) {
    // Returns a scale function with domain [a, b] and integer output range [0, nbins - 1]
    // - the number of bins should be a 32-bit integer, since we use | for a fast floor operation.
    // - d should never be NaN, since the bitwise operation will incorrectly turn it into a zero.
    let eps = 1e-6;
    // this factor scales the range [a, b] to [0, nbins - eps]
    let factor = (nbins - eps) / (b - a);
    return d => ((d - a) * factor) | 0;
}

// bresenham's line algorithm. code adapted from
// https://observablehq.com/@mbostock/bresenhams-line-algorithm
// note: this bresenham implementation assumes integer coordinates
function plotLine(x0: number, y0: number, x1: number, y1: number, plot) {
    if (Math.abs(y1 - y0) < Math.abs(x1 - x0)) {
        if (x0 > x1) plotLineLow(x1, y1, x0, y0, plot);
        else plotLineLow(x0, y0, x1, y1, plot);
    } else {
        if (y0 > y1) plotLineHigh(x1, y1, x0, y0, plot);
        else plotLineHigh(x0, y0, x1, y1, plot);
    }
}

function plotLineHigh(x0: number, y0: number, x1: number, y1: number, plot) {
    let dx = x1 - x0;
    let dy = y1 - y0;
    let xi = dx < 0 ? ((dx = -dx), -1) : 1;
    let D = 2 * dx - dy;
    for (let x = x0, y = y0; y <= y1; ++y, D += 2 * dx) {
        plot(x, y);
        if (D > 0) (x += xi), (D -= 2 * dy);
    }
}


function plotLineLow(x0: number, y0: number, x1: number, y1: number, plot) {
    let dx = x1 - x0;
    let dy = y1 - y0;
    let yi = dy < 0 ? ((dy = -dy), -1) : 1;
    let D = 2 * dy - dx;
    for (let x = x0, y = y0; x <= x1; ++x, D += 2 * dy) {
        plot(x, y);
        if (D > 0) (y += yi), (D -= 2 * dx);
    }
}

// Convenience function to create a cached color interpolator that
// returns cached rgb objects, avoiding color string parsing.
function cacheInterpolator(interpolator, n = 250) {
    return d3.scaleQuantize(d3.quantize(pc => d3.rgb(interpolator(pc)), n))
}

function renderSeries(data: number[], options): Float64Array {
    let {
        xBins,
        yBins,
        x0,
        ys,
        xDomain,
        yDomain,
        normalize
    } = options;

    // note: the buffers represent data in column-major order since we sum by column
    let buffer = new Float64Array(xBins * yBins);
    let tmpBuf = new Int8Array(xBins * yBins);
    let tmpSums = new Int32Array(xBins); // note: assumes no more than 2.1 billion weight per pixel
    let tmpMinY = new Int32Array(xBins);
    let tmpMaxY = new Int32Array(xBins);
    let tmp = { tmpBuf, tmpSums, tmpMinY, tmpMaxY };
    let xScale = binScale(xDomain[0], xDomain[1], xBins);
    let yScale = binScale(yDomain[1], yDomain[0], yBins);
    let i = 0;
    for (let series of data) {
        renderSingleSeries(
            x0(series, i, data),
            ys(series, i, data),
            options,
            xScale,
            yScale,
            tmp,
            buffer
        );
        i += 1;
    }
    return buffer;
}

function renderSingleSeries(
    x0,
    ys,
    options,
    xScale,
    yScale, { tmpBuf: tmp, tmpSums: sums, tmpMinY: minY, tmpMaxY: maxY },
    ret: Float64Array
) {
    let { xBins, yBins, arcLengthNormalize } = options;
    if (ys.length < 2) return ret;
    let { max, min } = Math;
    let prevX = xScale(x0);
    let prevY = yScale(ys[0]);

    // prepare our temporary buffers
    tmp.fill(0);
    sums.fill(0);
    minY.fill(yBins - 1);
    maxY.fill(0);

    // render all interpolated lines (between adjacent points) to the temp canvas
    // note: the current bresenham implementation assumes integer coordinates.
    let curInBounds = 0 <= prevX && prevX < xBins && 0 <= prevY && prevY < yBins;
    let prevInBounds;
    for (let i = 1; i < ys.length; i++) {
        let curX = xScale(x0 + i);
        let curY = yScale(ys[i]); // perf todo: this could apply a constant increment?
        // this bounds check prevents rendering NaN values as well as datapoints out of bounds
        prevInBounds = curInBounds;
        curInBounds = 0 <= curX && curX < xBins && 0 <= curY && curY < yBins;
        let inBounds = prevInBounds || curInBounds;
        if (inBounds || nonnegative(prevY) != nonnegative(curY)) {
            plotLine(prevX, prevY, curX, curY, (x, y) => {
                // plot only in-bounds pixels
                if (0 <= x && x < xBins && 0 <= y && y < yBins) {
                    sums[x] += tmp[yBins * x + y] == 0;
                    tmp[yBins * x + y] = 1;
                    minY[x] = min(minY[x], y);
                    maxY[x] = max(maxY[x], y);
                }
            });
        }
        prevX = curX;
        prevY = curY;
    }

    // normalize the temp canvas by column sums and add to ret canvas
    for (let x = 0; x < xBins; x++) {
        let sum = sums[x];
        if (sum > 0) {
            let scale = arcLengthNormalize ? 1 / sum : 1;
            let lo = yBins * x + minY[x];
            let hi = yBins * x + maxY[x] + 1;
            for (let i = lo; i < hi; i++) ret[i] += tmp[i] * scale;
        }
    }

    return ret;
}

function nonnegative(x) { x > 0 }