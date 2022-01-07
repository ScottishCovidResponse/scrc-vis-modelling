/**
 * Preprocess the data to usefull datastructures
 */
function preprocessData() {
    for (let i = 0; i < repTreesData.length; i++) {
        const repTree = repTreesData[i];
        const treeId = repTree.id;
        repTreeById.set(treeId, repTree);

        const nodes = getNodes(repTree);
        for (let j = 0; j < nodes.length; j++) {
            const repNode = nodes[j];
            const repId = repNode.id;
            repNodeById.set(repId, repNode);
        }
    }


    for (let i = 0; i < metaData.length; i++) {
        const node = metaData[i];
        const id = node.id;
        metaDataFromNodeById.set(id, node);
    }

    for (let i = 0; i < allTreesData.length; i++) {
        const tree = allTreesData[i];
        const id = tree.id;
        allTreeById.set(id, tree);

        //add depth meta data for each tree.
        addDepthMetaData(tree, 0);
    }


    console.log("Get metadata from data directly")
    metaDataNames = ["positiveTestTime", "location"];
    metaDataTypes = ["date", "categorical"];

    //    //Save the names and types of metadata that we have. All nodes contain these in the same order (except policies)
    //     metaDataNames = Object.getOwnPropertyNames(metaData[0]);
    //     console.log(metaDataNames);
    //     for (let i = 0; i < metaDataNames.length; i++) {
    //         const val = metaData[0][metaDataNames[i]];
    //         if (Number.isInteger(val)) {
    //             metaDataTypes[i] = "integer";
    //         } else {
    //             metaDataTypes[i] = "categorical"
    //         }
    //     }


    // for (let i = 0; i < metaData.length; i++) {
    //     const metaDataVal = metaData[i]
    //     const name = metaDataVal['attributeName'];
    //     const type = metaDataVal['dataType'];
    //     //save to variables
    //     metaDataNames[i] = name;
    //     metaDataTypes[i] = type;
    // }
}

function addDepthMetaData(tree, depth) {
    //save node reference
    metaDataFromNodeById.get(tree.id).depth = depth;

    //recurse into children
    for (let child of tree.children) {
        addDepthMetaData(child, depth + 1);
    }
}

/**
 * Returns the maximum depth of any tree
 */
function getMaxDepth() {
    let maxDepth = 0;
    for (let tree of allTreeById.values()) {
        maxDepth = Math.max(maxDepth, getTreeHeight(tree))
    }
    return maxDepth;
}

/**
 * 
 * Returns the height of the subtree rooted at treeNode
 */
function getTreeHeight(treeNode) {
    let height = 0;
    for (let tree of treeNode.children) {
        let newHeight = getTreeHeight(tree) + 1; //1 further downt he tree
        height = Math.max(height, newHeight);
    }
    return height;
}



/**
 * Gets the amount of trees represented by the tree {@code d} before editdistance {@code editDistance}
 * @param {*} editDistance 
 */
function getAmountOfTreesRepresented(d, editDistance) {
    let count = 0;
    let reps = d.data.representations;
    for (let repI = 0; repI < reps.length; repI++) {
        const repIData = reps[repI];
        if (repIData.editDistance <= editDistance) {
            count += repIData.representationIds.length;
        }
    }
    return count;
}


/**
 * Gets the amount of trees represented by the tree with id {@code id} before editdistance {@code editDistance}
 * @param {*} editDistance 
 */
function getAmountOfTreesRepresentedById(id, editDistance) {
    const repTree = repTreeById.get(id);
    if (repTree === undefined) { //Occurs when looking at Alltrees which do not have representations
        return 1;
    }
    let reps = repTree.representations;

    let count = 0;
    for (let repI = 0; repI < reps.length; repI++) {
        const repIData = reps[repI];
        if (repIData.editDistance <= editDistance) {
            count += repIData.representationIds.length;
        }
    }
    return count;
}

/**
 * Gets the amount of trees represented by the tree with id {@code id} before editdistance {@code editDistance}
 * @param {*} editDistance 
 */
