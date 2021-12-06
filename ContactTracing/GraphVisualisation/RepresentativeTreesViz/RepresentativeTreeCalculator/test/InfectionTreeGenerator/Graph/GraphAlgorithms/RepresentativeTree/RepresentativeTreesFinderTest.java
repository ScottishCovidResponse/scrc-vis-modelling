/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfectionTreeGenerator.Graph.GraphAlgorithms.RepresentativeTree;

import InfectionTreeGenerator.Graph.GraphAlgorithms.RepresentativeTree.RepresentativeTree;
import InfectionTreeGenerator.Graph.GraphAlgorithms.RepresentativeTree.RepresentativeTreesFinder;
import InfectionTreeGenerator.Graph.GraphAlgorithms.RepresentativeTree.RepresentativeEdge;
import InfectionTreeGenerator.Graph.GraphAlgorithms.RepresentativeTree.RepresentativeNode;
import Export.Json.RepresentativeNodeJson;
import Utility.Log;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import InfectionTreeGenerator.Graph.Edge;
import InfectionTreeGenerator.Graph.Graph;
import InfectionTreeGenerator.Graph.GraphAlgorithms.DistanceMeasures.TreeEditDistance.TreeEditDistanceCalculator;
import InfectionTreeGenerator.Graph.GraphAlgorithms.ForestFinder;
import InfectionTreeGenerator.Graph.Node;
import InfectionTreeGenerator.Graph.Tree;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static testUtility.Utlity.*;

/**
 *
 * @author MaxSondag
 */
public class RepresentativeTreesFinderTest {

    Graph inputGraph;
    Node n1, n2, n3, n4, n5, n6, n7, n8, n9, n10;
    Edge e12, e13, e45, e56, e78, e910;

    RepresentativeTreesFinder instance;

    String outputPrefix = "./unitTest";

    public RepresentativeTreesFinderTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        //4 trees, two different trees of size 3 and 2 identical trees of size 2.
        inputGraph = new Graph();
        n1 = new Node(1);
        n2 = new Node(2);
        n3 = new Node(3);
        e12 = new Edge(n1, n2);
        e13 = new Edge(n1, n3);

        n4 = new Node(4);
        n5 = new Node(5);
        n6 = new Node(6);
        e45 = new Edge(n4, n5);
        e56 = new Edge(n5, n6);

        n7 = new Node(7);
        n8 = new Node(8);
        e78 = new Edge(n7, n8);

        n9 = new Node(9);
        n10 = new Node(10);
        e910 = new Edge(n9, n10);

