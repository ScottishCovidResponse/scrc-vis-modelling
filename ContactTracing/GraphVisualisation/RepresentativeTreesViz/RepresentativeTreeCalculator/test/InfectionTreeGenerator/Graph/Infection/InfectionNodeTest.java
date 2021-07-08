/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfectionTreeGenerator.Graph.Infection;

import InfectionTreeGenerator.Graph.Infection.InfectionNode;
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
public class InfectionNodeTest {

    public InfectionNodeTest() {
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
     * Test of addAlert method, of class InfectionNode.
     */
    @Test
    public void testAddAlert() {
        System.out.println("addAlert");

        InfectionNode instance = new InfectionNode(0, 0);
        instance.addAlert(1, "1");
        instance.addAlert(0, "0");

        assertEquals(2, instance.alerts.size());
        assertEquals("0", instance.alerts.firstEntry().getValue());
        assertEquals("1", instance.alerts.lastEntry().getValue());

        assertEquals(0, instance.alerts.firstEntry().getKey(), 0);
        assertEquals(1, instance.alerts.lastEntry().getKey(), 0);

    }


    /**
     * Test of addVirusProgression method, of class InfectionNode.
     */
    @Test
    public void testAddVirusProgression() {
        System.out.println("addVirusProgression");
        
        InfectionNode instance = new InfectionNode(0, 0);
        instance.addVirusProgression(1, "1");
        instance.addVirusProgression(0, "0");

        assertEquals(2, instance.virusProgression.size());
        assertEquals("0", instance.virusProgression.firstEntry().getValue());
        assertEquals("1", instance.virusProgression.lastEntry().getValue());

        assertEquals(0, instance.virusProgression.firstEntry().getKey(), 0);
        assertEquals(1, instance.virusProgression.lastEntry().getKey(), 0);
    }

}
