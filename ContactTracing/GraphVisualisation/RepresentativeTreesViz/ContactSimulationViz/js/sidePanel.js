function createSidePanel() {
    createSelectors();
    createDistributionChartPanel();
    createColorLegends();
}


function createSelectors() {
    const selectorDiv = d3.select("#sidePanel").append("div")
        .attr("id", "SelectorDiv")
        .attr("class", "SidePanelPanelDiv");

    createDistanceSlider(selectorDiv);
    createSizeSlider(selectorDiv);
    createNodeColorSelectors(selectorDiv);
    createPolicySelectors(selectorDiv);
    createAppPercentageSelectors(selectorDiv);
    // createSortOptions(selectorDiv);
    createRecalculateButton(selectorDiv);
}


function createDistanceSlider(selectorDiv) {

    createSlider(selectorDiv, "DistanceSlider", "Rt tree distance", 0, 99, initEditDistanceSliderVal)

    d3.select("#DistanceSlider")
        .on("input", function() {
            currentEditDistance = parseInt(this.value); //keep the value up to date
            updateSliderPreview() //Show a preview
            d3.select("#DistanceSliderNumber").text(this.value);
            changePending();
        })

    //create it at the end of the sliderdiv so the slider aligns with the scented widget
    createScentedRtLineChart(selectorDiv.select("#DistanceSliderdiv"), initEditDistanceSliderVal);
}

function createSizeSlider(selectorDiv) {

    createSlider(selectorDiv, "SizeSlider", "Node Size", 1, 10, nodeBaseSize)

    d3.select("#SizeSlider")
        .on("input", function() {
            setVizSizes(parseInt(this.value)); //update all the sizes that are dependent on node size


            d3.select("#SizeSliderNumber").text(this.value);
            recalculate = true;
            changePending();
        })
}


function createNodeColorSelectors(selectorDiv) {

    selectorDiv.append("p")
        .attr("class", "text title")
        .text("Node Property")

    createLeftRightSubtitles(selectorDiv);

    //get the properties of the selectors
    const colorOptions = [
        { "NAME": "None" },
        { "NAME": "Infection Location" },
        { "NAME": "Infection Time" },
        { "NAME": "Age" },
        { "NAME": "Infector State" }
    ];

    const leftChangeFunction = function() {
        currentLeftColor = this.value; //keep the color up to date
        changePending();
    };

    const rightChangeFunction = function() {
        currentRightColor = this.value; //keep the color up to date
        changePending();
    };

    createLeftRightComboBoxes(selectorDiv, colorOptions, "leftNodeColorSelector", "rightNodeColorSelector", currentLeftColor, currentRightColor, leftChangeFunction, rightChangeFunction);
}


function createPolicySelectors(selectorDiv) {

    selectorDiv.append("p")
        .attr("class", "text title")
        .text("Policy")

    createLeftRightSubtitles(selectorDiv);

    //get the properties of the selectors
    var colorOptions = [
        { "NAME": "None", },
        { "NAME": "1a", },
        { "NAME": "1b", },
        { "NAME": "1cX1Y3", },
        { "NAME": "1cX3Y3", },
        { "NAME": "1cX7Y3", },
        { "NAME": "1cX14Y3", },
        { "NAME": "1cX1Y7", },
        { "NAME": "1cX3Y7", },
        { "NAME": "1cX7Y7", },
        { "NAME": "1cX14Y7", },
        { "NAME": "1cX1Y14", },
        { "NAME": "1cX3Y14", },
        { "NAME": "1cX7Y14", },
        { "NAME": "1cX14Y14", },
        // { "NAME": "1x", }
    ];

    const leftChangeFunction = function() {
        currentLeftPolicy = this.value; //keep the policy up to date
        changePending();
    }

    const rightChangeFunction = function() {
        currentRightPolicy = this.value; //keep the policy up to date
        changePending();
    }


    const policySplitCheckBoxFunction = function() {
        splitPolicy = this.checked;
        changePending();
    };

    const comboBoxDiv = selectorDiv.append("div")
        .attr("class", "comboBoxesDiv")

    createComboBox(comboBoxDiv, "leftPolicySelector", colorOptions, currentLeftPolicy, leftChangeFunction);
    createCheckBox(comboBoxDiv, "policySplitCheckbox", false, policySplitCheckBoxFunction, "Detailed")
    createComboBox(comboBoxDiv, "rightPolicySelector", colorOptions, currentRightPolicy, rightChangeFunction);
}



