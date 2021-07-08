/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Import.SimulatedData.InfectionMapParser;
import InfectionTreeGenerator.Graph.Infection.InfectionEdge;
import InfectionTreeGenerator.Graph.Infection.InfectionGraph;
import InfectionTreeGenerator.Graph.Infection.InfectionNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author MaxSondag
 */
public class InfectionMapParserTest {

    public InfectionMapParserTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test of constructGraph method, of class InfectionMapParser.
     */
    @Test
    public void testConstructGraph() {
        System.out.println("constructGraph");
        List<String> testContent = new ArrayList();
        testContent.add("1(0.0)      ->     [2(1.0), 3(5.0)");
        testContent.add("            ->     2(1.0) -> [ 4(2.0)]");
        testContent.add("   ");
        testContent.add("8(0.0)      ->     [7(2.0), 6(2.0)");
        testContent.add("            ->     7(2.0) -> [ 9(3.0),15(11.0)]");
        testContent.add("                   ->          9(3.0) -> [11(5.0)]");
        testContent.add("            ->     6(2.0) -> [42(43.0)]");

        InfectionMapParser instance = new InfectionMapParser(testContent);

        InfectionNode n1 = new InfectionNode(1, 0.0);
        InfectionNode n2 = new InfectionNode(2, 1.0);
        InfectionNode n3 = new InfectionNode(3, 5.0);
        InfectionNode n4 = new InfectionNode(4, 2.0);
        InfectionNode n6 = new InfectionNode(6, 2.0);
        InfectionNode n7 = new InfectionNode(7, 2.0);
        InfectionNode n8 = new InfectionNode(8, 0.0);
        InfectionNode n9 = new InfectionNode(9, 3.0);
        InfectionNode n11 = new InfectionNode(11, 5.0);
        InfectionNode n15 = new InfectionNode(15, 11.0);
        InfectionNode n42 = new InfectionNode(42, 34.0);

        InfectionGraph expResult = new InfectionGraph();

        expResult.addNode(n1);
        expResult.addNode(n2);
        expResult.addNode(n3);
        expResult.addNode(n4);
        expResult.addNode(n6);
        expResult.addNode(n7);
        expResult.addNode(n8);
        expResult.addNode(n9);
        expResult.addNode(n11);
        expResult.addNode(n15);
        expResult.addNode(n42);


        expResult.addEdge(new InfectionEdge(n1, n2, 1.0));
        expResult.addEdge(new InfectionEdge(n1, n3, 5.0));

        expResult.addEdge(new InfectionEdge(n2, n4, 2.0));

        expResult.addEdge(new InfectionEdge(n8, n7, 2.0));
        expResult.addEdge(new InfectionEdge(n8, n6, 2.0));

        expResult.addEdge(new InfectionEdge(n7, n9, 3.0));
        expResult.addEdge(new InfectionEdge(n7, n15, 11.0));

        expResult.addEdge(new InfectionEdge(n9, n11, 5.0));

        expResult.addEdge(new InfectionEdge(n6, n42, 43.0));

        InfectionGraph result = instance.constructGraph();

        Collection<InfectionNode> resultNodes = result.getNodes();
        Collection<InfectionNode> expNodes = expResult.getNodes();
        verifyNodesEqual(resultNodes, expNodes);
        
        Collection<InfectionEdge> resultEdges = result.getEdges();
        Collection<InfectionEdge> expEdges = expResult.getEdges();

        verifyEdgesEqual(resultEdges, expEdges);

    }

    private boolean verifyNodesEqual(Collection<InfectionNode> resultNodes, Collection<InfectionNode> expNodes) {
        assertTrue(resultNodes.size() == expNodes.size());
        for (InfectionNode rN : resultNodes) {
            boolean foundEqual = false;
            for (InfectionNode eN : expNodes) {
                if (rN.id != eN.id) {
                    continue;
                }
                if (rN.exposedTime != eN.exposedTime) {
                    return false;
                }
                foundEqual = true;
            }
            if (!foundEqual) {
                return false;
            }
        }
        return true;
    }

     private boolean verifyEdgesEqual(Collection<InfectionEdge> resultEdges, Collection<InfectionEdge> expEdges) {
        assertTrue(resultEdges.size() == expEdges.size());
        for (InfectionEdge rE : resultEdges) {
            boolean foundEqual = false;
            for (InfectionEdge eE : expEdges) {
                if (rE.source.id != eE.source.id || rE.target.id != eE.target.id) {
                    continue;
                }
                if (rE.exposedTime != eE.exposedTime) {
                    return false;
                }
                foundEqual = true;
            }
            if (!foundEqual) {
                return false;
            }
        }
        return true;
    }
    
}
