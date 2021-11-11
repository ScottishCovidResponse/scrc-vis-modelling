//Holds several methods to count how many nodes are in states



/**
 * Counts how many nodes that the node with id={id} represents were infected by a node with each state
// Order:[RemovedByIsolation,RemovedByPolicy, EXPOSED,ASYMPTOMATIC,PRESYMPTOMATIC,SYMPTOMATIC,SEVERELY_SYMPTOMATIC,RECOVERED,DEAD]
 * @param {} nodeId 
 * @param {If true, takes the trees that are represented by this node into account as well} isRepTree
 * @param {Which policy data to use} policy
 * @param {How much the appPercentage of the policy is} appPercentage
 */
function infectorStateCount(nodeId, isRepTree, policy, appPercentage) {
    let nodesMetaData;
    if (isRepTree) { //use all representative trees for the data
        nodesMetaData = getRepresentedNodesMetaData(nodeId, currentEditDistance);
    } else { //only use the tree itself
        nodesMetaData = [metaDataFromNodeById.get(nodeId)];
    }

    const count = getStateArray(nodesMetaData, infectionColorSchemeOrder, policy, appPercentage, getInfectorState);
    return count;
}

/**
 * Counts how many nodes that the node with id={id} represents were removed by a policy
 * Order:[RemovedByIsolation,RemovedByPolicy, notRemovedByPolicy]
 * @param {} nodeId 
 * @param {If true, takes the trees that are represented by this node into account as well} isRepTree
 * @param {Which policy data to use} policy
 * @param {How much the appPercentage of the policy is} appPercentage
 */
function noneCount(nodeId, isRepTree, policy, appPercentage) {
    let nodesWithMetaData;
    if (isRepTree) { //use all representative trees for the data
        nodesWithMetaData = getRepresentedNodesMetaData(nodeId, currentEditDistance);
    } else { //only use the tree itself
        nodesWithMetaData = [metaDataFromNodeById.get(nodeId)];
    }
    const count = getStateArray(nodesWithMetaData, noneColorSchemeOrder, policy, appPercentage, getNoneState);
    return count;
}



/**
 * Counts how many nodes that the node with id={id} represents were infected at a location
 * @param {} nodeId 
 * @param {If true, takes the trees that are represented by this node into account as well} isRepTree
 * @param {Which policy data to use} policy
 * @param {How much the appPercentage of the policy is} appPercentage
 */
function locationCount(nodeId, isRepTree, policy, appPercentage) {
    let nodesMetaData;
    if (isRepTree) { //use all representative trees for the data
        nodesMetaData = getRepresentedNodesMetaData(nodeId, currentEditDistance);
    } else { //only use the tree itself
        nodesMetaData = [metaDataFromNodeById.get(nodeId)];
    }

    const count = getStateArray(nodesMetaData, locationColorSchemeOrder, policy, appPercentage, getLocation);
    return count;
}

/**
 * Counts how many nodes that the node with id={id} represents are in a certain age group
 * @param {} nodeId 
 * @param {If true, takes the trees that are represented by this node into account as well} isRepTree
 * @param {Which policy data to use} policy
 * @param {How much the appPercentage of the policy is} appPercentage
 */
function ageCount(nodeId, isRepTree, policy, appPercentage) {
    let nodesMetaData;
    if (isRepTree) { //use all representative trees for the data
        nodesMetaData = getRepresentedNodesMetaData(nodeId, currentEditDistance);
    } else { //only use the tree itself
        nodesMetaData = [metaDataFromNodeById.get(nodeId)];
    }

    const count = getStateArray(nodesMetaData, ageColorSchemeOrder, policy, appPercentage, getAge);
    return count;
}


/**
 * Counts how many nodes that the node with id={id} represents are in a certain exposed time group
 * @param {} nodeId 
 * @param {If true, takes the trees that are represented by this node into account as well} isRepTree
 * @param {Which policy data to use} policy
 * @param {How much the appPercentage of the policy is} appPercentage
 */
function timeCount(nodeId, isRepTree, policy, appPercentage) {
    let nodesMetaData;
    if (isRepTree) { //use all representative trees for the data
        nodesMetaData = getRepresentedNodesMetaData(nodeId, currentEditDistance);
    } else { //only use the tree itself
        nodesMetaData = [metaDataFromNodeById.get(nodeId)];
    }

    const count = getStateArray(nodesMetaData, infectionTimeColorSchemeOrder, policy, appPercentage, getInfectionTime);
    return count;
}

/**
 * 
 * @param {*} nodesWithMetaData 
 * @param {*} stateNames 
 * @param {*} policy 
 * @param {*} appPercentage 
 * @param {*} stateFunction 
 * @returns 
 */
