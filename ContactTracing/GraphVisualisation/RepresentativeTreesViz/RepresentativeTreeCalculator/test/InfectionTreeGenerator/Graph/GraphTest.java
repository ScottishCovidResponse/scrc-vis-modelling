/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfectionTreeGenerator.Graph;

import InfectionTreeGenerator.Graph.Edge;
import InfectionTreeGenerator.Graph.Node;
import InfectionTreeGenerator.Graph.Graph;
import java.util.Arrays;
import java.util.Collection;
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
public class GraphTest {

    Graph instance;
    Node n1, n2, n3, n4, n5;
    Edge e12, e21, e13, e31, e23, e32, e45;

    public GraphTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        instance = new Graph();
        n1 = new Node(1);
        n2 = new Node(2);
        n3 = new Node(3);
        n4 = new Node(4);
        n5 = new Node(5);
        instance.addNodes(n1, n2, n3, n4, n5);

        e12 = new Edge(n1, n2);
        e21 = new Edge(n2, n1);
        e13 = new Edge(n1, n3);
        e31 = new Edge(n3, n1);
        e23 = new Edge(n2, n3);
        e32 = new Edge(n3, n2);
        e45 = new Edge(n4, n5);

        instance.addEdges(e12, e21, e13, e31, e23, e32, e45);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getReachableNodes method, of class Graph.
     */
    @Test
    public void testGetReachableNodes() {
        System.out.println("getReachableNodes");

        Collection expResult = Arrays.asList(n1, n2, n3);
        Collection result = instance.getReachableNodes(n1);
        assertEquals(true, checkCollectionContentEqual(expResult, result));

        result = instance.getReachableNodes(n2);
        assertEquals(true, checkCollectionContentEqual(expResult, result));

        result = instance.getReachableNodes(n2);
        assertEquals(true, checkCollectionContentEqual(expResult, result));

        expResult = Arrays.asList(n4, n5);
        result = instance.getReachableNodes(n4);
        assertEquals(true, checkCollectionContentEqual(expResult, result));

        expResult = Arrays.asList(n5);
        result = instance.getReachableNodes(n5);
        assertEquals(true, checkCollectionContentEqual(expResult, result));
    }

    @Test
    public void testCheckDirectedCycle() {
        for (Node n : Arrays.asList(n1, n2, n3)) {
            assertTrue(instance.checkDirectedCycle(n));
        }
        for (Node n : Arrays.asList(n4, n5)) {
            assertFalse(instance.checkDirectedCycle(n));
        }
    }

}
