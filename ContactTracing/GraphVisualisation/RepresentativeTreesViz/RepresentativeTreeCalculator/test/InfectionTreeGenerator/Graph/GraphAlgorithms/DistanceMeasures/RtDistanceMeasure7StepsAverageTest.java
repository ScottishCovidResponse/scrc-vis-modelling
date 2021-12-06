/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfectionTreeGenerator.Graph.GraphAlgorithms.DistanceMeasures;

import InfectionTreeGenerator.Graph.GraphAlgorithms.DistanceMeasures.RtDistanceMeasure;
import InfectionTreeGenerator.Graph.GraphAlgorithms.DistanceMeasures.RtDistanceMeasure7StepsAverage;
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
public class RtDistanceMeasure7StepsAverageTest {

    public RtDistanceMeasure7StepsAverageTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    Tree<InfectionNode, InfectionEdge> t = new Tree();

    @Before
    public void setUp() {
        InfectionNode n5 = new InfectionNode(5, 1);
        InfectionNode n6 = new InfectionNode(6, 2);
        InfectionNode n7 = new InfectionNode(7, 3);
        InfectionNode n8 = new InfectionNode(8, 9);
        InfectionNode n9 = new InfectionNode(9, 5);
        InfectionNode n10 = new InfectionNode(10, 10);

        InfectionEdge e56 = new InfectionEdge(n5, n6, 2);
        InfectionEdge e57 = new InfectionEdge(n5, n7, 3);
        InfectionEdge e59 = new InfectionEdge(n5, n9, 5);
        InfectionEdge e67 = new InfectionEdge(n6, n8, 9);
        InfectionEdge e810 = new InfectionEdge(n8, n10, 10);
        t.addNodes(n5, n6, n7, n8, n9, n10);
        t.addEdges(e56, e57, e59, e67, e810);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getCalculatedRtValues method, of class
     * RtDistanceMeasure7StepsAverage.
     */
    @Test
    public void testGetCalculatedRtValues() {
        System.out.println("getCalculatedRtValues");

        //Amount of nodes in window-------:[1,1,1,0,1,0,0,0,1,1]
        //amount of future nodes in window:[3,1,0,1,0,0,0,0,1,0]
        //Rt array------------------------:[3,1,0,0,0,0,0,0,1,0]
        int timeWindowSize = 1;
        RtDistanceMeasure instance = new RtDistanceMeasure7StepsAverage(timeWindowSize);

        Double[] expResult = new Double[]{
            4.0 / 7.0,//0
            1.0 / 7.0,//1
            1.0 / 7.0,//1
        };
        Double[] result = instance.getCalculatedRtValues(t);
        assertArrayEquals(expResult, result);
    }

}
