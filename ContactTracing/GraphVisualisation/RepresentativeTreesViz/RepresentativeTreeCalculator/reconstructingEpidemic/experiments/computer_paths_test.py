import unittest
from compute_paths import *

class test_compute_paths(unittest.TestCase):
    def test_default(self):        
        #Data contains an index case, contacts are after reports. All nodes can be traced back to the origin and all active nodes are reported
        filepath_edges = "F:/Development/Swansea/scrc-vis-modelling/ContactTracing/GraphVisualisation/RepresentativeTreesViz/RepresentativeTreeCalculator/reconstructingEpidemic/Data/TestCases/baseDataEdge.txt";
        filepath_nodes = "F:/Development/Swansea/scrc-vis-modelling/ContactTracing/GraphVisualisation/RepresentativeTreesViz/RepresentativeTreeCalculator/reconstructingEpidemic/Data/TestCases/baseDataNode.txt";
        K = 1
        
        output_paths = compute_output_paths(K,filepath_edges,filepath_nodes)
        
        self.assertEqual(len(output_paths),4)
        
        self.assertEquals(len(output_paths[0]),1)
        self.assertEquals(output_paths[0][0], (1564040000,1,1))
        
        self.assertEquals(len(output_paths[1]),1)
        self.assertEquals(output_paths[1][0], (1564050000,1,2))
        
        self.assertEquals(len(output_paths[2]),1)
        self.assertEquals(output_paths[2][0], (1564350000,1,6))
        
        self.assertEquals(len(output_paths[3]),2)
        self.assertEquals(output_paths[3][0], (1564350000,1,6))
        self.assertEquals(output_paths[3][1], (1564750000,6,7))
        
    def test_unreported(self):
        #Data contains an index case, contacts are after reports. Node 6 is not reported to be active, but needs to be passed towards node 7
        filepath_edges = "F:/Development/Swansea/scrc-vis-modelling/ContactTracing/GraphVisualisation/RepresentativeTreesViz/RepresentativeTreeCalculator/reconstructingEpidemic/Data/TestCases/unknownDataEdge.txt";
        filepath_nodes = "F:/Development/Swansea/scrc-vis-modelling/ContactTracing/GraphVisualisation/RepresentativeTreesViz/RepresentativeTreeCalculator/reconstructingEpidemic/Data/TestCases/unknownDataNode.txt";
        K = 1
        
        output_paths = compute_output_paths(K,filepath_edges,filepath_nodes)


        self.assertEqual(len(output_paths),3)
        
        self.assertEquals(len(output_paths[0]),1)
        self.assertEquals(output_paths[0][0], (1564040000,1,1))
        
        self.assertEquals(len(output_paths[1]),1)
        self.assertEquals(output_paths[1][0], (1564450000,1,2))#2 exposed after second contact, so take it as it is closer
        
        self.assertEquals(len(output_paths[2]),2)
        self.assertEquals(output_paths[2][0], (1564350000,1,6)) #going through 6 even if it is not reported as it is the only way to reach 7
        self.assertEquals(output_paths[2][1], (1564750000,6,7))

    def test_preferKnownOverUnreported(self):
        #Data contains an index case, contacts are after reports. Node 6 is not reported to be active but can be passed to 7. Additionally, can pass through reported node 2 to 7 at slightly earlier time.
        #prefrence should go to 1-2-7 over 1-6-7 even if 6 is earlier than 2 due to 2 being a known active node
        filepath_edges = "F:/Development/Swansea/scrc-vis-modelling/ContactTracing/GraphVisualisation/RepresentativeTreesViz/RepresentativeTreeCalculator/reconstructingEpidemic/Data/TestCases/unknownAndExtraDataEdge.txt";
        filepath_nodes = "F:/Development/Swansea/scrc-vis-modelling/ContactTracing/GraphVisualisation/RepresentativeTreesViz/RepresentativeTreeCalculator/reconstructingEpidemic/Data/TestCases/unknownAndExtraDataNode.txt";
        K = 1

        output_paths = compute_output_paths(K,filepath_edges,filepath_nodes)


        self.assertEqual(len(output_paths),3)
        
        self.assertEquals(len(output_paths[0]),1)
        self.assertEquals(output_paths[0][0], (1564040000,1,1))
        
        self.assertEquals(len(output_paths[1]),1)
        self.assertEquals(output_paths[1][0], (1564450000,1,2))#2 exposed after second contact, so take it as it is closer
        
        self.assertEquals(len(output_paths[2]),2)
        self.assertEquals(output_paths[2][0], (1564450000,1,2)) #going through 2 instead of 7
        self.assertEquals(output_paths[2][1], (1564730000,2,7))
        
    def test_consistentPaths(self):
        #TODO: Note currently failing at no post-processing step to consolidate when to take a path is made.
        
        #test whether the same interactions paths are always chosen.
        filepath_edges = "F:/Development/Swansea/scrc-vis-modelling/ContactTracing/GraphVisualisation/RepresentativeTreesViz/RepresentativeTreeCalculator/reconstructingEpidemic/Data/TestCases/consistentDataEdge.txt";
        filepath_nodes = "F:/Development/Swansea/scrc-vis-modelling/ContactTracing/GraphVisualisation/RepresentativeTreesViz/RepresentativeTreeCalculator/reconstructingEpidemic/Data/TestCases/consistentDataNode.txt";
        #Cannot take the path 1-2 late in this dataset, and need to take the earlier contact as otherwise 2-7 cannot be reached in time
        K = 1

        output_paths = compute_output_paths(K,filepath_edges,filepath_nodes)


        self.assertEqual(len(output_paths),3)
        
        self.assertEquals(len(output_paths[0]),1)
        self.assertEquals(output_paths[0][0], (1564040000,1,1))
        
        self.assertEquals(len(output_paths[1]),1)
        self.assertEquals(output_paths[1][0], (1564050000,1,2))#2 exposed after second contact, so take it as it is closer
        
        self.assertEquals(len(output_paths[2]),2)
        self.assertEquals(output_paths[2][0], (1564050000,1,2)) #going through 2 instead of 7
        self.assertEquals(output_paths[2][1], (1564060000,2,7))

    def test_before_report(self):        
        #Data contains an index case, contacts are before reports. All nodes can be traced back to the origin and all active nodes are reported
        filepath_edges = "F:/Development/Swansea/scrc-vis-modelling/ContactTracing/GraphVisualisation/RepresentativeTreesViz/RepresentativeTreeCalculator/reconstructingEpidemic/Data/TestCases/ContactBeforeReportDataEdge.txt";
        filepath_nodes = "F:/Development/Swansea/scrc-vis-modelling/ContactTracing/GraphVisualisation/RepresentativeTreesViz/RepresentativeTreeCalculator/reconstructingEpidemic/Data/TestCases/ContactBeforeReportDataNode.txt";
        K = 1

        output_paths = compute_output_paths(K,filepath_edges,filepath_nodes)


        self.assertEqual(len(output_paths),4)
        
        self.assertEquals(len(output_paths[0]),1)
        self.assertEquals(output_paths[0][0], (1564910000,1,1)) #self infection path is always at report time if only a single edge is needed. Insunates costs 0, not consistency with other paths
        
        self.assertEquals(len(output_paths[1]),1)
        self.assertEquals(output_paths[1][0], (1564450000,1,2)) #it should take the later one of the two (1,2) contacts, as that is closer to the reported time
        
        self.assertEquals(len(output_paths[2]),1)
        self.assertEquals(output_paths[2][0], (1564350000,1,6))
        
        self.assertEquals(len(output_paths[3]),2)
        self.assertEquals(output_paths[3][0], (1564350000,1,6))
        self.assertEquals(output_paths[3][1], (1564750000,6,7))
        
        
    def test_k2(self):        
        #Data contains two index case, contacts are after reports. All nodes can be traced back to the origin and all active nodes are reported
        #There is no viable connection between the two index cases. Needs at least 2 trees
        filepath_edges = "F:/Development/Swansea/scrc-vis-modelling/ContactTracing/GraphVisualisation/RepresentativeTreesViz/RepresentativeTreeCalculator/reconstructingEpidemic/Data/TestCases/k2DataEdge.txt";
        filepath_nodes = "F:/Development/Swansea/scrc-vis-modelling/ContactTracing/GraphVisualisation/RepresentativeTreesViz/RepresentativeTreeCalculator/reconstructingEpidemic/Data/TestCases/k2DataNode.txt";
        K = 1
        
        output_paths = compute_output_paths(K,filepath_edges,filepath_nodes)
        
        self.assertEqual(len(output_paths),4)
        
        self.assertEquals(len(output_paths[0]),1)
        self.assertEquals(output_paths[0][0], (1564040000,1,1))
        
        self.assertEquals(len(output_paths[1]),1)
        self.assertEquals(output_paths[1][0], (1564050000,1,2))
        
        self.assertEquals(len(output_paths[2]),1)
        self.assertEquals(output_paths[2][0], (1564370000,6,6))
        
        self.assertEquals(len(output_paths[3]),1)
        self.assertEquals(output_paths[3][0], (1564750000,6,7))
        
    def test_small(self):        
        #Data only contains 2 nodes and 1 edge.
        filepath_edges = "F:/Development/Swansea/scrc-vis-modelling/ContactTracing/GraphVisualisation/RepresentativeTreesViz/RepresentativeTreeCalculator/reconstructingEpidemic/Data/TestCases/smallDataEdge.txt";
        filepath_nodes = "F:/Development/Swansea/scrc-vis-modelling/ContactTracing/GraphVisualisation/RepresentativeTreesViz/RepresentativeTreeCalculator/reconstructingEpidemic/Data/TestCases/smallDataNode.txt";
        K = 1

        output_paths = compute_output_paths(K,filepath_edges,filepath_nodes)


        self.assertEqual(len(output_paths),2)
        
        self.assertEquals(len(output_paths[0]),1)
        self.assertEquals(output_paths[0][0], (1664770000,8,8)) #self infection path is always at report time if only a single edge is needed. 
        
        self.assertEquals(len(output_paths[1]),1)
        self.assertEquals(output_paths[1][0], (1665770000,8,9)) #Only path available
        
if __name__ == '__main__':
    unittest.main()