from utils.fileReader import *
from utils.sinks_sources import *
from utils.greedyKcover import *



def compute_output_paths(K,filepath_edges,filepath_nodes):
    
    print(filepath_nodes)
    
    contacts,NS = readRealFiles(filepath_edges,filepath_nodes)
    #TS contains a array of arrays of the transactions in the data. [[time,id1,id2]]
    #NS contains a array of arrays of the nodes and their report time in the input data. [[n1,time]]. Time might not be present

    if single_infected_node(NS):
        return ([[(NS[0][1],NS[0][0],NS[0][0])]])
    
    
        
    

    sources, immuned, sinks, unreported = get_sinks_and_sources(contacts,NS)

    #Sources: Dictionary of {nodeid,latest interactiontime before report} of reported nodes
    #sinks: Dictionary of {nodeid,latest interactiontime before report} of reported nodes
    #unreported: Dictionary of {nodeid, lastTime in interactions} of all nodes that have no reports

    print ("If contact have no weights, put them on 0.")
    SP = shortestPath1(contacts, sources, sinks, unreported)
    #SP holds the shortets paths between Source and Sink nodes.
    #[startOfPathNodeid][endOfPathNodeId] => (total weight of path,Last time of path from start to end,[path]). path is triples of (time of edge,startNodeId,endNodeId)

    cover, output_paths, cover_cost, legal_alpha = greedyBS(SP, len(sinks), K)
    #output_paths holds the infection paths. It holds a path for each sink in the data, which correspond to reported notes. Note, these are the paths, not the trees.
    #format: (datatime,nodeId1,nodeId2). These correspond to the interactions
    print(output_paths)

    #Note output_paths are not optimal as it is NP hard. Due to the method used for calculation, a node may be visited multiple times via different interactions as an interaction with time $t$ might be infesible.
    #TODO: Postprocessing could be done if needed. 
    #       1) For any node, only use the earliest interaction. 
    #       2) For any node, set the activation to the minimum of the earliest interaction of  the report time


    
    return output_paths;


#Returns true if there is only a single infected node
def single_infected_node(NS):
        count = 0;
        for n in NS:
            if n[1] != "":
                count = count+1
            
        return (count == 1)