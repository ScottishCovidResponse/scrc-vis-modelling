/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfectionTreeGenerator.Graph.GraphAlgorithms.DistanceMeasures.TreeEditDistance;

import InfectionTreeGenerator.Graph.GraphAlgorithms.DistanceMeasures.TreeEditDistance.TreeEditDistanceLPCplexPrioritizeDepth;
import InfectionTreeGenerator.Graph.GraphAlgorithms.DistanceMeasures.TreeEditDistance.TEDMapping;
import InfectionTreeGenerator.Graph.Edge;
import InfectionTreeGenerator.Graph.Node;
import InfectionTreeGenerator.Graph.Tree;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author MaxSondag
 */
public class TreeEditDistanceLPCplexPrioritizeDepthTest {

    Tree t1, t2, t3, t4, t5;
    Node n1, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11;
    Edge e12, e13, e45, e56, e78, e910;

    public TreeEditDistanceLPCplexPrioritizeDepthTest() {
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

        n9 = new Node(9);
        n10 = new Node(10);
        e910 = new Edge(n9, n10);
        t4 = new Tree();
        t4.addNodes(n9, n10);
        t4.addEdges(e910);

        n11 = new Node(11);
        t5 = new Tree();
        t5.addNodes(n11);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of solve method, of class TreeEditDistanceLPCplexPrioritizeDepth.
     */
    @Test
    public void testSolve() {
        System.out.println("testSolve");

        //check basic distance
        TreeEditDistanceLPCplexPrioritizeDepth instance = new TreeEditDistanceLPCplexPrioritizeDepth(t1, t2);
        int ted = instance.solve();
        assertEquals(2, ted);

        //check mapping
        TEDMapping mapping = instance.getMapping();
        Node n1M = mapping.getMappedNode(n1);
        Node n2M = mapping.getMappedNode(n2);
        Node n3M = mapping.getMappedNode(n3);
        assertEquals(n1M, n4);
        //one of the two is 0, and the other is deleted
        assertTrue(n2M != null ^ n3M != null);//one of the two must be null

        if (n2M != null) {
            assertTrue(n2M == n5);//not allowed to be n6
        } else {
            assertTrue(n3M == n5);//not allowed to be n6
        }

        //inverse is equal
        instance = new TreeEditDistanceLPCplexPrioritizeDepth(t2, t1);
        ted = instance.solve();
        assertEquals(2, ted);
        //check mapping
        mapping = instance.getMapping();
        Node n4M = mapping.getMappedNode(n4);
        Node n5M = mapping.getMappedNode(n5);
        Node n6M = mapping.getMappedNode(n6);
        assertEquals(n1M, n4);
        //one of the two is 0, and the other is deleted
        assertTrue(n5M == n2 || n5M == n3);
        assertTrue(n6M == null);//6 is not allowed to map to anything

        //can be 0
        instance = new TreeEditDistanceLPCplexPrioritizeDepth(t3, t4);
        ted = instance.solve();
        assertEquals(0, ted);

        //can be 1
        instance = new TreeEditDistanceLPCplexPrioritizeDepth(t1, t4);
        ted = instance.solve();
        assertEquals(1, ted);

        //can go towards a singleton node
        instance = new TreeEditDistanceLPCplexPrioritizeDepth(t1, t5);
        ted = instance.solve();
        assertEquals(2, ted);

        //and also from a singleton node
        instance = new TreeEditDistanceLPCplexPrioritizeDepth(t5, t1);
        ted = instance.solve();
        assertEquals(2, ted);
    }

}
