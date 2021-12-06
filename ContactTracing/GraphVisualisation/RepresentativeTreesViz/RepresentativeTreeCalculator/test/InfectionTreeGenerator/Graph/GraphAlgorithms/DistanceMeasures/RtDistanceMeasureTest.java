/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfectionTreeGenerator.Graph.GraphAlgorithms.DistanceMeasures;

import InfectionTreeGenerator.Graph.GraphAlgorithms.DistanceMeasures.RtDistanceMeasure;
import InfectionTreeGenerator.Graph.Infection.InfectionEdge;
import InfectionTreeGenerator.Graph.Infection.InfectionNode;
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
public class RtDistanceMeasureTest {

    public RtDistanceMeasureTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    Tree<InfectionNode, InfectionEdge> t1 = new Tree();
    Tree<InfectionNode, InfectionEdge> t2 = new Tree();

    @Before
    public void setUp() {
        InfectionNode n1 = new InfectionNode(1, 1);
        InfectionNode n2 = new InfectionNode(2, 3);
        InfectionNode n3 = new InfectionNode(3, 4);
        InfectionNode n4 = new InfectionNode(4, 4);
        InfectionNode n5 = new InfectionNode(5, 7);

        InfectionEdge e12 = new InfectionEdge(n1, n2, 3);
        InfectionEdge e23 = new InfectionEdge(n2, n3, 4);
        InfectionEdge e24 = new InfectionEdge(n2, n4, 4);
        InfectionEdge e25 = new InfectionEdge(n2, n5, 7);
        t1.addNodes(n1, n2, n3, n4, n5);
        t1.addEdges(e12, e23, e24, e25);

        InfectionNode n6 = new InfectionNode(6, 1);
        InfectionNode n7 = new InfectionNode(7, 2);
        InfectionNode n8 = new InfectionNode(8, 3);
        InfectionNode n9 = new InfectionNode(9, 4);
        InfectionNode n10 = new InfectionNode(10, 5);

        InfectionEdge e67 = new InfectionEdge(n6, n7, 2);
        InfectionEdge e68 = new InfectionEdge(n6, n8, 3);
        InfectionEdge e69 = new InfectionEdge(n6, n9, 4);
        InfectionEdge e710 = new InfectionEdge(n7, n10, 5);

        t2.addNodes(n6, n7, n8, n9, n10);
        t2.addEdges(e67, e68, e69, e710);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getDistance method, of class RtDistanceMeasure.
     */
    @Test
    public void testGetDistance() {
        System.out.println("getDistance");

        int timeWindowSize = 2;
        RtDistanceMeasure instance = new RtDistanceMeasure(timeWindowSize);

        double expResult = 1 + 1;
        double result = instance.getDistance(t1, t2);
        assertEquals(expResult, result, 0.01);
    }

    /**
     * Test of getRtValuesPerStep method, of class RtDistanceMeasure.
     */
    @Test
    public void testGetRtValues() {
        System.out.println("getRtValues");
        int timeWindowSize = 2;
        RtDistanceMeasure instance = new RtDistanceMeasure(timeWindowSize);

        Double[] expResult = new Double[]{1.0 / 1.0, 3.0 / 3.0, 0.0, 0.0};
        Double[] result = instance.getRtValuesPerStep(t1);
        assertEquals(expResult[0], result[0], 0.01);
        assertEquals(expResult[1], result[1], 0.01);
        assertEquals(expResult[2], result[2], 0.01);
        assertEquals(expResult[3], result[3], 0.01);

        expResult = new Double[]{4.0 / 2.0, 0.0, 0.0};
        result = instance.getRtValuesPerStep(t2);
        assertEquals(expResult[0], result[0], 0.01);
        assertEquals(expResult[1], result[1], 0.01);
        assertEquals(expResult[2], result[2], 0.01);
    }

}
