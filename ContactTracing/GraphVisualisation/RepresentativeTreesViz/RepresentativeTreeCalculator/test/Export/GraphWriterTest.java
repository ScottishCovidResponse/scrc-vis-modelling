/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Export;

import Export.RepresentativeNodeJson.RepresentationJson;
import Utility.Pair;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import InfectionTreeGenerator.Graph.Edge;
import InfectionTreeGenerator.Graph.GraphAlgorithms.RepresentativeTree.RepresentativeEdge;
import InfectionTreeGenerator.Graph.GraphAlgorithms.RepresentativeTree.RepresentativeNode;
import InfectionTreeGenerator.Graph.GraphAlgorithms.RepresentativeTree.RepresentativeTree;
import InfectionTreeGenerator.Graph.Node;
import InfectionTreeGenerator.Graph.Tree;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashSet;
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
public class GraphWriterTest {

    public GraphWriterTest() {
    }

    String outputFileLocation = "./test.json";

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
        //delete the test file
        File f = new File(outputFileLocation);
        f.delete();
    }
//
//    /**
//     * Test of writeInfectionGraph method, of class GraphWriter.
//     */
//    @Test
//    public void testWriteInfectionGraph() throws Exception {
//        System.out.println("writeInfectionGraph");
//        String outputFileLocation = "";
//        Collection<InfectionNode> nodes = null;
//        Collection<InfectionEdge> edges = null;
//        GraphWriter instance = new GraphWriter();
//        instance.writeInfectionGraph(outputFileLocation, nodes, edges);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of writeRepresentativeGraph method, of class GraphWriter.
//     */
//    @Test
//    public void testWriteRepresentativeGraph() throws Exception {
//        System.out.println("writeRepresentativeGraph");
//        String outputFileLocation = "";
//        Collection<RepresentativeTree> trees = null;
//        GraphWriter instance = new GraphWriter();
//        instance.writeRepresentativeGraph(outputFileLocation, trees);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of writeForest method, of class GraphWriter.
//     */
//    @Test
//    public void testWriteForest() {
//        System.out.println("writeForest");
//        String string = "";
//        Set<Tree> forest = null;
//        GraphWriter instance = new GraphWriter();
//        instance.writeForest(string, forest);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of writeRepresentativeTrees method, of class GraphWriter.
     */
    @Test
    public void testWriteRepresentativeTrees() throws Exception {
        System.out.println("writeRepresentativeTrees");

        Tree t1 = new Tree();
        Node n1 = new Node(1);
        Node n2 = new Node(2);
        Node n3 = new Node(3);
        Edge e12 = new Edge(n1, n2);
        Edge e13 = new Edge(n1, n3);

        t1.addNodes(n1, n2, n3);
        t1.addEdges(e12, e13);

        //extra nodes and edges
        Tree t2 = new Tree();
        Node n4 = new Node(4);
        Node n5 = new Node(5);
        Node n6 = new Node(56);
        Edge e45 = new Edge(n4, n5);
        Edge e56 = new Edge(n5, n6);
        t2.addNodes(n4, n5, n6);
        t2.addEdges(e45, e56);

        //get what it represents
        RepresentativeTree rt = new RepresentativeTree(t1);
        rt.maxEditDistance = 100;
        ((RepresentativeNode) rt.getNode(1)).addToRepresentsNodes(0, n1);
        ((RepresentativeNode) rt.getNode(2)).addToRepresentsNodes(0, n2);
        ((RepresentativeNode) rt.getNode(3)).addToRepresentsNodes(0, n3);
        ((RepresentativeEdge) rt.getEdge(1, 2)).addToRepresentsEdges(0, e12);
        ((RepresentativeEdge) rt.getEdge(1, 3)).addToRepresentsEdges(0, e13);

        ((RepresentativeNode) rt.getNode(1)).addToRepresentsNodes(2, n4);
        ((RepresentativeNode) rt.getNode(2)).addToRepresentsNodes(2, n5);
        ((RepresentativeEdge) rt.getEdge(1, 2)).addToRepresentsEdges(2, e45);

        List<RepresentativeTree> repTrees = Arrays.asList(rt);
        GraphWriter instance = new GraphWriter();
        instance.writeRepresentativeTrees(outputFileLocation, repTrees);

        //file exists now, read it back in to see if it is correct.
        JsonReader reader = new JsonReader(new FileReader(outputFileLocation));
        Gson gson = new Gson();
        RepresentativeNodeJson[] jsonTreeList = gson.fromJson(reader, RepresentativeNodeJson[].class);

        RepresentativeNodeJson rnj1 = jsonTreeList[0];
        assertEquals(100, rnj1.maxEditDistance);//only the first node has the edit distance
        assertEquals(1, rnj1.id);
        checkRepresentationJson(rnj1, new Pair(0, new HashSet(Arrays.asList(1))), new Pair(2, new HashSet(Arrays.asList(4))));

        RepresentativeNodeJson rnj2 = rnj1.getChild(2);
        assertEquals(2, rnj2.id);
        checkRepresentationJson(rnj2, new Pair(0, new HashSet(Arrays.asList(2))), new Pair(2, new HashSet(Arrays.asList(5))));

        RepresentativeNodeJson rnj3 = rnj1.getChild(3);
        assertEquals(3, rnj3.id);
        checkRepresentationJson(rnj3, new Pair(0, new HashSet(Arrays.asList(3))));
        
        //close the reader so the file can get deleted
        reader.close();
    }

    private void checkRepresentationJson(RepresentativeNodeJson rnj1, Pair<Integer, HashSet<Integer>>... repPairs) {
        assertEquals(repPairs.length, rnj1.representations.size());

        for (RepresentationJson r : rnj1.representations) {
            boolean found = false;
            for (Pair<Integer, HashSet<Integer>> repPair : repPairs) {
                if (r.editDistance == repPair.a) {
                    assertTrue(checkCollectionContentEqual(r.representationIds, repPair.b));
                    found = true;
                }
            }
            if (!found) {
                fail("Unknown edit distance found: " + r.editDistance);
            }
        }
    }

}
