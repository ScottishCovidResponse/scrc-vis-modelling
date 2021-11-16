/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Import.RealData;

import InfectionTreeGenerator.Graph.ContactData.ContactEdge;
import InfectionTreeGenerator.Graph.ContactData.ContactGraph;
import InfectionTreeGenerator.Graph.ContactData.ContactNode;
import Utility.TimeFunctions;
import java.io.IOException;
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
public class ContactGraphParserTest {

    String inputFolderLocation = "./Data/testSet/TestRealData/";
    String outputFileLocation = "./Data/testSet/output/";

    public ContactGraphParserTest() {
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

    /**
     * Test of constructGraph method, of class ContactGraphParser.
     */
    @Test
    public void testConstructGraph() throws IOException {
        //Verify that it only takes nodes with positive, and removes edges not between positive nodes.

        System.out.println("constructGraph");
        ContactGraph expectedGraph = new ContactGraph();

        ContactNode cnA = new ContactNode(0);
        ContactNode cnB = new ContactNode(1);
        ContactNode cnC = new ContactNode(2);
        ContactNode cnD = new ContactNode(3);
        ContactNode cnE = new ContactNode(4);
        //F is ignored as it did not test positive
        expectedGraph.addNode(cnA);
        expectedGraph.addNode(cnB);
        expectedGraph.addNode(cnC);
        expectedGraph.addNode(cnD);
        expectedGraph.addNode(cnE);

        ContactEdge ceA = new ContactEdge(cnA, cnB, TimeFunctions.dateToUnixTimestamp("2021-11-11"), 1);
        ContactEdge ceB = new ContactEdge(cnD, cnA, TimeFunctions.dateToUnixTimestamp("2021-11-05"), 1);
        ContactEdge ceC = new ContactEdge(cnD, cnB, TimeFunctions.dateToUnixTimestamp("2021-11-05"), 1);
        ContactEdge ceD = new ContactEdge(cnD, cnC, TimeFunctions.dateToUnixTimestamp("2021-11-05"), 1);

        //need bidirectional edges
        ContactEdge ceAI = new ContactEdge(cnB, cnA, TimeFunctions.dateToUnixTimestamp("2021-11-11"), 1);
        ContactEdge ceBI = new ContactEdge(cnA, cnD, TimeFunctions.dateToUnixTimestamp("2021-11-05"), 1);
        ContactEdge ceCI = new ContactEdge(cnB, cnD, TimeFunctions.dateToUnixTimestamp("2021-11-05"), 1);
        ContactEdge ceDI = new ContactEdge(cnC, cnD, TimeFunctions.dateToUnixTimestamp("2021-11-05"), 1);

        expectedGraph.addEdge(ceA);
        expectedGraph.addEdge(ceB);
        expectedGraph.addEdge(ceC);
        expectedGraph.addEdge(ceD);
        expectedGraph.addEdge(ceAI);
        expectedGraph.addEdge(ceBI);
        expectedGraph.addEdge(ceCI);
        expectedGraph.addEdge(ceDI);

        ContactGraphParser instance = new ContactGraphParser(inputFolderLocation + "TTPTestContacts.csv", inputFolderLocation + "TTPTestExposures.csv");
        ContactGraph result = instance.constructGraph();

        for (ContactNode cn : expectedGraph.getNodes()) {
            assertNotNull(result.getNode(cn.id));
        }
        assertEquals(expectedGraph.getNodes().size(), result.getNodes().size());

        for (ContactEdge ce : expectedGraph.getEdges()) {
            assertNotNull(result.getEdgeSet(ce.source.id, ce.target.id));
        }
        assertEquals(expectedGraph.getEdges().size(), result.getEdges().size());

    }

//    /**
//     * Test of addMetaDataFiles method, of class ContactGraphParser.
//     */
//    @Test
//    public void testAddMetaDataFiles() throws Exception {
//        System.out.println("addMetaDataFiles");
//        fail("The test case is a prototype.");
//
//        String nodeMetaDataFileLocation = "";
//        String edgeMetaDataFileLocation = "";
//        ContactGraphParser instance = null;
//        instance.addMetaDataFiles(nodeMetaDataFileLocation, edgeMetaDataFileLocation);
//        // TODO review the generated test code and remove the default call to fail.
//    }
}