function getTreesRepresentedById(id, editDistance) {
    const repTree = repTreeById.get(id);
    let reps = repTree.representations;

    let repTreeIds = [];
    for (let i = 0; i < reps.length; i++) {
        const repIData = reps[i];
        if (repIData.editDistance <= editDistance) {
            const repIds = repIData.representationIds;
            for (let j = 0; j < repIds.length; j++) {
                repTreeIds.push(repIds[j]);
            }
        }
    }

    let allTreesRepresented = [];
    for (let i = 0; i < repTreeIds.length; i++) {
        const tree = allTreeById.get(repTreeIds[i]);
        allTreesRepresented.push(tree);
    }


    return allTreesRepresented;
}


/**
 * Get all nodes that the node with nodeId represents at the specified editdistance. treeId is the tree nodeid belong to
 * @param {} treeId 
 * @param {} nodeId 
 * @param {} editDistance
 */
function getRepresentedNodesMetaData(nodeId, editDistance) {
    const node = repNodeById.get(nodeId);
    let reps = node.representations;

    let repNodeIds = [];
    for (let i = 0; i < reps.length; i++) {
        const repIData = reps[i];
        if (repIData.editDistance <= editDistance) {
            const repIds = repIData.representationIds;
            for (let j = 0; j < repIds.length; j++) {
                repNodeIds.push(repIds[j]);
            }
        }
    }

    let metaDataNodes = [];
    for (let i = 0; i < repNodeIds.length; i++) {
        const tree = metaDataFromNodeById.get(repNodeIds[i]);
        if (tree === undefined) {
            console.error("Tree with id " + repNodeIds[i] + " is not present in the metadata")
            continue;
        }
        metaDataNodes.push(tree);
    }

    return metaDataNodes;
}


/**
 * 
 * @param {name of the attribute we want to get the values from} name 
 * @param {id of the node we want the data from} id 
 * @returns a single value containing the value of the 'name' attribute of the node with the given id
 */
function getMetaDataValueFromId(name, id) {
    if (name == "None") {
        return "None";
    }


    const metaDataNode = metaDataFromNodeById.get(id);
    return metaDataNode[name];

    // const nameIndex = metaDataNames.indexOf(name);
    // const metaDataNode = metaDataFromNodeById.get(id);
    // return metaDataNode.metaDataList[nameIndex].valueString;
}

/**
 * 
 * @param {Name of the attribute we want to get the values from. In case name = "none", returns "None" for each metadata element} name  
 * @param {An array of all metadata values to be considered}
 * @returns An array of all values for this attribute from the metadata in metadataArray. Values can be present multiple times
 */
function getMetaDataValues(name, metaDataArray) {

    //find the index of the string with the given name
    const nameIndex = metaDataNames.indexOf(name);
    if (nameIndex == -1) {
        if (name == "None") //Special variable to visualize no data
        {
            return new Array(metaDataArray.length).fill("None"); //Fill array with "None" values
        }

        console.error("Name " + name + " was not found in the metadata")
        return [];
    }

    let values = [];
    for (let i = 0; i < metaDataArray.length; i++) {
        const metaDataNode = metaDataArray[i];
        // const val = metaDataNode.metaDataList[nameIndex].valueString;
        const val = metaDataNode[name];
        values[i] = val;
    }

    return values;
}


/**
 * 
 * @param {name of the attribute we want to get the values from} name  
 * @param {Id of the node that is representing other nodes} id
 * @param {The maximum edit distance to find trees represented by 'id'} editDistance
 * @returns An array of all values for this attribute for all trees represented at the given editdistance by the node with the specified id. Values can be present multiple times
 */
function getMetaDataValuesFromRepTrees(name, id, editDistance) {
    const repTreeMetaData = getRepresentedNodesMetaData(id, editDistance)
    return getMetaDataValues(name, repTreeMetaData);
}




function getNodes(rootNode) {
    let nodeList = [rootNode]; //add the current node to the list
    //recurse in all children
    for (let i = 0; i < rootNode.children.length; i++) {
        const newNodes = getNodes(rootNode.children[i]);
        if (newNodes) {
            nodeList = nodeList.concat(newNodes);
        }
    }
    return nodeList;
}


function getNodeFromTree(rootNode, nodeId) {
    const nodes = getNodes(rootNode);
    for (let i = 0; i < nodes.length; i++) {
        const node = nodes[i];
        if (node.id === nodeId) {
            return node;
        }
    }
    //not present
    return null;
}