function createAppPercentageSelectors(selectorDiv) {

    selectorDiv.append("p")
        .attr("class", "text title")
        .text("App percentage")

    createLeftRightSubtitles(selectorDiv);

    //get the properties of the selectors
    var colorOptions = [
        { "NAME": "0%" },
        { "NAME": "10%" },
        { "NAME": "20%" },
        { "NAME": "30%" },
        { "NAME": "40%" },
        { "NAME": "50%" },
        { "NAME": "60%" },
        { "NAME": "70%" },
        { "NAME": "80%" },
        { "NAME": "90%" },
        { "NAME": "100%" }
    ];

    const leftChangeFunction = function() {
        const appPercentage = this.value.substring(0, this.value.length - 1); //remove % sign
        currentLeftAppPercentage = appPercentage; //keep the appPercentage up to date
        changePending();
    }

    const rightChangeFunction = function() {
        const appPercentage = this.value.substring(0, this.value.length - 1); //remove % sign
        currentRightAppPercentage = appPercentage; //keep the appPercentage up to date
        changePending();

    }

    createLeftRightComboBoxes(selectorDiv, colorOptions, "leftAppPercentageSelector", "rightAppPercentageSelector", currentLeftAppPercentage + "%", currentRightAppPercentage + "%", leftChangeFunction, rightChangeFunction);
}

function createSortOptions(selectorDiv) {

    const sortDiv = selectorDiv.append("div")
        .attr("class", "sortDiv")

    sortDiv.append("p")
        .attr("class", "text subtitle")
        .text("Sort")

    const sortEnabledChangeFunction = function() {
        sortEnabled = this.checked; //keep it updated
        changePending();
    };

    createCheckBox(sortDiv, "sortCheckBox", sortEnabled, sortEnabledChangeFunction);


    sortDiv.append("p")
        .attr("class", "text subtitle")
        .text("by")

    const comboOptions = [
        { "NAME": "Tree size" },
        { "NAME": "Difference" },
        { "NAME": "Root width" }
    ]

    const sortByChangeFunction = function() {
        sortBy = this.value; //keep it updated
        changePending();
    };

    createComboBox(sortDiv, "sortComboBox", comboOptions, sortBy, sortByChangeFunction);
}

function createRecalculateButton(selectorDiv) {

    const recalculateDiv = selectorDiv.append("div")
        .attr("class", "recalculateDiv")

    const text = "Press to recalculate";

    const recalculateFunction = function() {
        updateAll();
    };

    createButton(recalculateDiv, "recalculateButton", text, recalculateFunction)
}




function createColorLegends() {
    const colorLegendDiv = d3.select("#sidePanel")
        .append("div")
        .attr("id", "colorLegendDiv")
        .attr("class", "colorLegend SidePanelPanelDiv")

    updateColorLegend();
}

function updateColorLegend() {

    const colorLegendDiv = d3.select("#sidePanel").select("#colorLegendDiv");
    colorLegendDiv.selectAll("*").remove(); //remove current legend
    createStateColorLegend(colorLegendDiv, true);
    createStateColorLegend(colorLegendDiv, false);
}

