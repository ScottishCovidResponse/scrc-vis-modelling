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
        InfectionEdge e12 = new InfectionEdge(n1, n2, 3);
        InfectionEdge e23 = new InfectionEdge(n2, n3, 4);
        InfectionEdge e24 = new InfectionEdge(n2, n4, 4);
        t1.addNodes(n1, n2, n3, n4);
        t1.addEdges(e12, e23, e24);

        InfectionNode n5 = new InfectionNode(5, 1);
        InfectionNode n6 = new InfectionNode(6, 2);
        InfectionNode n7 = new InfectionNode(7, 3);
        InfectionNode n8 = new InfectionNode(8, 4);
        InfectionNode n9 = new InfectionNode(9, 5);
        InfectionEdge e56 = new InfectionEdge(n5, n6, 2);
        InfectionEdge e57 = new InfectionEdge(n5, n7, 3);
        InfectionEdge e59 = new InfectionEdge(n5, n9, 5);
        InfectionEdge e67 = new InfectionEdge(n6, n8, 4);
        t2.addNodes(n5, n6, n7, n8, n9);
        t2.addEdges(e56, e57, e59, e67);
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

        int totalTimeWindows = 3;
        int timeWindowSize = 2;
        RtDistanceMeasure instance = new RtDistanceMeasure(totalTimeWindows, timeWindowSize);

        double expResult = 1.0 + 2.0 / 3.0;
        double result = instance.getDistance(t1, t2);
        assertEquals(expResult, result, 0.01);
    }

    /**
     * Test of getRtValuesPerStep method, of class RtDistanceMeasure.
     */
    @Test
    public void testGetRtValues() {
        System.out.println("getRtValues");
        int totalTimeWindows = 3;
        int timeWindowSize = 2;
        RtDistanceMeasure instance = new RtDistanceMeasure(totalTimeWindows, timeWindowSize);

        
        Double[] expResult = new Double[]{1.0, 2.0 / 3.0, 0.0};
        Double[] result = instance.getRtValuesPerStep(t1);
        assertEquals(expResult[0], result[0], 0.01);
        assertEquals(expResult[1], result[1], 0.01);
        assertEquals(expResult[2], result[2], 0.01);

        expResult = new Double[]{2.0, 0.0, 0.0};
        result = instance.getRtValuesPerStep(t2);
        assertEquals(expResult[0], result[0], 0.01);
        assertEquals(expResult[1], result[1], 0.01);
        assertEquals(expResult[2], result[2], 0.01);
    }

}
