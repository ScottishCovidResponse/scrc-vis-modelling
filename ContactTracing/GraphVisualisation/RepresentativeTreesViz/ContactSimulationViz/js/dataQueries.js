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


    for (let i = 0; i < allTreesData.length; i++) {
        const tree = allTreesData[i];
        const id = tree.id;
        allTreeById.set(id, tree);
    }

    for (let i = 0; i < metaData.length; i++) {
        const node = metaData[i];
        const id = node.id;
        metaDataFromNodeById.set(id, node);
    }

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
        metaDataNodes.push(tree);
    }

    return metaDataNodes;
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