function createStateColorLegend(colorLegendDiv, isLeft) {
    const halfColorDiv = colorLegendDiv.append("div")
        .attr("class", "halfColorLegendDiv")


    let startI = 0;

    //get the colorname and policy name
    let currentColor, currentPolicy;
    if (isLeft) {
        currentColor = currentLeftColor;
        currentPolicy = currentLeftPolicy;
    } else {
        currentColor = currentRightColor;
        currentPolicy = currentRightPolicy;
    }

    //get the colors and names to display
    let colors, names
    if (currentColor == "Infector State") { //Color arc by infector state
        colors = infectionColorScheme;
        names = infectionColorSchemeOrderDisplay;
    } else if (currentColor == "None") {
        colors = noneColorScheme;
        names = noneColorSchemeOrderDisplay;
    } else if (currentColor == "Infection Location") {
        colors = locationColorScheme;
        names = locationColorSchemeOrderDisplay;
    } else if (currentColor == "Age") {
        colors = ageColorScheme;
        names = ageColorSchemeOrderDisplay;
    } else if (currentColor == "Infection Time") {
        colors = infectionTimeColorScheme;
        names = infectionTimeColorSchemeOrderDisplay;
    }

    if (currentPolicy == "None") {
        startI = 2;
    }




    for (let i = startI; i < names.length; i++) {
        const color = colors[i];
        let name = names[i];
        if (!splitPolicy) //If we aren't looking into the detailed split policy we merge them together
        {
            if (name == "Infection route prevented earlier") {
                //skip the detailed view
                continue;
            }
            if (name == "Contact avoided due to isolation") {
                //rename as it now represents all states
                name = "Infection route prevented";
            }
        }
        createStateColorLegendItem(color, name, isLeft, halfColorDiv);
    }

}

function createStateColorLegendItem(color, name, isLeft, divToAddTo) {
    const item = divToAddTo.insert("div")
        .attr("class", "colorLegendItem");



    item.insert("div")
        .attr("class", "colorLegendDot")
        .style("background-color", color);

    const p = item.insert("p")
        .text(name)
        .attr("class", "text colorLegendText")
        .style("text-align", "left")

    if (!isLeft) { //inverse direction of items if this is the right column
        item.style("flex-direction", "row-reverse");
        p.style("text-align", "right")
    }

}

function createDistributionChartPanel() {
    const distributionDiv = d3.select("#sidePanel").append("div")
        .attr("id", "distributionChartPanel")
        .attr("class", "distributionChartPanel SidePanelPanelDiv");


    distributionDiv.append("p").attr("class", "title text").text("Distribution of properties")

    createDistributionChartSelectors(distributionDiv);

    // createDistributionChart(distributionDiv);
    // createDistributionLegend(distributionDiv);

    createComponentBarChart(distributionDiv);
}

function createDistributionChartSelectors(divToAddTo) {

    const comboOptions = [
        { "NAME": "All" },
    ]

    const maxDepth = getMaxDepth();

    for (let i = 0; i < maxDepth; i++) {
        comboOptions.push({ "NAME": "Level " + i });
    }

    const selectLeftLevelFunction = function() {
        currentLeftDistributionSelection = [];
        for (let option of this.selectedOptions) {
            if (option.value == "All") {
                currentLeftDistributionSelection.push("All")
            } else {
                //take only the number. Represent as int for ease of manipulation later
                currentLeftDistributionSelection.push(parseInt(option.value.split(" ")[1]))
            }
        }
        updateGlobalChart();
    };

    const selectRightLevelFunction = function() {
        currentRightDistributionSelection = [];
        for (let option of this.selectedOptions) {
            if (option.value == "All") {
                currentRightDistributionSelection.push("All")
            } else {
                //take only the number. Represent as int for ease of manipulation later
                currentRightDistributionSelection.push(parseInt(option.value.split(" ")[1]))
            }
        }
        updateGlobalChart();
    };


    const normalizeCheckBoxFunction = function() {
        normalizeComponentChart = this.checked;
        updateGlobalChart();
    };
    const comboBoxDiv = divToAddTo.append("div").attr("class", "comboBoxesDiv")

    createComboBox(comboBoxDiv, "levelComboBox", comboOptions, sortBy, selectLeftLevelFunction, false);

    createCheckBox(comboBoxDiv, "normalizeCheckbox", false, normalizeCheckBoxFunction, "Normalized")

    createComboBox(comboBoxDiv, "levelComboBox", comboOptions, sortBy, selectRightLevelFunction, false);

}




