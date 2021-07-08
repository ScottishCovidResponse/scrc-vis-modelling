/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfectionTreeGenerator.Graph.GraphAlgorithms.DistanceMeasures.TreeEditDistance;

import InfectionTreeGenerator.Graph.GraphAlgorithms.DistanceMeasures.TreeEditDistance.TreeEditDistanceCalculator;
import InfectionTreeGenerator.Graph.GraphAlgorithms.DistanceMeasures.TreeEditDistance.TEDMapping;
import Utility.Pair;
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
public class TreeEditDistanceCalculatorTest {

    Tree t1, t2, t3, t4, t5;
    Node n1, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11;
    Edge e12, e13, e45, e56, e78, e910;

    public TreeEditDistanceCalculatorTest() {
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
     * Test of getTreeEditDistance method, of class TreeEditDistanceCalculator.
     */
    @Test
    public void testGetTreeEditDistance() {
        System.out.println("getTreeEditDistance");
        TreeEditDistanceCalculator instance = new TreeEditDistanceCalculator();

        //check basic distance
        int ted = instance.getTreeEditDistance(t1, t2);
        assertEquals(2, ted);

        //inverse is equal
        ted = instance.getTreeEditDistance(t2, t1);
        assertEquals(2, ted);

        //can be 0
        ted = instance.getTreeEditDistance(t3, t4);
        assertEquals(0, ted);

        //can be 1
        ted = instance.getTreeEditDistance(t1, t4);
        assertEquals(1, ted);

        //can go towards a singleton node
        ted = instance.getTreeEditDistance(t1, t5);
        assertEquals(2, ted);

        //and also from a singleton node
        ted = instance.getTreeEditDistance(t5, t1);
        assertEquals(2, ted);
        
        
        //test
        
        //check that the values are stored correctly. 8 as inverse is automatically stored
        assertEquals(instance.tedMapping.keySet().size(),8);
        
        
        
        //check that the mapping is correct
        TEDMapping t1t2Mapping = (TEDMapping) instance.tedMapping.get(new Pair(t1,t2));
        
        //root should be mapped
        assertEquals(n4, t1t2Mapping.getMappedNode(n1));

        //either n2 or n3 maps to n3
        Node mappedNode = t1t2Mapping.getMappedNode(n2);
        if (mappedNode != null) {
            assertEquals(n5, mappedNode);//verify it maps correctly
            assertEquals(e45, t1t2Mapping.getMappedEdge(e12)); //verify it maps the correct edge
            assertEquals(null, t1t2Mapping.getMappedEdge(e13)); //and only that edge
        } else {
            mappedNode = t1t2Mapping.getMappedNode(n3); //n3 should map instead
            assertEquals(n5, mappedNode); //verify it maps correctly
            assertEquals(e45, t1t2Mapping.getMappedEdge(e13)); //verify it maps the correct edge
            assertEquals(null, t1t2Mapping.getMappedEdge(e12)); //and only that edge
        }

    }

}
