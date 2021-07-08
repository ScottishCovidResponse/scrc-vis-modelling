/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfectionTreeGenerator.Graph.GraphAlgorithms.RepresentativeTree;

import InfectionTreeGenerator.Graph.GraphAlgorithms.RepresentativeTree.RepresentativeTree;
import InfectionTreeGenerator.Graph.GraphAlgorithms.RepresentativeTree.RepresentativeEdge;
import InfectionTreeGenerator.Graph.GraphAlgorithms.RepresentativeTree.RepresentativeNode;
import Utility.Pair;
import InfectionTreeGenerator.Graph.Edge;
import InfectionTreeGenerator.Graph.GraphAlgorithms.DistanceMeasures.TreeEditDistance.TEDMapping;
import InfectionTreeGenerator.Graph.GraphAlgorithms.DistanceMeasures.TreeEditDistance.TreeEditDistanceCalculator;
import InfectionTreeGenerator.Graph.Node;
import InfectionTreeGenerator.Graph.Tree;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static testUtility.Utlity.*;

/**
 *
 * @author MaxSondag
 */
public class RepresentativeTreeTest {

    Tree t1, t2, t3;
    Node n1, n2, n3, n4, n5, n6, n7, n8;
    Edge e12, e13, e45, e56, e78;

    public RepresentativeTreeTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        n1 = new Node(1);
        n2 = new Node(2);
        n3 = new Node(3);
        e12 = new Edge(n1, n2);
        e13 = new Edge(n1, n3);
        t1 = new Tree();
        t1.addNodes(n1, n2, n3);
        t1.addEdges(e12, e13);

        n4 = new Node(4);
        n5 = new Node(5);
        n6 = new Node(6);
        e45 = new Edge(n4, n5);
        e56 = new Edge(n5, n6);
        t2 = new Tree();
        t2.addNodes(n4, n5, n6);
        t2.addEdges(e45, e56);

        n7 = new Node(7);
        n8 = new Node(8);
        e78 = new Edge(n7, n8);
        t3 = new Tree();
        t3.addNodes(n7, n8);
        t3.addEdges(e78);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of addToMapping method, of class RepresentativeTree.
     */
    @Test
    public void testAddToMapping() {
        System.out.println("addToMapping");
        List<Tree> treesMapped = Arrays.asList(t2, t3);

        RepresentativeTree rt1 = new RepresentativeTree(t1);
        //get nodes and edges
        RepresentativeNode n1R = (RepresentativeNode) rt1.getNode(n1.id);
        RepresentativeNode n2R = (RepresentativeNode) rt1.getNode(n2.id);
        RepresentativeNode n3R = (RepresentativeNode) rt1.getNode(n3.id);

        RepresentativeEdge e12R = (RepresentativeEdge) rt1.getEdge(n1.id, n2.id);
        RepresentativeEdge e13R = (RepresentativeEdge) rt1.getEdge(n1.id, n3.id);

        //initialize t2t1 map
        HashMap<Node, Node> t2t1NMap = new HashMap();
        t2t1NMap.put(n4, n1R);
        t2t1NMap.put(n5, n2R);
        HashMap<Edge, Edge> t2t1EMap = new HashMap();
        t2t1EMap.put(e45, e12R);
        TEDMapping t2t1Mapping = new TEDMapping(t2t1NMap, t2t1EMap);

        //initialize t3t1 map
        HashMap<Node, Node> t3t1NMap = new HashMap();
        t3t1NMap.put(n7, n1R);
        t3t1NMap.put(n8, n2R);
        HashMap<Edge, Edge> t3t1EMap = new HashMap();
        t3t1EMap.put(e78, e12R);
        TEDMapping t3t1Mapping = new TEDMapping(t3t1NMap, t3t1EMap);

        //get the mapping stored in tedC
        TreeEditDistanceCalculator tedC = new TreeEditDistanceCalculator();
        //add the hardcocded mapping
        tedC.tedMapping.put(new Pair(t2, t1), t2t1Mapping);
        tedC.tedMapping.put(new Pair(t3, t1), t3t1Mapping);

        int editDistance = 2;
        rt1.addToMapping(editDistance, treesMapped, tedC);

        assertEquals(2, rt1.maxEditDistance);

        //verify representation correct.
        assertTrue(checkRepresentation(n1R, 0, Arrays.asList(n1)));
        assertTrue(checkRepresentation(n1R, 2, Arrays.asList(n4, n7)));
        assertTrue(checkRepresentation(e12R, 0, Arrays.asList(e12)));
        assertTrue(checkRepresentation(e12R, 2, Arrays.asList(e45, e78)));

        assertTrue(checkRepresentation(n2R, 0, Arrays.asList(n2)));
        assertTrue(checkRepresentation(n2R, 2, Arrays.asList(n5, n8)));

        assertTrue(checkRepresentation(e12R, 0, Arrays.asList(e12)));
        assertTrue(checkRepresentation(e12R, 2, Arrays.asList(e45, e78)));
        assertTrue(checkRepresentation(n3R, 0, Arrays.asList(n3)));

        assertTrue(checkRepresentation(e13R, 0, Arrays.asList(e13)));

        //check representation empty at other places
        assertTrue(checkRepresentationEmpty(n1R,1,3));
        assertTrue(checkRepresentationEmpty(n2R,1,3));
        assertTrue(checkRepresentationEmpty(n3R,1,3));
        assertTrue(checkRepresentationEmpty(e12R,1,3));
        assertTrue(checkRepresentationEmpty(e13R,1,3));
        
        //verify adding it again on a second distance only increases the edit distance but does not add more
        rt1.addToMapping(editDistance + 1, treesMapped, tedC);
        assertEquals(3, rt1.maxEditDistance);
        assertTrue(checkRepresentationEmpty(n1R,3));
        assertTrue(checkRepresentationEmpty(n2R,3));
        assertTrue(checkRepresentationEmpty(n3R,3));
        assertTrue(checkRepresentationEmpty(e12R,3));
        assertTrue(checkRepresentationEmpty(e13R,3));

    }

   

}
