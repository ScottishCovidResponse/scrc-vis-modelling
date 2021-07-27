import os

from utils.fileReader import *
from utils.sinks_sources import *
from utils.greedyKcover import *
from utils.get_path_stats import *
import pickle

K = 1
filepath_edges = "F:/Development/Swansea/scrc-vis-modelling/ContactTracing/GraphVisualisation/RepresentativeTreesViz/RepresentativeTreeCalculator/reconstructingEpidemic/Data/testDataEdge.txt";
filepath_nodes = "F:/Development/Swansea/scrc-vis-modelling/ContactTracing/GraphVisualisation/RepresentativeTreesViz/RepresentativeTreeCalculator/reconstructingEpidemic/Data/testDataNode.txt";

print("TODO: Full automation from command line with arguments")
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


#TODO: Weight function both in shortestpath and get_out_cost. 
out_cost, out_interactions, out_causality = get_out_cost(output_paths, sources, sinks, unreported)
print ('cost of our solution', out_cost)
#exit()