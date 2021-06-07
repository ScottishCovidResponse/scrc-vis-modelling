/**
 * Returns an array of [x,y] offsets to layout the rectangles without overlap.
 * 
 * @param {Array of widths} widths 
 * @param {array of heights} heights 
 * @param {The horizontal margin after each node. Used when filtering out nodes} horMargins
 * @param {the maximum width we can use to layout} maxWidth
 */
function calculateOffsets(widths, heights, horMargins, maxWidth) {
    return snakeLayout(widths, heights, horMargins, maxWidth);
}


/**
 * Returns an array of [x,y] offsets to layout the rectangles without overlap in strips.
 * @param {Array of widths} widths 
 * @param {array of heights} heights 
 * @param {The horizontal margin after each node. Used when filtering out nodes} horMargins
 * @param {the maximum width we can use to layout} maxWidth

 */
function snakeLayout(inputWidths, inputHeights, horMargins, maxWidth) {
    //use snake pattern to remove jarring transitions from how the eye goes from one place to the other

    //add the margins to the widths and the heights so we now how much space each element takes
    const widths = addMargin(inputWidths, horMargins);
    const heights = addMarginConstant(inputHeights, verticalMarginBetweenTrees);


    //outputArray
    let outputPositions = [];

    //current offset
    let stripYOffset = 0;
    let currentXOffset = 0;

    let stripDirection = "Right";//whether we are currently laying the strip out towards the right or the left
    let stripStartIndex = 0;//holds the index of the element where the current strip starts
    let stripMaxHeight = 0;//maximum height found of an element in the strip
    let stripWidth = 0;//current width


    //go through the nodes
    for (let i = 0; i < widths.length; i++) {
        let width = widths[i];

        //update strip properties
        stripWidth += width;
        stripMaxHeight = Math.max(stripMaxHeight, heights[i]);



        //whether this is the lastelemnt
        let lastElement = (i + 1) == widths.length;

        //If this is the last elemet or the next element does not fit we need to lay this strip out
        if (lastElement || (stripWidth + widths[i + 1]) > maxWidth) {
            //layout the nodes in the strip            
            for (let j = stripStartIndex; j <= i; j++) {
                let extraYOffset = stripMaxHeight - heights[j];//if the tree is smaller, shift it down more to align at bottom

                let extraXOffset = 0;
                if (stripDirection == "Left") {
                    extraXOffset = -widths[j];//move it to the left by the width of this tree so that the element fits (coordinates at left bottom)
                    extraXOffset -= (maxWidth - stripWidth);//ensure it is always flush against the left side
                }
                outputPositions[j] = [currentXOffset + extraXOffset, stripYOffset + extraYOffset];

                //increase or decrease depending on direction
                if (stripDirection == "Right") {
                    currentXOffset += widths[j];
                } else {
                    currentXOffset -= widths[j];
                }
            }

            //initialize new strip
            stripYOffset += stripMaxHeight;//shift strip down
            stripWidth = 0;
            stripMaxHeight = 0;
            stripStartIndex = i + 1;//start the new strip at the next element
            if (stripDirection == "Right") {
                stripDirection = "Left";
                currentXOffset = maxWidth;
            } else {
                stripDirection = "Right";
                currentXOffset = 0;
            }
        }
    }
    return outputPositions;
}
/**
 * Add the margins to the values for easier calculation. Does not modify the original values
 * @param {*} inputValues 
 * @param {The horizontal margin after each node. Used when filtering out nodes} horMargins

 * @returns 
 */
function addMargin(inputValues, horMargins) {
    let outputValues = [];
    for (let i = 0; i < inputValues.length; i++) {
        outputValues[i] = inputValues[i] + horMargins[i];
    }
    return outputValues;

}


/**
 * Add the margins to the values for easier calculation. Does not modify the original values
 * @param {*} inputValues 
 * @param {The constand added margin} margin

 * @returns 
 */
function addMarginConstant(inputValues, margin) {
    let outputValues = [];
    for (let i = 0; i < inputValues.length; i++) {
        outputValues[i] = inputValues[i] + margin;
    }
    return outputValues;

}

