/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfectionTreeGenerator.Graph.GraphAlgorithms;

import InfectionTreeGenerator.Graph.GraphAlgorithms.DistanceMeasures.TreeDistanceMeasure;
import InfectionTreeGenerator.Graph.Infection.InfectionEdge;
import InfectionTreeGenerator.Graph.Infection.InfectionNode;
import InfectionTreeGenerator.Graph.Tree;
import java.util.Arrays;
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
public class DominatingSetCalculatorImplicitTest {

    Tree t1N = new Tree(1);
    Tree t2N = new Tree(2);
    Tree t3N = new Tree(3);
    Tree t4N = new Tree(4);
    Tree t5N = new Tree(5);
    Tree t6N = new Tree(6);
    Tree t7N = new Tree(7);

    List<Tree> trees = Arrays.asList(t1N, t2N, t3N, t4N, t5N, t6N, t7N);

    DominatingSetCalculatorImplicit instance;

    private class MockTreeDistanceMeasure implements TreeDistanceMeasure {

        @Override
        public double getDistance(Tree<InfectionNode, InfectionEdge> t1, Tree<InfectionNode, InfectionEdge> t2) {
            return Math.max(t1.id, t2.id) - Math.min(t1.id, t2.id);
        }

    }

    TreeDistanceMeasure dm = new MockTreeDistanceMeasure();

    public DominatingSetCalculatorImplicitTest() {
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
     * Test of getDominatingSet method, of class
     * DominatingSetCalculatorImplicit.
     */
    @Test
    public void testGetDominatingSet() {
        DominatingSetCalculatorImplicit dsc = new DominatingSetCalculatorImplicit(trees,dm);
        List<Integer> dominatingSet = dsc.getDominatingSet(0);
        assertEquals(7, dominatingSet.size()); //all nodes
        assertTrue(dominatingSet.contains(1));
        assertTrue(dominatingSet.contains(2));
        assertTrue(dominatingSet.contains(3));
        assertTrue(dominatingSet.contains(4));
        assertTrue(dominatingSet.contains(5));
        assertTrue(dominatingSet.contains(6));
        assertTrue(dominatingSet.contains(7));

        //sensible implementation should have at most this many nodes
        dominatingSet = dsc.getDominatingSet(1);
        assertTrue(dominatingSet.size() <= 4);

        dominatingSet = dsc.getDominatingSet(2);
        assertTrue(dominatingSet.size() <= 3);

        dominatingSet = dsc.getDominatingSet(7);
        assertEquals(1, dominatingSet.size()); //single node
    }

    /**
     * Test of trimDominatingSet method, of class
     * DominatingSetCalculatorImplicit.
     */
    @Test
    public void testTrimDominatingSet() {
        DominatingSetCalculatorImplicit dsc = new DominatingSetCalculatorImplicit(trees,dm);
        List<Integer> dominatingSet = dsc.getDominatingSet(0);
        assertEquals(7, dominatingSet.size()); //all nodes

        List<Integer> trimmedDominatingSet = dsc.trimDominatingSet(dominatingSet, 0);
        //nothing changed
        assertEquals(7, trimmedDominatingSet.size());

        trimmedDominatingSet = dsc.trimDominatingSet(dominatingSet, 1);
        assertTrue(trimmedDominatingSet.size() <= 4);

        trimmedDominatingSet = dsc.trimDominatingSet(dominatingSet, 2);
        assertTrue(trimmedDominatingSet.size() <= 3);

        trimmedDominatingSet = dsc.trimDominatingSet(dominatingSet, 7);
        assertEquals(1, trimmedDominatingSet.size()); //single node

        //test it also works without a full base
        trimmedDominatingSet = dsc.trimDominatingSet(dominatingSet, 2);
        trimmedDominatingSet = dsc.trimDominatingSet(trimmedDominatingSet, 7);
        assertEquals(1, trimmedDominatingSet.size()); //single node
    }

}
