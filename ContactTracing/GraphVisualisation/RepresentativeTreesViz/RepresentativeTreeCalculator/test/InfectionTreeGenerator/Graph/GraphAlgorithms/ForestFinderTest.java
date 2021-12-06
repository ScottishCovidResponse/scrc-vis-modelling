/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfectionTreeGenerator.Graph.GraphAlgorithms;

import InfectionTreeGenerator.Graph.GraphAlgorithms.ForestFinder;
import InfectionTreeGenerator.Graph.Edge;
import InfectionTreeGenerator.Graph.Graph;
import InfectionTreeGenerator.Graph.Node;
import InfectionTreeGenerator.Graph.Tree;
import java.util.Arrays;
import java.util.Set;
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
public class ForestFinderTest {

    public ForestFinderTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSingletonsAndDuplicates() {
        Graph inputGraph = new Graph();
        Node n0 = new Node(0);
        Node n1 = new Node(1);
        Node n2 = new Node(2);

        Node n3 = new Node(3);
        Node n4 = new Node(4);
        Edge e34 = new Edge(n3, n4);

        Node n5 = new Node(5);
        Node n6 = new Node(6);
        Edge e56 = new Edge(n5, n6);

        Node n7 = new Node(7);
        Node n8 = new Node(8);
        Edge e78 = new Edge(n7, n8);

        inputGraph.addNodes(n0, n1, n2, n3, n4, n5, n6, n7, n8);
        inputGraph.addEdges(e34, e56, e78);

        ForestFinder instance = new ForestFinder(inputGraph, Tree.class);
        Set<Tree> trees = instance.getForest();

        //correct size
        assertEquals(6, trees.size());

        boolean[] nodesFound = new boolean[9];
        Arrays.fill(nodesFound, false);

        for (Tree t : trees) {
            for (int i = 0; i < 9; i++) {
                if (t.hasNodeWithId(i)) {//only find each nodes once
                    assertFalse(nodesFound[i]);
                    nodesFound[i] = true;
                }
                //trees are size 2 or 3
                if (t.hasNodeWithId(3) || t.hasNodeWithId(5) || t.hasNodeWithId(7)) {
                    assertTrue(t.hasNodeWithId(4) || t.hasNodeWithId(6) || t.hasNodeWithId(8));//must have one of the other
                    assertEquals(2, t.getNodes().size());
                } else {
                    assertEquals(1, t.getNodes().size());
                }
            }
        }
        //all nodes found
        for (boolean found : nodesFound) {
            assertTrue(found);
        }
    }

    /**
     * Test of getForest method, of class ForestFinder.
     */
    @Test
    public void testGetForest() {
        System.out.println("getForest");
        Graph inputGraph = new Graph();
        Node n1 = new Node(1);
        Node n2 = new Node(2);
        Node n3 = new Node(3);
        Node n4 = new Node(4);
        Edge e12 = new Edge(n1, n2);
        Edge e13 = new Edge(n1, n3);
        Edge e24 = new Edge(n2, n4);

        Node n5 = new Node(5);
        Node n6 = new Node(6);
        Edge e56 = new Edge(n5, n6);

        Node n7 = new Node(7);

        inputGraph.addNodes(n1, n2, n3, n4, n5, n6, n7);
        inputGraph.addEdges(e12, e13, e24, e56);

        ForestFinder instance = new ForestFinder(inputGraph, Tree.class);
        Set<Tree> trees = instance.getForest();

        //correct size
        assertEquals(3, trees.size());

        //check trees correct
        for (Tree t : trees) {
            switch (t.getNodes().size()) {
                case 4://identified a tree
                    //test correct nodes in
                    assertNotNull(t.getNode(1));
                    assertNotNull(t.getNode(2));
                    assertNotNull(t.getNode(3));
                    assertNotNull(t.getNode(4));
                    //test correct edges in
                    assertNotNull(t.getEdge(1, 2));
                    assertNotNull(t.getEdge(1, 3));
                    assertNotNull(t.getEdge(2, 4));
                    //test no more edges
                    assertEquals(t.getEdges().size(), 3);

                    //id should be equal to root
                    assertEquals(1, t.id);
                    break;
                case 2:
                    //test correct nodes in
                    assertNotNull(t.getNode(5));
                    assertNotNull(t.getNode(6));
                    //test correct edges in
                    assertNotNull(t.getEdge(5, 6));
                    assertEquals(t.getEdges().size(), 1);

                    //id should be equal to root
                    assertEquals(5, t.id);
                    break;
                case 1:
                    //test correct node in
                    assertNotNull(t.getNode(7));
                    //test it has no edges
                    assertEquals(t.getEdges().size(), 0);

                    //id should be equal to root
                    assertEquals(7, t.id);
                    break;
                default:
                    fail("There was a tree with a size that should not exist");
                    break;
            }

        }

    }
}
