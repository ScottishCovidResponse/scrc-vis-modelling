import os

from utils.fileReader import *
from utils.sinks_sources import *
from utils.greedyKcover import *
from utils.get_path_stats import *
import pickle

K = 1
filepath = "F:/Development/Swansea/scrc-vis-modelling/ContactTracing/GraphVisualisation/RepresentativeTreesViz/RepresentativeTreeCalculator/reconstructingEpidemic/Data/testData.txt";


TS = readRealFile(filepath)
#TS contains a array of arrays of the inputdata. [[time,id1,id2,status1,status2,node 1 has reporeted],...]
#Seeds is a set of {nodeids} which representes  initial infections


#NOTE: Data should be preprocessed such that latest interaction before a report has a flag
sources, immuned, sinks, reported, unreported, sources_TnI, sinks_TnI, unreported_TnI = get_sinks_and_sources(TS)

#Sources: Dictionary of {nodeid,latest interactiontime before report} of reported nodes
#sinks: Dictionary of {nodeid,latest interactiontime before report} of reported nodes
#reported: 'infected' contains a set with all infected nodes. 'recovered' contains a list with all immuned nodes
#unreported: Dictionary of {nodeid, lastTime in interactions} of all nodes that have no reports

SP = shortestPath1(TS, sources, sinks, immuned, unreported)
#SP holds the shortets paths between Source and Sink nodes.
#[startOfPathNodeid][endOfPathNodeId] => (total weight of path,Last time of path from start to end,[path]). path is triples of (time of edge,startNodeId,endNodeId)


cover, output_paths, cover_cost, legal_alpha = greedyBS(SP, len(sinks), K)
#output_paths holds the infection paths.
print(output_paths)

out_cost, out_interactions, out_causality = get_out_cost(output_paths, sources, sinks, unreported)
print ('cost of our solution', out_cost)
#exit()