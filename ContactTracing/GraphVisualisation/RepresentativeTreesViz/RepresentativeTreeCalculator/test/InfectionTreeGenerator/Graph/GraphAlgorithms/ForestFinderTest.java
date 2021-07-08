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

    Graph inputGraph;

    Node n1, n2, n3, n4, n5, n6, n7;
    Edge e12, e13, e24, e56;

    ForestFinder instance;

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
        inputGraph = new Graph();
        n1 = new Node(1);
        n2 = new Node(2);
        n3 = new Node(3);
        n4 = new Node(4);
        e12 = new Edge(n1, n2);
        e13 = new Edge(n1, n3);
        e24 = new Edge(n2, n4);

        n5 = new Node(5);
        n6 = new Node(6);
        e56 = new Edge(n5, n6);

        n7 = new Node(7);

        inputGraph.addNodes(n1, n2, n3, n4, n5, n6, n7);
        inputGraph.addEdges(e12, e13, e24, e56);

        instance = new ForestFinder(inputGraph, Tree.class);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getForest method, of class ForestFinder.
     */
    @Test
    public void testGetForest() {
        System.out.println("getForest");
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
