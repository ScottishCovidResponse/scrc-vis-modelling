from compute_paths import *
from argparse import ArgumentParser

parser = ArgumentParser()
parser.add_argument("-n","--nodes", help="Location of the inputfile for nodes. Format is (nid \t unixtimestamp). timestamp is time of report. Can be empty")
parser.add_argument("-e","--edges", help="Location of the inputfile for edges. Format is (unixtimestamp \t n1id \t n2id \t weight). timestamp is time of interaction")
parser.add_argument("-o","--output", help="Location of the outputfile")

args = parser.parse_args()

#filepath_nodes = parser.nodes
#filepath_edges = parser.edges
#TODO Work in output file
#filepath_output = parser.output


filepath_edges = "F:/Development/Swansea/scrc-vis-modelling/ContactTracing/GraphVisualisation/RepresentativeTreesViz/RepresentativeTreeCalculator/reconstructingEpidemic/Data/DataEdge.txt";
filepath_nodes = "F:/Development/Swansea/scrc-vis-modelling/ContactTracing/GraphVisualisation/RepresentativeTreesViz/RepresentativeTreeCalculator/reconstructingEpidemic/Data/DataNode.txt";


#TODO: Work away K
K=1
compute_output_paths(1,filepath_edges,filepath_nodes)


