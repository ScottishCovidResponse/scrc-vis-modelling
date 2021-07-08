/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfectionTreeGenerator.Graph;

import InfectionTreeGenerator.Graph.Edge;
import InfectionTreeGenerator.Graph.Node;
import InfectionTreeGenerator.Graph.Tree;
import java.util.List;
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
public class TreeTest {

    public TreeTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    Tree t1, t2;
    Node n1, n2, n3, n4, n5, n6, n7, n8, n9, n10;
    Edge e12, e13, e45, e46, e47, e58, e59, e710;

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
        n7 = new Node(7);
        n8 = new Node(8);
        n9 = new Node(9);
        n10 = new Node(10);
        e45 = new Edge(n4, n5);
        e46 = new Edge(n4, n6);
        e47 = new Edge(n4, n7);
        e58 = new Edge(n5, n8);
        e59 = new Edge(n5, n9);
        e710 = new Edge(n7, n10);

        t2 = new Tree();
        t2.addNodes(n4, n5, n6, n7, n8, n9, n10);
        t2.addEdges(e45, e46, e47, e58, e59, e710);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of isAncestor method, of class Tree.
     */
    @Test
    public void testIsAncestor() {
        System.out.println("isAncestor");

        //check valid ancestors
        assertEquals(true, t1.isAncestor(n1, n2));
        assertEquals(true, t1.isAncestor(n1, n3));

        assertEquals(false, t1.isAncestor(n2, n1));//not inverted
        assertEquals(false, t1.isAncestor(n3, n1));//not inverted
        assertEquals(false, t1.isAncestor(n3, n2));//not from neighbor
        assertEquals(false, t1.isAncestor(n2, n3));//not from neighbor
        assertEquals(false, t1.isAncestor(n1, n1));  //no ancestor of yourself

        //check all ancestors
        assertEquals(true, t2.isAncestor(n4, n5));
        assertEquals(true, t2.isAncestor(n4, n6));
        assertEquals(true, t2.isAncestor(n4, n7));
        assertEquals(true, t2.isAncestor(n4, n8));
        assertEquals(true, t2.isAncestor(n4, n9));
        assertEquals(true, t2.isAncestor(n4, n10));
        assertEquals(true, t2.isAncestor(n5, n8));
        assertEquals(true, t2.isAncestor(n5, n9));
        assertEquals(true, t2.isAncestor(n7, n10));

        assertEquals(false, t2.isAncestor(n6, n4));//not inverted
        assertEquals(false, t2.isAncestor(n6, n5));//not from same level
        assertEquals(false, t2.isAncestor(n6, n10));//not from deeper

    }

    /**
     * Test of getParent method, of class Tree.
     */
    @Test
    public void testGetParent() {
        System.out.println("getParent");

        assertEquals(null, t1.getParent(n1));
        assertEquals(n1, t1.getParent(n2));
        assertEquals(n1, t1.getParent(n3));

        assertEquals(null, t2.getParent(n4));
        assertEquals(n4, t2.getParent(n5));
        assertEquals(n4, t2.getParent(n6));
        assertEquals(n4, t2.getParent(n7));
        assertEquals(n5, t2.getParent(n8));
        assertEquals(n5, t2.getParent(n9));
        assertEquals(n7, t2.getParent(n10));

    }

    /**
     * Test of calculateRoot method, of class Tree.
     */
    @Test
    public void testCalculateRoot() {
        System.out.println("calculateRoot");

        assertEquals(n1, t1.calculateRoot());
        assertEquals(n4, t2.calculateRoot());
    }

    /**
     * Test of getDepth method, of class Tree.
     */
    @Test
    public void testGetDepth_0args() {
        System.out.println("getDepth");
        int expResult = 1;
        int result = t1.getDepth();
        assertEquals(expResult, result);

        expResult = 2;
        result = t2.getDepth();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDepth method, of class Tree.
     */
    @Test
    public void testGetDepth_GenericType() {
        System.out.println("getDepth");
        int expResult = 0;
        int result = t1.getDepth(n1);
        assertEquals(expResult, result);

        expResult = 1;
        result = t1.getDepth(n2);
        assertEquals(expResult, result);

        expResult = 1;
        result = t1.getDepth(n3);
        assertEquals(expResult, result);

        expResult = 0;
        result = t2.getDepth(n4);
        assertEquals(expResult, result);

        expResult = 1;
        result = t2.getDepth(n5);
        assertEquals(expResult, result);

        expResult = 1;
        result = t2.getDepth(n6);
        assertEquals(expResult, result);

        expResult = 1;
        result = t2.getDepth(n7);
        assertEquals(expResult, result);

        expResult = 2;
        result = t2.getDepth(n8);
        assertEquals(expResult, result);

        expResult = 2;
        result = t2.getDepth(n9);
        assertEquals(expResult, result);

        expResult = 2;
        result = t2.getDepth(n10);
        assertEquals(expResult, result);

    }

    @Test
    public void testSortTree() {
        //verify not sorted before
        List<Edge> edges = n4.edges;
        assertNotEquals(e47, edges.get(0));
        assertNotEquals(e45, edges.get(1));
        assertNotEquals(e46, edges.get(2));

        //verify sorted afterwards
        t2.sortTree();
        List<Edge> sortedEdges = n4.edges;

        assertEquals(e47, sortedEdges.get(0));
        assertEquals(e45, sortedEdges.get(1));
        assertEquals(e46, sortedEdges.get(2));
    }
}
