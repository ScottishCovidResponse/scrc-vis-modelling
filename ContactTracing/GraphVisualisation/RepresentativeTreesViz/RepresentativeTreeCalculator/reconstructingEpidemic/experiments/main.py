from compute_paths import *
from argparse import ArgumentParser

parser = ArgumentParser()
parser.add_argument("-n","--nodes",required=True, help="Location of the inputfile for nodes. Format is (nid \t unixtimestamp). timestamp is time of report. Can be empty")
parser.add_argument("-e","--edges",required=True, help="Location of the inputfile for edges. Format is (unixtimestamp \t n1id \t n2id \t weight). timestamp is time of interaction")
parser.add_argument("-o","--output",required=True, help="Location of the outputfile")

args = parser.parse_args()

filepath_nodes = args.nodes
filepath_edges = args.edges
filepath_output = args.output


#filepath_edges = "F:/Development/Swansea/scrc-vis-modelling/ContactTracing/GraphVisualisation/RepresentativeTreesViz/RepresentativeTreeCalculator/reconstructingEpidemic/Data/DataEdge.txt";
#filepath_nodes = "F:/Development/Swansea/scrc-vis-modelling/ContactTracing/GraphVisualisation/RepresentativeTreesViz/RepresentativeTreeCalculator/reconstructingEpidemic/Data/DataNode.txt";
#filepath_output = "F:/Development/Swansea/scrc-vis-modelling/ContactTracing/GraphVisualisation/RepresentativeTreesViz/RepresentativeTreeCalculator/reconstructingEpidemic/Data/output.txt";


K=1
output_paths = compute_output_paths(1,filepath_edges,filepath_nodes)

#transform every path into a seperate line
output_string = "\n".join(str(e) for e in output_paths)
#remove braces
output_string = output_string.replace("[","")
output_string = output_string.replace("]","")

f = open(filepath_output,"w")
f.write(output_string)
f.close()


