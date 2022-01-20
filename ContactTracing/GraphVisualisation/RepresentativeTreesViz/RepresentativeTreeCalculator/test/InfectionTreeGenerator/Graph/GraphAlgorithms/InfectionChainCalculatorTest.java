/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfectionTreeGenerator.Graph.GraphAlgorithms;

import InfectionTreeGenerator.Graph.ContactData.ContactEdge;
import InfectionTreeGenerator.Graph.ContactData.ContactGraph;
import InfectionTreeGenerator.Graph.ContactData.ContactNode;
import InfectionTreeGenerator.Graph.Infection.InfectionGraph;
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
public class InfectionChainCalculatorTest {

    public InfectionChainCalculatorTest() {
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
    public void testPreparsed() {
        ContactGraph g = new ContactGraph();
        createNode(g, 1, 1564040000);
        createNode(g, 2, 1564170000);
        createNode(g, 3);
        createNode(g, 4);
        createNode(g, 5);
        createNode(g, 6, 1564370000);
        createNode(g, 7, 1564770000);

        createEdge(g, 1, 2, 1, 1564050000);
        createEdge(g, 2, 3, 1, 1564150000);
        createEdge(g, 1, 3, 1, 1564250000);
        createEdge(g, 1, 6, 1, 1564350000);
        createEdge(g, 1, 2, 1, 1564450000);
        createEdge(g, 3, 4, 1, 1564550000);
        createEdge(g, 7, 6, 1, 1564650000);
        createEdge(g, 6, 7, 1, 1564750000);
        createEdge(g, 4, 5, 1, 1564850000);
        createEdge(g, 4, 6, 1, 1565950000);

        //seperate component
        createNode(g, 8, 1664770000);
        createNode(g, 9, 1667770000);
        createEdge(g, 8, 9, 1, 1665770000);

        //singleton component
        createNode(g, 10, 1664770000);

        //generate output files
        InfectionChainCalculator instance = new InfectionChainCalculator(g, "F:/Development/Swansea/scrc-vis-modelling/ContactTracing/GraphVisualisation/RepresentativeTreesViz/RepresentativeTreeCalculator/temp");
        InfectionGraph result = instance.calculateInfectionGraph(false);

        //do it again while not generating files, should give the same result
        instance = new InfectionChainCalculator(g, "F:/Development/Swansea/scrc-vis-modelling/ContactTracing/GraphVisualisation/RepresentativeTreesViz/RepresentativeTreeCalculator/temp");
        result = instance.calculateInfectionGraph(true);

        assertEquals(7, result.getNodes().size());
        assertNotNull(result.getNode(1));
        assertNotNull(result.getNode(2));
        assertNotNull(result.getNode(6));
        assertNotNull(result.getNode(7));
        assertNotNull(result.getNode(8));
        assertNotNull(result.getNode(9));
        assertNotNull(result.getNode(10));

        assertEquals(4, result.getEdges().size());
        assertNotNull(result.getEdge(1, 2));
        assertNotNull(result.getEdge(1, 6));
        assertNotNull(result.getEdge(6, 7));
        assertNotNull(result.getEdge(8, 9));
    }

    /**
     * Test of calculateInfectionGraph method, of class
     * InfectionChainCalculator.
     */
    @Test
    public void testCalculateInfectionGraph() {
        System.out.println("calculateInfectionGraph");
        System.out.println("Testing the interface between the python program and the java program. Python program has it's own test cases.");

        ContactGraph g = new ContactGraph();
        createNode(g, 1, 1564040000);
        createNode(g, 2, 1564170000);
        createNode(g, 3);
        createNode(g, 4);
        createNode(g, 5);
        createNode(g, 6, 1564370000);
        createNode(g, 7, 1564770000);

        createEdge(g, 1, 2, 1, 1564050000);
        createEdge(g, 2, 3, 1, 1564150000);
        createEdge(g, 1, 3, 1, 1564250000);
        createEdge(g, 1, 6, 1, 1564350000);
        createEdge(g, 1, 2, 1, 1564450000);
        createEdge(g, 3, 4, 1, 1564550000);
        createEdge(g, 7, 6, 1, 1564650000);
        createEdge(g, 6, 7, 1, 1564750000);
        createEdge(g, 4, 5, 1, 1564850000);
        createEdge(g, 4, 6, 1, 1565950000);

        //seperate component
        createNode(g, 8, 1664770000);
        createNode(g, 9, 1667770000);
        createEdge(g, 8, 9, 1, 1665770000);

        //singleton component
        createNode(g, 10, 1664770000);

        InfectionChainCalculator instance = new InfectionChainCalculator(g, "F:/Development/Swansea/scrc-vis-modelling/ContactTracing/GraphVisualisation/RepresentativeTreesViz/RepresentativeTreeCalculator/temp");
        InfectionGraph result = instance.calculateInfectionGraph(true);

        assertEquals(7, result.getNodes().size());
        assertNotNull(result.getNode(1));
        assertNotNull(result.getNode(2));
        assertNotNull(result.getNode(6));
        assertNotNull(result.getNode(7));
        assertNotNull(result.getNode(8));
        assertNotNull(result.getNode(9));
        assertNotNull(result.getNode(10));

        assertEquals(4, result.getEdges().size());
        assertNotNull(result.getEdge(1, 2));
        assertNotNull(result.getEdge(1, 6));
        assertNotNull(result.getEdge(6, 7));
        assertNotNull(result.getEdge(8, 9));
    }

    private ContactNode createNode(ContactGraph g, int nodeId, long timestamp) {
        ContactNode cn = createNode(g, nodeId);
        cn.positiveTestTime = timestamp;
        return cn;
    }

    private ContactNode createNode(ContactGraph g, int nodeId) {
        ContactNode cn = new ContactNode(nodeId);
        g.addNode(cn);
        return cn;
    }

    private ContactEdge createEdge(ContactGraph g, int id1, int id2, int weight, int timestsmp) {
        ContactEdge ce = new ContactEdge(g.getNode(id1), g.getNode(id2), timestsmp, weight);
        g.addEdge(ce);
        return ce;
    }
}
