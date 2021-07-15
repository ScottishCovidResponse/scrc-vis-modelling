/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Import.RealData;

import InfectionTreeGenerator.Graph.ContactData.ContactEdge;
import InfectionTreeGenerator.Graph.ContactData.ContactGraph;
import InfectionTreeGenerator.Graph.ContactData.ContactNode;
import InfectionTreeGenerator.Graph.Graph;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
public class GraphParserTest {

    public GraphParserTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    String nodeFileLocation = "NodeTestFile.csv";
    String edgeFileLocation = "EdgeTestFile.csv";

    @Before
    public void setUp() throws IOException {

        //setup a basic file for parsing
        String nodeFileContent = ""
                + "id,timeOfPositiveTest,Attribute1,Attribute2,Attribute3\n"
                + "integer,integer,integer,categorical,e;f;g\n"
                + "1,5,2,test1,e\n"
                + "2,12,3,test2,f\n"
                + "3,20,4,test3,g\n"
                + "4,,5,test4,f\n" //empty test time
                + "5,,6,test5,e\n"
                + "6,,7,test6,e\n";
        Files.write(Paths.get(nodeFileLocation), nodeFileContent.getBytes());

        String edgeFileContent = ""
                + "node1Id,node2Id,timeOfPositiveTest,infectionWeight,Attribute1,Attribute2,Attribute3\n"
                + "integer,integer,integer,integer,integer,categorical,a;b;c\n"
                + "1,2,3,1,2,testE12t3,a\n"
                + "1,2,5,2,3,testE12t5,b\n"
                + "2,4,3,3,4,testE24t3,c\n"
                + "4,5,3,4,5,testE45t3,b\n"
                + "5,4,4,5,6,testE54t4,a\n"
                + "3,6,4,6,7,testE36t5,b\n";
        Files.write(Paths.get(edgeFileLocation), edgeFileContent.getBytes());

    }

    @After
    public void tearDown() {
        File nodeFile = new File(nodeFileLocation);
        nodeFile.delete();
        File edgeFile = new File(edgeFileLocation);
        edgeFile.delete();
    }

    /**
     * Test of constructGraph method, of class GraphParser.
     */
    @Test
    public void testConstructGraph() throws IOException {
        System.out.println("constructGraph");
        ContactGraphParser instance = new ContactGraphParser(nodeFileLocation, edgeFileLocation);
        ContactGraph g = instance.constructGraph();

        //verify that all the nodes are present in the graph with the correct metadata
        verifyNodes(g);
        //verify that all the edges are present in the graph with the correct metadaata
        verifyEdges(g);

    }

    private void verifyNodes(ContactGraph g) {
        //verify nodes from input file.
        verifyNode(g, 1, 5, "2", "test1", "e");
        verifyNode(g, 2, 12, "3", "test2", "f");
        verifyNode(g, 3, 20, "4", "test3", "g");
        verifyNode(g, 4, null, "5", "test4", "f");
        verifyNode(g, 5, null, "6", "test5", "e");
        verifyNode(g, 6, null, "7", "test6", "e");
    }

    private void verifyNode(ContactGraph g, int id, Integer timeOfPositiveTest, String a1, String a2, String a3) {
        ContactNode cn = g.getNode(id);
        assertNotNull(cn);

        assertEquals(cn.id, id);
        assertEquals(cn.positiveTestTime, timeOfPositiveTest);

        assertEquals("Attribute1", cn.metaDataList.get(0).attributeName);
        assertEquals("Attribute2", cn.metaDataList.get(1).attributeName);
        assertEquals("Attribute3", cn.metaDataList.get(2).attributeName);

        assertEquals("integer", cn.metaDataList.get(0).dataType);
        assertEquals("categorical", cn.metaDataList.get(1).dataType);
        assertEquals("e;f;g", cn.metaDataList.get(2).dataType);

        assertEquals(a1, cn.metaDataList.get(0).valueString);
        assertEquals(a2, cn.metaDataList.get(1).valueString);
        assertEquals(a3, cn.metaDataList.get(2).valueString);
    }

    private void verifyEdges(ContactGraph g) {
        //verify edges from input file.
//                        + "node1Id,node2Id,time,infectionWeight,Attribute1,Attribute2,Attribute3\n"
//                + "integer,integer,integer,integer,integer,categorical,a;b;c\n"
//                + "1,2,3,1,2,testE12t3,a\n"
//                + "1,2,5,2,3,testE12t5,b\n"
//                + "2,4,3,3,4,testE24t3,c\n"
//                + "4,5,3,4,5,testE45t3,b\n"
//                + "5,4,4,5,6,testE54t4,a\n"
//                + "3,6,4,6,7,testE36t5,b\n";

        verifyEdge(g, 1, 2, 3, 1, "2", "testE12t3", "a");
        verifyEdge(g, 1, 2, 5, 2, "3", "testE12t5", "b");
        verifyEdge(g, 2, 4, 3, 3, "4", "testE24t3", "c");
        verifyEdge(g, 4, 5, 3, 4, "5", "testE45t3", "b");
        verifyEdge(g, 5, 4, 4, 5, "6", "testE54t4", "a");
        verifyEdge(g, 3, 6, 4, 6, "7", "testE36t5", "b");

        //verify edges amounts
        assertEquals(g.getEdges().size(), 6);
        assertEquals(g.getEdgeSet(1, 2).size(), 2);
        assertEquals(g.getEdgeSet(2, 4).size(), 1);
        assertEquals(g.getEdgeSet(4, 5).size(), 1);
        assertEquals(g.getEdgeSet(5, 4).size(), 1);
        assertEquals(g.getEdgeSet(3, 6).size(), 1);
    }

    private void verifyEdge(ContactGraph g, int sourceId, int targetId, int contactTime, int weight, String a1, String a2, String a3) {
        //check structure
        ContactEdge ce = getEdgeWithTime(g, sourceId, targetId, contactTime);
        assertNotNull(ce);

        //check meta data
        assertEquals(sourceId,ce.source.id);
        assertEquals(targetId,ce.target.id);
        assertEquals(contactTime,ce.contactTime,0.01);
        assertEquals(weight,ce.weight,0.01);

        assertEquals("Attribute1",ce.metaDataList.get(0).attributeName);
        assertEquals("Attribute2",ce.metaDataList.get(1).attributeName);
        assertEquals("Attribute3",ce.metaDataList.get(2).attributeName);

        assertEquals("integer",ce.metaDataList.get(0).dataType);
        assertEquals("categorical",ce.metaDataList.get(1).dataType);
        assertEquals("a;b;c",ce.metaDataList.get(2).dataType);

        assertEquals(a1,ce.metaDataList.get(0).valueString);
        assertEquals(a2,ce.metaDataList.get(1).valueString);
        assertEquals(a3,ce.metaDataList.get(2).valueString);
    }

    private ContactEdge getEdgeWithTime(ContactGraph g, int sourceId, int targetId, int time) {
        Set<ContactEdge> edgeSet = g.getEdgeSet(sourceId, targetId);
        for (ContactEdge ce : edgeSet) {
            if (ce.contactTime == time) {
                return ce;
            }
        }
        return null;
    }
}
