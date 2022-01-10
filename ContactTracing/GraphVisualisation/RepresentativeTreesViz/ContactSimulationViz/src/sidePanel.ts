import * as d3 from 'd3';
import { createScentedRtLineChart } from './LineChart';
import { policyDataPresent } from './index';
import {
    setVizSizes,
    // nodeBaseSize, initEditDistanceSliderVal,
    // currentLeftAttributeName, currentRightAttributeName, setCurrentLeftAttributeName, setCurrentRightAttributeName,
    // currentLeftAttributeType, currentRightAttributeType, setCurrentLeftAttributeType, setCurrentRightAttributeType,
    // currentLeftColorScheme, currentRightColorScheme,
    // currentLeftColorSchemeValues, currentRightColorSchemeValues,
    // currentLeftAttributeBounds, currentRightAttributeBounds,
    vars
} from './vizVariables';
import { metaDataNames, metaDataTypes, getMaxDepth } from './dataQueries';
import { createComponentBarChart } from './BarChart';
import { changePending, updateAll, updateSliderPreview, setRecalculate, updateGlobalChart } from './updateFunctions';



export let currentLeftPolicy = "1a"; //what the current policy is for the left sides of the glyphs
export let currentRightPolicy = "1a"; //what the current policy is for the left sides of the glyphs
export let splitPolicy = false; //Whether to split to policy into infection route prevent and contact avoided.

export let currentLeftAppPercentage = "100"; //How many people have the app
export let currentRightAppPercentage = "100";



let sortEnabled = false;
let sortBy = "Tree size";

export function createSidePanel(repTreesData) {
    createSelectors(repTreesData);
    createDistributionChartPanel();
    createColorLegends();
}


function createSelectors(repTreesData) {
    const selectorDiv = d3.select("#sidePanel").append("div")
        .attr("id", "SelectorDiv")
        .attr("class", "SidePanelPanelDiv");

    createDistanceSlider(selectorDiv, repTreesData);
    createSizeSlider(selectorDiv);
    createNodeColorSelectors(selectorDiv);
    if (policyDataPresent) {
        createPolicySelectors(selectorDiv);
        createAppPercentageSelectors(selectorDiv);
    }
    // createSortOptions(selectorDiv);
    createRecalculateButton(selectorDiv);
}


function createDistanceSlider(selectorDiv, repTreesData) {

    createSlider(selectorDiv, "DistanceSlider", "Rt tree distance", 0, 99, vars.initEditDistanceSliderVal)

    d3.select("#DistanceSlider")
        .on("input", function () {
            vars.currentEditDistance = parseInt(this.value); //keep the value up to date
            updateSliderPreview() //Show a preview
            d3.select("#DistanceSliderNumber").text(this.value);
            changePending();
        })

    //create it at the end of the sliderdiv so the slider aligns with the scented widget
    createScentedRtLineChart(selectorDiv.select("#DistanceSliderdiv"), vars.initEditDistanceSliderVal, repTreesData);
}

function createSizeSlider(selectorDiv) {

    createSlider(selectorDiv, "SizeSlider", "Node Size", 1, 10, vars.nodeBaseSize)

    d3.select("#SizeSlider")
        .on("input", function () {
            setVizSizes(parseInt(this.value)); //update all the sizes that are dependent on node size


            d3.select("#SizeSliderNumber").text(this.value);
            setRecalculate();
            changePending();
        })
}