        inputGraph.addNodes(n1, n2, n3, n4, n5, n6, n7, n8, n9, n10);
        inputGraph.addEdges(e12, e13, e45, e56, e78, e910);
    }

    @After
    public void tearDown() {
        //delete output files
        File f = new File(outputPrefix + "2.json");
        f.delete();

        f = new File(outputPrefix + "3.json");
        f.delete();

    }

    @Test
    public void testCheckSingletonTrees() throws IOException {
        Graph inputGraph = new Graph();
        Node n1 = new Node(1);
        Node n2 = new Node(2);
        Node n3 = new Node(3);
        Node n4 = new Node(4);
        Node n5 = new Node(5);
        Node n6 = new Node(6);
        Node n7 = new Node(7);
        Node n8 = new Node(8);
        Node n9 = new Node(9);
        Node n10 = new Node(10);

        Edge e12 = new Edge(n1, n2);
        Edge e34 = new Edge(n3, n4);
        Edge e56 = new Edge(n5, n6);
        inputGraph.addNodes(n1, n2, n3, n4, n5, n6, n7, n8, n9, n10);
        inputGraph.addEdges(e12, e34,e56);
        RepresentativeTreesFinder instance = new RepresentativeTreesFinder();

        TreeEditDistanceCalculator tedC = new TreeEditDistanceCalculator();

        ForestFinder ff = new ForestFinder(inputGraph, Tree.class);
        Set<Tree> forest = ff.getForest();

        List<RepresentativeTree> result = instance.getAndWriteRepresentativeTreeData(forest, 0, 100, tedC, outputPrefix);

        assertEquals(2, result.size());

        //check identical tree mapped correct
        for (RepresentativeTree rt : result) {
            if (rt.getNodes().size() == 1) {
                assertEquals(instance.MAXEDITDISTANCE, rt.maxEditDistance);
                Collection<RepresentativeNode> nodes = (Collection<RepresentativeNode>) rt.getNodes();
                for (RepresentativeNode rn : nodes) {
                    List<Node> representedNodes = rn.getRepresentNodes(0);
                    assertEquals(4, representedNodes.size());
                }

            }

            if (rt.getNodes().size() == 2) {
                //both nodes should only represent one node
                assertEquals(instance.MAXEDITDISTANCE, rt.maxEditDistance);
                Collection<RepresentativeNode> nodes = (Collection<RepresentativeNode>) rt.getNodes();
                for (RepresentativeNode rn : nodes) {
                    List<Node> representedNodes = rn.getRepresentNodes(0);
                    assertEquals(3, representedNodes.size());
                }
            }
        }
    }

    /**
     * Test of getAndWriteRepresentativeTreeData method, of class
     * RepresentativeTreesFinder.
     */
    @Test
    public void testGetRepresentativeTreeData() throws IOException {
        System.out.println("getRepresentativeTreeData");;

        instance = new RepresentativeTreesFinder();
        TreeEditDistanceCalculator tedC = new TreeEditDistanceCalculator();

        ForestFinder ff = new ForestFinder(inputGraph, Tree.class
        );
        Set<Tree> forest = ff.getForest();

        List<RepresentativeTree> result = instance.getAndWriteRepresentativeTreeData(forest, 0, 100, tedC, outputPrefix);

        Log.printOnce("Test case for testGetRepresentativeTreeData does not verify the output json yet.");

        //check identical trees collapsed correctly, and non-identical are represented
        assertEquals(3, result.size());

        //check identical tree mapped correct
        for (RepresentativeTree rt : result) {
            if (rt.getNodes().size() == 2) {
                assertEquals(instance.MAXEDITDISTANCE, rt.maxEditDistance);
            }
        }

        //get the trees
        RepresentativeTree rtN1 = null;
        RepresentativeTree rtN4 = null;
        RepresentativeTree rtN79 = null;
        for (RepresentativeTree rt : result) {
            if (rt.hasNodeWithId(n1.id)) {
                rtN1 = rt;
            }
            if (rt.hasNodeWithId(n4.id)) {
                rtN4 = rt;
            }
            if (rt.hasNodeWithId(n7.id) || rt.hasNodeWithId(n9.id)) {
                rtN79 = rt;
            }
        }

        //one of the two different trees should last forever, the other should have 2
        assertTrue(rtN1.maxEditDistance == instance.MAXEDITDISTANCE ^ rtN4.maxEditDistance == instance.MAXEDITDISTANCE);
        assertTrue(rtN1.maxEditDistance == 1 ^ rtN4.maxEditDistance == 1);

        checkRepresentativeNodesCorrect(rtN1, rtN4, rtN79);
        checkRepresentativeEdgesCorrect(rtN1, rtN4, rtN79);

        checkJsonCorrect(rtN1, rtN4, rtN79, outputPrefix);

    }

    private void checkRepresentativeNodesCorrect(RepresentativeTree rtN1, RepresentativeTree rtN4, RepresentativeTree rtN79) {
        //get the nodes
        RepresentativeNode rn1 = (RepresentativeNode) rtN1.getNode(1);
        RepresentativeNode rn2 = (RepresentativeNode) rtN1.getNode(2);
        RepresentativeNode rn3 = (RepresentativeNode) rtN1.getNode(3);
        RepresentativeNode rn4 = (RepresentativeNode) rtN4.getNode(4);
        RepresentativeNode rn5 = (RepresentativeNode) rtN4.getNode(5);
        RepresentativeNode rn6 = (RepresentativeNode) rtN4.getNode(6);
        RepresentativeNode rn79, rn810;
        if (rtN79.getNode(7) != null) {
            rn79 = (RepresentativeNode) rtN79.getNode(7);
            rn810 = (RepresentativeNode) rtN79.getNode(8);
        } else {
            rn79 = (RepresentativeNode) rtN79.getNode(9);
            rn810 = (RepresentativeNode) rtN79.getNode(10);
        }

        //check represented at start
        assertTrue(checkRepresentation(rn1, 0, Arrays.asList(n1)));
        assertTrue(checkRepresentation(rn2, 0, Arrays.asList(n2)));
        assertTrue(checkRepresentation(rn3, 0, Arrays.asList(n3)));
        assertTrue(checkRepresentation(rn4, 0, Arrays.asList(n4)));
        assertTrue(checkRepresentation(rn5, 0, Arrays.asList(n5)));
        assertTrue(checkRepresentation(rn6, 0, Arrays.asList(n6)));

        //identical trees collapse immediatly
        assertTrue(checkRepresentation(rn79, 0, Arrays.asList(n7, n9)));
        assertTrue(checkRepresentation(rn810, 0, Arrays.asList(n8, n10)));

        //can use either to represent it, check which one is representing trees rtN1 and rtN4
        if (rtN1.maxEditDistance == instance.MAXEDITDISTANCE) { //trN1 is used as the representative tree
            //check root is represented
            assertTrue(checkRepresentation(rn1, 2, Arrays.asList(n4)));

            //either 2 or 3 must represent 5
            assertTrue(checkRepresentation(rn2, 2, Arrays.asList(n5))
                    ^ checkRepresentation(rn3, 2, Arrays.asList(n5)));

            //ensure nothing added elsewere
            assertEquals(1, rtN4.maxEditDistance);

        } else {//RT4 is used as the representative tree
            //check root is represented
            assertTrue(checkRepresentation(rn4, 2, Arrays.asList(n1)));

            //5 must represent either 2 or 3
            assertTrue(checkRepresentation(rn5, 2, Arrays.asList(n2))
                    ^ checkRepresentation(rn5, 2, Arrays.asList(n3)));

            //ensure rn6 does not get something added
            assertTrue(checkRepresentation(rn6, 2, Arrays.asList()));

            //ensure rtN1 stops being a representative tree
            assertEquals(1, rtN1.maxEditDistance);
        }

        //ensure the rest is empty. GO through everything but 0 and 2
        Integer[] emptyIndices = new Integer[instance.MAXEDITDISTANCE - 2];
        emptyIndices[0] = 1;
        for (int i = 3; i < instance.MAXEDITDISTANCE; i++) {
            emptyIndices[i - 2] = i;
        }
        assertTrue(checkRepresentationEmpty(rn1, emptyIndices));
        assertTrue(checkRepresentationEmpty(rn2, emptyIndices));
        assertTrue(checkRepresentationEmpty(rn3, emptyIndices));
        assertTrue(checkRepresentationEmpty(rn4, emptyIndices));
        assertTrue(checkRepresentationEmpty(rn5, emptyIndices));
        assertTrue(checkRepresentationEmpty(rn6, emptyIndices));
        assertTrue(checkRepresentationEmpty(rn79, emptyIndices));
        assertTrue(checkRepresentationEmpty(rn810, emptyIndices));
    }

    private void checkRepresentativeEdgesCorrect(RepresentativeTree rtN1, RepresentativeTree rtN4, RepresentativeTree rtN79) {
        RepresentativeEdge re12 = (RepresentativeEdge) rtN1.getEdge(1, 2);
        RepresentativeEdge re13 = (RepresentativeEdge) rtN1.getEdge(1, 3);
        RepresentativeEdge re45 = (RepresentativeEdge) rtN4.getEdge(4, 5);
        RepresentativeEdge re56 = (RepresentativeEdge) rtN4.getEdge(5, 6);
        RepresentativeEdge re78910;
        if (rtN79.getEdge(7, 8) != null) {
            re78910 = (RepresentativeEdge) rtN79.getEdge(7, 8);
        } else {
            re78910 = (RepresentativeEdge) rtN79.getEdge(9, 10);
        }

        //check represented at start
        assertTrue(checkRepresentation(re12, 0, Arrays.asList(e12)));
        assertTrue(checkRepresentation(re13, 0, Arrays.asList(e13)));
        assertTrue(checkRepresentation(re45, 0, Arrays.asList(e45)));
        assertTrue(checkRepresentation(re56, 0, Arrays.asList(e56)));

        //identical trees collapse
        assertTrue(checkRepresentation(re78910, 0, Arrays.asList(e78, e910)));

        //can use either to represent it
        if (rtN1.maxEditDistance == instance.MAXEDITDISTANCE) {
            //either e12 or e13 must represent e45
            assertTrue(checkRepresentation(re12, 2, Arrays.asList(e45))
                    ^ checkRepresentation(re13, 2, Arrays.asList(e45)));

            //ensure one of the two doesn't represent anything
            assertTrue(checkRepresentationEmpty(re12, 2)
                    ^ checkRepresentationEmpty(re13, 2));
        } else {
            //e45 represents either e12 or e13
            assertTrue(checkRepresentation(re45, 2, Arrays.asList(e12))
                    ^ checkRepresentation(re45, 2, Arrays.asList(e13)));

            //ensure 56 does not represent anything
            assertTrue(checkRepresentationEmpty(re56, 2));
        }

        //ensure the rest is empty. GO through everything but 0 and 2
        Integer[] emptyIndices = new Integer[instance.MAXEDITDISTANCE - 2];
        emptyIndices[0] = 1;
        for (int i = 3; i < instance.MAXEDITDISTANCE; i++) {
            emptyIndices[i - 2] = i;
        }
        assertTrue(checkRepresentationEmpty(re12, emptyIndices));
        assertTrue(checkRepresentationEmpty(re13, emptyIndices));
        assertTrue(checkRepresentationEmpty(re45, emptyIndices));
        assertTrue(checkRepresentationEmpty(re56, emptyIndices));
        assertTrue(checkRepresentationEmpty(re78910, emptyIndices));
    }

    private void checkJsonCorrect(RepresentativeTree rtN1, RepresentativeTree rtN4, RepresentativeTree rtN79, String filePath) throws IOException {

        //actual nodes
        RepresentativeNodeJson rnj1 = null;
        RepresentativeNodeJson rnj2 = null;
        RepresentativeNodeJson rnj3 = null;
        RepresentativeNodeJson rnj4 = null;
        RepresentativeNodeJson rnj5 = null;
        RepresentativeNodeJson rnj6 = null;
        RepresentativeNodeJson rnj79 = null;
        RepresentativeNodeJson rnj810 = null;;

        //read the file for the trees of size 2
        JsonReader reader = new JsonReader(new FileReader(filePath + "2.json"));
        Gson gson = new Gson();
        RepresentativeNodeJson[] jsonTreeList = gson.fromJson(reader, RepresentativeNodeJson[].class
        );

        for (RepresentativeNodeJson rnj : jsonTreeList) {
            if (rnj.id == 7) {
                rnj79 = rnj;
                rnj810 = rnj.getChild(8);
            } else if (rnj.id == 9) {
                rnj79 = rnj;
                rnj810 = rnj.getChild(10);
            } else {
                fail("Found a tree in the json that should not be represented");
            }
        }
        //close the reader so the file can get deleted
        reader.close();

        //read for trees of size 3
        reader = new JsonReader(new FileReader(filePath + "3.json"));
        gson = new Gson();
        jsonTreeList = gson.fromJson(reader, RepresentativeNodeJson[].class
        );
        for (RepresentativeNodeJson rnj : jsonTreeList) {
            if (rnj.id == 1) {
                rnj1 = rnj;
                rnj2 = rnj.getChild(2);
                rnj3 = rnj.getChild(3);
            } else if (rnj.id == 4) {
                rnj4 = rnj;
                rnj5 = rnj.getChild(5);
                rnj6 = rnj5.getChild(6);
            } else {
                fail("Found a tree in the json that should not be represented");
            }
        }

        //check structure
        assertTrue(checkCollectionContentEqual(Arrays.asList(rnj2, rnj3), rnj1.children));
        assertTrue(rnj2.children.isEmpty());
        assertTrue(rnj3.children.isEmpty());

        assertTrue(checkCollectionContentEqual(Arrays.asList(rnj5), rnj4.children));
        assertTrue(checkCollectionContentEqual(Arrays.asList(rnj6), rnj5.children));
        assertTrue(rnj6.children.isEmpty());

        assertTrue(checkCollectionContentEqual(Arrays.asList(rnj810), rnj79.children));
        assertTrue(rnj810.children.isEmpty());

        //expected nodes, used to check the representation and edit distance
        RepresentativeNodeJson rnj1E = new RepresentativeNodeJson(rtN1);
        RepresentativeNodeJson rnj2E = rnj1E.getChild(n2.id);
        RepresentativeNodeJson rnj3E = rnj1E.getChild(n3.id);

        RepresentativeNodeJson rnj4E = new RepresentativeNodeJson(rtN4);
        RepresentativeNodeJson rnj5E = rnj4E.getChild(n5.id);
        RepresentativeNodeJson rnj6E = rnj5E.getChild(n6.id);

        RepresentativeNodeJson rnj79E = new RepresentativeNodeJson(rtN79);
        RepresentativeNodeJson rnj810E = rnj79E.getChild(n8.id);
        if (rnj810E == null) {
            rnj810E = rnj79E.getChild(n10.id);
        }

        //check the preservation of the maximum edit distances
        assertTrue(rnj1.maxEditDistance == rnj1E.maxEditDistance);
        assertTrue(rnj4.maxEditDistance == rnj4E.maxEditDistance);
        assertTrue(rnj79.maxEditDistance == rnj79E.maxEditDistance);

        //check items represented at the correct time
        assertTrue(checkCollectionContentEqual(rnj1E.representations, rnj1.representations));
        assertTrue(checkCollectionContentEqual(rnj2E.representations, rnj2.representations));
        assertTrue(checkCollectionContentEqual(rnj3E.representations, rnj3.representations));
        assertTrue(checkCollectionContentEqual(rnj4E.representations, rnj4.representations));
        assertTrue(checkCollectionContentEqual(rnj5E.representations, rnj5.representations));
        assertTrue(checkCollectionContentEqual(rnj6E.representations, rnj6.representations));
        assertTrue(checkCollectionContentEqual(rnj79E.representations, rnj79.representations));
        assertTrue(checkCollectionContentEqual(rnj810E.representations, rnj810.representations));

        //close the reader so the file can get deleted
        reader.close();
    }
}