function createDistributionLegend(distributionDiv) {
    const legend = distributionDiv.append("div")
        .attr("class", "distributionChartLegend");

    const names = distributionChartColorSchemeOrderDisplay;
    const colors = distributionChartColorScheme;
    for (let i = 0; i < names.length; i++) {
        const color = colors[i];
        const name = names[i];
        createStateColorLegendItem(color, name, true, legend);
    }
}

function createLeftRightSubtitles(sidePanelDiv) {

    const subTitleDiv = sidePanelDiv.append("div")
        .attr("class", "subtitleDiv");

    subTitleDiv.append("p")
        .attr("class", "text subtitle")
        .text("Left");

    subTitleDiv.append("p")
        .attr("class", "text subtitle")
        .text("Right");
}


function createLeftRightComboBoxes(divToAppendTo, colorOptions, leftId, rightId, leftInitColor, rightInitColor, leftChangeFunction, rightChangeFunction) {

    const comboBoxDiv = divToAppendTo.append("div")
        .attr("class", "comboBoxesDiv")

    createComboBox(comboBoxDiv, leftId, colorOptions, leftInitColor, leftChangeFunction);
    createComboBox(comboBoxDiv, rightId, colorOptions, rightInitColor, rightChangeFunction);
}

function createComboBox(divToAppendTo, id, valueList, initVal, changeFunction, multiple) {

    //attach the combobox
    const dropDown = divToAppendTo.append("select")
        .attr("class", "sidePanelComboBox")
        .attr("id", id);

    if (multiple) {
        dropDown.attr("multiple", "multiple")
    }

    const options = dropDown.selectAll("option")
        .data(valueList)
        .enter()
        .append("option")

    options
        .text(function(d) {
            return d.NAME;
        })
        .attr("value", function(d) {
            return d.NAME;
        })
        .property("selected", function(d) { return d.NAME === initVal; }); //set default value

    //attach the change function
    dropDown
        .on("change", changeFunction);

}

function createCheckBox(divToAppendTo, id, initVal, changeFunction, labelName) {

    if (labelName != undefined) {
        const label = divToAppendTo.append("label").text(labelName);
        divToAppendTo = label;
    }

    // const checkboxDiv = divToAppendTo
    //     .insert("div") //insert a div for the checkbox
    //     .attr("class", "checkdiv")

    //attach the checkbox itself
    const checkbox = divToAppendTo.append("input")
        .attr("class", "sidePanelCheckBox")
        .attr("id", id)
        .attr("type", "checkbox")
        .property("checked", initVal)
        .on("change", changeFunction);
}


function createButton(divToAppendTo, id, text, clickFunction) {

    const buttonDiv = divToAppendTo
        .insert("div") //insert combodiv before svg
        .attr("class", "buttonDiv")

    //attach the checkbox itself
    const button = buttonDiv.append("button")
        .attr("class", "button")
        .attr("id", id)
        .text(text)
        .on("click", clickFunction);
}


function createSlider(divToAppendTo, id, text, minVal, maxVal, initVal) {

    const sliderDiv = divToAppendTo
        .insert("div") //insert sliderdiv before svg
        .attr("id", id + "div")
        .attr("class", "sliderdiv")

    //text above slider
    sliderDiv.append("p")
        .attr("class", "text title")
        .text(text)

    //slider itself
    const slideContainer = sliderDiv.append("div")
        .attr("class", "slidecontainer")

    slideContainer.append("input")
        .attr("type", "range")
        .attr("class", "slider")
        .attr("id", id)
        .attr("min", minVal)
        .attr("max", maxVal)
        .attr("value", initVal)

    //attach the number behind the slider
    slideContainer.append("div")
        .attr("class", "slidernumber")
        .attr("id", id + "Number")
        .text(initVal)
}