function createNodeColorSelectors(selectorDiv) {

    createLeftRightSubtitles(selectorDiv, "Node Property");

    let colorOptions = [{ "NAME": "None" }];
    for (let i = 0; i < metaDataNames.length; i++) {
        const name = metaDataNames[i]
        colorOptions[i + 1] = { "NAME": name } //0 is used by none
    }

    const leftChangeFunction = function () {
        vars.currentLeftAttributeName = this.value; //keep the color up to date
        let i = metaDataNames.indexOf(this.value);
        vars.currentLeftAttributeType = metaDataTypes[i];
        changePending();
    };

    const rightChangeFunction = function () {
        vars.currentRightAttributeName = this.value; //keep the color up to date
        let i = metaDataNames.indexOf(this.value);
        vars.currentRightAttributeType = metaDataTypes[i];
        changePending();
    };

    createLeftRightComboBoxes(selectorDiv, colorOptions, "leftNodeColorSelector", "rightNodeColorSelector", vars.currentLeftAttributeName, vars.currentRightAttributeName, leftChangeFunction, rightChangeFunction);
}


function createPolicySelectors(selectorDiv) {

    createLeftRightSubtitles(selectorDiv, "Policy");

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
    ];

    const leftChangeFunction = function () {
        currentLeftPolicy = this.value; //keep the policy up to date
        changePending();
    }

    const rightChangeFunction = function () {
        currentRightPolicy = this.value; //keep the policy up to date
        changePending();
    }


    const policySplitCheckBoxFunction = function () {
        splitPolicy = this.checked;
        changePending();
    };

    const comboBoxDiv = selectorDiv.append("div")
        .attr("class", "comboBoxesDiv")

    createComboBox(comboBoxDiv, "leftPolicySelector", colorOptions, currentLeftPolicy, leftChangeFunction, false);
    createCheckBox(comboBoxDiv, "policySplitCheckbox", false, policySplitCheckBoxFunction, "Detailed")
    createComboBox(comboBoxDiv, "rightPolicySelector", colorOptions, currentRightPolicy, rightChangeFunction, false);
}



