/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfectionTreeGenerator.Graph.GraphAlgorithms;

import InfectionTreeGenerator.Graph.GraphAlgorithms.DominatingSetCalculator;
import InfectionTreeGenerator.Graph.Edge;
import InfectionTreeGenerator.Graph.Graph;
import InfectionTreeGenerator.Graph.Node;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static testUtility.Utlity.checkCollectionContentEqual;

/**
 *
 * @author MaxSondag
 */
public class DominatingSetCalculatorTest {

    Graph<Node, Edge> g;
    Node n1, n2, n3, n4, n5, n6;
    Edge e12, e13, e14, e45;
    Edge e21, e31, e41;
    DominatingSetCalculator instance;

    public DominatingSetCalculatorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        g = new Graph();
        n1 = new Node(1);
        n2 = new Node(2);
        n3 = new Node(3);
        n4 = new Node(4);
        n5 = new Node(5);
        n6 = new Node(6);

        e12 = new Edge(n1, n2);
        e13 = new Edge(n1, n3);
        e14 = new Edge(n1, n4);
        e45 = new Edge(n4, n5);

        e21 = new Edge(n2, n1);
        e31 = new Edge(n3, n1);
        e41 = new Edge(n4, n1);

        g.addNodes(n1, n2, n3, n4, n5, n6);
        g.addEdges(e12, e13, e14, e45, e21, e31, e41);

        instance = new DominatingSetCalculator();

    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getDominatingSet method, of class DominatingSetCalculator.
     */
    @Test
    public void testGetDominatingSet() {
        System.out.println("getDominatingSet");
        List<Integer> domSet = instance.getDominatingSet(g);

        //test that all nodes are dominated
        for (Node n : g.getNodes()) {
            assertTrue(instance.isDominated(g, domSet, n));
        }

        //test that at least one node is excluded.
        assertTrue(domSet.size() <= (g.getNodes().size() - 1));
    }

    /**
     * Test of trimDominatingSet method, of class DominatingSetCalculator.
     */
    @Test
    public void testTrimDominatingSet() {
        System.out.println("trimDominatingSet");
        List<Integer> domSet = new ArrayList(Arrays.asList(1, 4, 6));

        //copy of the graph
        Graph newG = g.deepCopy();
        Edge newE15 = new Edge(newG.getNode(1), newG.getNode(5));
        newG.addEdge(newE15);

        //can remove 4, but not 1 or 6
        ArrayList<Integer> expResult = new ArrayList(Arrays.asList(1, 6));
        ArrayList<Integer> result = instance.trimDominatingSet(newG, domSet);
        assertTrue(checkCollectionContentEqual(expResult, result));

        //make sure edge direction matters
        newG = g.deepCopy();
        Edge newE51 = new Edge(newG.getNode(5), newG.getNode(1));
        newG.addEdge(newE51);

        //cannot remove 4 now
        expResult = new ArrayList(Arrays.asList(1, 4, 6));
        result = instance.trimDominatingSet(newG, domSet);
        assertTrue(checkCollectionContentEqual(expResult, result));

    }

    /**
     * Test of isDominated method, of class DominatingSetCalculator.
     */
    @Test
    public void testIsDominated() {
        System.out.println("isDominated");

        ArrayList<Integer> domSet = new ArrayList(Arrays.asList(1));

        //dominates itself
        boolean result = instance.isDominated(g, domSet, n1);
        assertEquals(true, result);

        //dominates a neighbor
        result = instance.isDominated(g, domSet, n2);
        assertEquals(true, result);

        //dominates isolated nodes
        domSet = new ArrayList(Arrays.asList(6));
        result = instance.isDominated(g, domSet, n6);
        assertEquals(true, result);

        //does not dominate non-neighbors
        result = instance.isDominated(g, domSet, n5);
        assertEquals(false, result);

        //does not dominate non-directional edges
        domSet = new ArrayList(Arrays.asList(5));
        result = instance.isDominated(g, domSet, n4);
        assertEquals(false, result);
    }

}
