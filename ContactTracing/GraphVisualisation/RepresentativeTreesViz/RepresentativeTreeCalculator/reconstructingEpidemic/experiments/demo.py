import os

from computePaths import *
import pickle

K = 1
filepath_edges = "F:/Development/Swansea/scrc-vis-modelling/ContactTracing/GraphVisualisation/RepresentativeTreesViz/RepresentativeTreeCalculator/reconstructingEpidemic/Data/testDataEdge.txt";
filepath_nodes = "F:/Development/Swansea/scrc-vis-modelling/ContactTracing/GraphVisualisation/RepresentativeTreesViz/RepresentativeTreeCalculator/reconstructingEpidemic/Data/testDataNode.txt";

computeOutputPaths(K,filepath_edges,filepath_nodes)