function getStateArray(nodesWithMetaData, stateNames, policy, appPercentage, stateFunction) {
    let count = new Array(maxParts).fill(0); //make array of the correct size filled with 0's

    for (const nodeMetaData of nodesWithMetaData) {
        const state = stateFunction(nodeMetaData);



        //which state this node has
        let stateIndex = -1;

        if (nodeMetaData.policies.length > 0) {
            stateIndex = stateIndex;
        }

        //policies are not a specific state, so do these seperatly
        if (isRemovedByPolicy(nodeMetaData, policy, appPercentage)) { //is the node removed by the policy
            stateIndex = stateNames.indexOf("removedByPolicy");
        }

        //Whether this node was the original of a removal chain
        if (isRemovedByPolicy(nodeMetaData, policy, appPercentage, true)) {
            stateIndex = stateNames.indexOf("removedByPolicyOrigin");
        }

        if (stateIndex == -1) { //not yet removed by a policy
            for (let j = 2; j < stateNames.length; j++) { //first 2 states are policies
                if (state.toUpperCase() == stateNames[j].toUpperCase()) { //compare without regard for capitalization
                    stateIndex = j;
                    break;
                }
            }
        }
        count[stateIndex] = count[stateIndex] + 1;
    }
    return count;
}


/**
 * Holds whether the node with the specified metadata is removed by the policy
 */
function isRemovedByPolicy(nodeMetaData, policy, appPercentage, origin = false) {
    for (const metaPolicy of nodeMetaData.policies) {

        //combine policyAndAppPercentage to get the right number
        let policyApp = policy;
        if (policy != "1x") { //1x policy doesn't have numbers
            policyApp += "A" + appPercentage;
        }
        if (origin) {
            policyApp += "Origin";
        }
        if (metaPolicy == policyApp) {
            return true;
        }
    }
    return false;
}

function isRemovedByIsolation(nodeMetaData) {
    return isRemovedByPolicy(nodeMetaData, "1x"); //1x contains all the nodes that are removed by isolation
}


/**
 * returns the state of the virus of the node at time t 
 * @param {the meta data of the node} nodeMetaData
 */
function getInfectorState(nodeMetaData) {
    const sourceId = nodeMetaData.sourceInfectionId;
    const time = nodeMetaData.exposedTime;

    const metaDataInfector = metaDataFromNodeById.get(sourceId);
    if (metaDataInfector == undefined) {
        console.log("No meta data available for node with id " + sourceId);
        return "";
    }
    const virusProgression = metaDataInfector.virusProgression;

    const lastVirusTime = getLastVirusTimeBeforeTime(virusProgression, time);

    const stateAtExposedTime = virusProgression[lastVirusTime];
    return stateAtExposedTime;
}

/**
 * Virusprogression is sorted
 * @param {} virusProgression 
 * @param {*} time 
 * @returns 
 */
function getLastVirusTimeBeforeTime(virusProgression, time) {
    let lastVirusTime = '0.0';
    for (const virusTime in virusProgression) {
        //need to convert everything to floats
        const virusTimeF = parseFloat(virusTime)
        if (virusTimeF <= time) { //this virusevent happened before or at the time we are interested in
            lastVirusTime = virusTime;
        } else {
            break; //sorted, so can't find a new one anymore
        }
    }
    return lastVirusTime;
}


/**
 * returns whether this is an intial node or an other node
 * @param {the meta data of the node} nodeMetaData
 */
function getNoneState(nodeMetaData) {
    let state = getInfectorState(nodeMetaData);
    if (state.toUpperCase() != "initial".toUpperCase()) { //root node
        state = "Other";
    }
    return state;
}

/**
 * returns the location of the node, parsed to fall into one of the specified categories
 * @param {*} nodeMetaData 
 */
function getLocation(nodeMetaData) {
    let state = nodeMetaData.infectionLocation;

    //collapse familystates
    if (familyStates.indexOf(state) != -1) {
        state = "Family";
    }

    //if not one of the specified state, it is an other strate
    if (!(locationColorSchemeOrder.includes(state))) {
        state = "Other";
    }

    return state;
}

/**
 * Returns the age group of the node
 */
function getAge(nodeMetaData) {
    let age = nodeMetaData.age

    for (let i = 0; i < 10; i++) {
        const bottomAgeRange = i * 20;
        const topAgeRange = (i + 1) * 20;
        if (bottomAgeRange <= age && age < topAgeRange) {
            age = bottomAgeRange + "-" + topAgeRange;
        }
    }
    return age;
}

/**
 * Returns the age group of the node
 */
function getInfectionTime(nodeMetaData) {
    let time = nodeMetaData.exposedTime


    if (time < 5) {
        time = "0-5";
    } else if (time < 10) {
        time = "5-10";
    } else if (time < 15) {
        time = "10-15";
    } else if (time < 20) {
        time = "15-20";
    } else if (time < 25) {
        time = "20-25";
    } else if (time < 30) {
        time = "25-30";
    } else if (time < 50) {
        time = "30-50";
    } else {
        time = "50-100";
    }

    return time;
}