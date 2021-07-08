/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfectionTreeGenerator.Graph.GraphAlgorithms.DistanceMeasures.TreeEditDistance;

import InfectionTreeGenerator.Graph.GraphAlgorithms.DistanceMeasures.TreeEditDistance.TreeEditDistanceLP;
import InfectionTreeGenerator.Graph.GraphAlgorithms.DistanceMeasures.TreeEditDistance.TEDMapping;
import InfectionTreeGenerator.Graph.Edge;
import InfectionTreeGenerator.Graph.Node;
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
public class TreeEditDistanceLPTest {

    Tree t1, t2, t3, t4, t5;
    Node n1, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11;
    Edge e12, e13, e45, e56, e78, e910;

    public TreeEditDistanceLPTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        n1 = new Node(1);
        n2 = new Node(2);
        n3 = new Node(3);
        e12 = new Edge(n1, n2);
        e13 = new Edge(n1, n3);
        t1 = new Tree();
        t1.addNodes(n1, n2, n3);
        t1.addEdges(e12, e13);

        n4 = new Node(4);
        n5 = new Node(5);
        n6 = new Node(6);
        e45 = new Edge(n4, n5);
        e56 = new Edge(n5, n6);
        t2 = new Tree();
        t2.addNodes(n4, n5, n6);
        t2.addEdges(e45, e56);

        n7 = new Node(7);
        n8 = new Node(8);
        e78 = new Edge(n7, n8);
        t3 = new Tree();
        t3.addNodes(n7, n8);
        t3.addEdges(e78);

        n9 = new Node(9);
        n10 = new Node(10);
        e910 = new Edge(n9, n10);
        t4 = new Tree();
        t4.addNodes(n9, n10);
        t4.addEdges(e910);

        n11 = new Node(11);
        t5 = new Tree();
        t5.addNodes(n11);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getMapping method, of class TreeEditDistanceLP.
     */
    @Test
    public void testGetMapping() {
        System.out.println("getMapping");
        //solve it to get the mapping
        TreeEditDistanceLP instance = new TreeEditDistanceLP(t1, t2);
        instance.solve();

        TEDMapping result = instance.getMapping();

        //root should be mapped
        assertEquals(n4, result.getMappedNode(n1));

        //either n2 or n3 maps to n3
        Node mappedNode = result.getMappedNode(n2);
        if (mappedNode != null) {
            assertEquals(n5, mappedNode);//verify it maps correctly
            assertEquals(e45, result.getMappedEdge(e12)); //verify it maps the correct edge
            assertEquals(null, result.getMappedEdge(e13)); //and only that edge
        } else {
            mappedNode = result.getMappedNode(n3); //n3 should map instead
            assertEquals(n5, mappedNode); //verify it maps correctly
            assertEquals(e45, result.getMappedEdge(e13)); //verify it maps the correct edge
            assertEquals(null, result.getMappedEdge(e12)); //and only that edge
        }
    }

    /**
     * Test of solve method, of class TreeEditDistanceLP.
     */
    @Test
    public void testSolve() {
        System.out.println("testSolve");

        //check basic distance
        TreeEditDistanceLP instance = new TreeEditDistanceLP(t1, t2);
        int ted = instance.solve();
        assertEquals(2, ted);

        //inverse is equal
        instance = new TreeEditDistanceLP(t2, t1);
        ted = instance.solve();
        assertEquals(2, ted);

        //can be 0
        instance = new TreeEditDistanceLP(t3, t4);
        ted = instance.solve();
        assertEquals(0, ted);

        //can be 1
        instance = new TreeEditDistanceLP(t1, t4);
        ted = instance.solve();
        assertEquals(1, ted);

        //can go towards a singleton node
        instance = new TreeEditDistanceLP(t1, t5);
        ted = instance.solve();
        assertEquals(2, ted);

        //and also from a singleton node
        instance = new TreeEditDistanceLP(t5, t1);
        ted = instance.solve();
        assertEquals(2, ted);

    }

}