function createAppPercentageSelectors(selectorDiv) {

    createLeftRightSubtitles(selectorDiv, "App percentage");

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

    const leftChangeFunction = function () {
        const appPercentage = this.value.substring(0, this.value.length - 1); //remove % sign
        currentLeftAppPercentage = appPercentage; //keep the appPercentage up to date
        changePending();
    }

    const rightChangeFunction = function () {
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

    const sortEnabledChangeFunction = function () {
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

    const sortByChangeFunction = function () {
        sortBy = this.value; //keep it updated
        changePending();
    };

    createComboBox(sortDiv, "sortComboBox", comboOptions, sortBy, sortByChangeFunction);
}

function createRecalculateButton(selectorDiv) {

    const recalculateDiv = selectorDiv.append("div")
        .attr("class", "recalculateDiv")

    const text = "Press to recalculate";

    const recalculateFunction = function () {
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

export function updateColorLegend() {
    const colorLegendDiv = d3.select("#sidePanel").select("#colorLegendDiv");
    colorLegendDiv.selectAll("*").remove(); //remove current legend
    createStateColorLegend(colorLegendDiv, true);
    createStateColorLegend(colorLegendDiv, false);
}

const maxIntegerBinsToDispaly = 10;

function createStateColorLegend(colorLegendDiv, isLeft) {
    const halfColorDiv = colorLegendDiv.append("div")
        .attr("class", "halfColorLegendDiv")

    //get the colors and names to display
    let colors, names, displayNames, type, bounds

    if (isLeft) {
        colors = vars.currentLeftColorScheme;
        names = vars.currentLeftColorSchemeValues;
        type = vars.currentLeftAttributeType;
        bounds = vars.currentLeftAttributeBounds;
    } else {
        colors = vars.currentRightColorScheme
        names = vars.currentRightColorSchemeValues;
        type = vars.currentRightAttributeType;
        bounds = vars.currentRightAttributeBounds;
    }
    //Copy the array to get the values
    displayNames = [...names]

    if (type == "integer") {
        displayNames[0] = bounds[0] + "-" + names[0];
        for (let i = 1; i < names.length; i++) {
            displayNames[i] = names[i - 1] + "-" + names[i];
        }
    }

    if (type == "date") {
        displayNames[0] = getDate(bounds[0]) + "-" + getDate(names[0]);
        for (let i = 1; i < names.length; i++) {
            displayNames[i] = getDate(names[i - 1]) + "-" + getDate(names[i]);
        }
    }

    for (let i = 0; i < displayNames.length; i++) {
        const color = colors[i];
        let name = displayNames[i];
        // if (!splitPolicy) //If we aren't looking into the detailed split policy we merge them together
        // {
        //     if (name == "Infection route prevented earlier") {
        //         //skip the detailed view
        //         continue;
        //     }
        //     if (name == "Contact avoided due to isolation") {
        //         //rename as it now represents all states
        //         name = "Infection route prevented";
        //     }
        // }
        createStateColorLegendItem(color, name, isLeft, halfColorDiv);
    }

}

function getDate(unixTimeStampInSeconds) {
    return new Date(unixTimeStampInSeconds * 1000).ddmmyyyy();
}

//convert date to human readable
Date.prototype.ddmmyyyy = function () {
    var mm = this.getMonth() + 1; // getMonth() is zero-based
    var dd = this.getDate();

    return [
        (dd > 9 ? '' : '0') + dd,
        (mm > 9 ? '' : '0') + mm,
        this.getFullYear(),
    ].join('.');
};


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

    const selectLeftLevelFunction = function () {
        vars.currentLeftDistributionSelection = [];
        for (let option of this.selectedOptions) {
            if (option.value == "All") {
                vars.currentLeftDistributionSelection.push("All")
            } else {
                //take only the number. Represent as int for ease of manipulation later
                vars.currentLeftDistributionSelection.push(parseInt(option.value.split(" ")[1]))
            }
        }
        updateGlobalChart();
    };

    const selectRightLevelFunction = function () {
        vars.currentRightDistributionSelection = [];
        for (let option of this.selectedOptions) {
            if (option.value == "All") {
                vars.currentRightDistributionSelection.push("All")
            } else {
                //take only the number. Represent as int for ease of manipulation later
                vars.currentRightDistributionSelection.push(parseInt(option.value.split(" ")[1]))
            }
        }
        updateGlobalChart();
    };


    const normalizeCheckBoxFunction = function () {
        vars.normalizeComponentChart = this.checked;
        updateGlobalChart();
    };
    const comboBoxDiv = divToAddTo.append("div").attr("class", "comboBoxesDiv")

    createComboBox(comboBoxDiv, "levelComboBox", comboOptions, sortBy, selectLeftLevelFunction, false);

    createCheckBox(comboBoxDiv, "normalizeCheckbox", false, normalizeCheckBoxFunction, "Normalized")

    createComboBox(comboBoxDiv, "levelComboBox", comboOptions, sortBy, selectRightLevelFunction, false);

}


function createLeftRightSubtitles(sidePanelDiv, title) {

    const subTitleDiv = sidePanelDiv.append("div")
        .attr("class", "subtitleDiv");

    subTitleDiv.append("p")
        .attr("class", "text subtitle")
        .text("Left");

    subTitleDiv.append("p")
        .attr("class", "text title")
        .text(title);

    subTitleDiv.append("p")
        .attr("class", "text subtitle")
        .text("Right");
}


function createLeftRightComboBoxes(divToAppendTo, colorOptions, leftId, rightId, leftInitColor, rightInitColor, leftChangeFunction, rightChangeFunction) {

    const comboBoxDiv = divToAppendTo.append("div")
        .attr("class", "comboBoxesDiv")

    createComboBox(comboBoxDiv, leftId, colorOptions, leftInitColor, leftChangeFunction, false);
    createComboBox(comboBoxDiv, rightId, colorOptions, rightInitColor, rightChangeFunction, false);
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
        .text(function (d) {
            return d.NAME;
        })
        .attr("value", function (d) {
            return d.NAME;
        })
        .property("selected", function (d) { return d.NAME === initVal; }); //set default value

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