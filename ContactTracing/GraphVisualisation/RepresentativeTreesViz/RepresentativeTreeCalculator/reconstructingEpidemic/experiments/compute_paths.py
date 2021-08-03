from utils.fileReader import *
from utils.sinks_sources import *
from utils.greedyKcover import *



def compute_output_paths(K,filepath_edges,filepath_nodes):
    
    print(filepath_nodes)
    
    TS,NS = readRealFiles(filepath_edges,filepath_nodes)
    #TS contains a array of arrays of the transactions in the data. [[time,id1,id2]]
    #NS contains a array of arrays of the nodes and their report time in the input data. [[n1,time]]. Time might not be present

    sources, immuned, sinks, unreported = get_sinks_and_sources(TS,NS)

    #Sources: Dictionary of {nodeid,latest interactiontime before report} of reported nodes
    #sinks: Dictionary of {nodeid,latest interactiontime before report} of reported nodes
    #unreported: Dictionary of {nodeid, lastTime in interactions} of all nodes that have no reports

    print ("Todo: Update the weight data. Need to partially put this in the inputdata to be read.")
    SP = shortestPath1(TS, sources, sinks, unreported)
    #SP holds the shortets paths between Source and Sink nodes.
    #[startOfPathNodeid][endOfPathNodeId] => (total weight of path,Last time of path from start to end,[path]). path is triples of (time of edge,startNodeId,endNodeId)

    print("Todo: find minimal k for which we can cover a component")
    cover, output_paths, cover_cost, legal_alpha = greedyBS(SP, len(sinks), K)
    #output_paths holds the infection paths. It holds a path for each sink in the data, which correspond to reported notes. Note, these are the paths, not the trees.
    #format: (datatime,nodeId1,nodeId2). These correspond to the interactions
    print(output_paths)

    #Note output_paths are not optimal as it is NP hard. Due to the method used for calculation, a node may be visited multiple times via different interactions as an interaction with time $t$ might be infesible.
    #TODO: Postprocessing could be done if needed. 
    #       1) For any node, only use the earliest interaction. 
    #       2) For any node, set the activation to the minimum of the earliest interaction of  the report time


    
    return output_paths;


