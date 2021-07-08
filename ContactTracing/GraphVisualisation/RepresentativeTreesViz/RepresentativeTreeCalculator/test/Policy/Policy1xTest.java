/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Policy;

import InfectionTreeGenerator.Event.AlertEvent;
import InfectionTreeGenerator.Event.Event;
import InfectionTreeGenerator.Event.VirusEvent;
import InfectionTreeGenerator.Graph.Infection.InfectionGraph;
import InfectionTreeGenerator.Graph.Infection.InfectionNode;
import java.util.ArrayList;
import java.util.HashMap;
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
public class Policy1xTest extends PolicyTest {

    InfectionNode nSymptomatic, nSecondRoot, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11;
    AlertEvent ae1;

    public Policy1xTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        //Symptomatic at time 24
        VirusEvent ve = new VirusEvent(1, 24, "SYMPTOMATIC", "OLD:EXPOSED");
        contactsFromUser = new HashMap();

        //times set later
        g = new InfectionGraph();
        nSecondRoot = new InfectionNode(0, 1);//second root infection occurs at t=1
        nSymptomatic = new InfectionNode(1, 1);//root infection occurs at t=1
        n2 = new InfectionNode(2, 0);
        n3 = new InfectionNode(3, 0);
        n4 = new InfectionNode(4, 0);
        n5 = new InfectionNode(5, 0);
        n6 = new InfectionNode(6, 0);
        n7 = new InfectionNode(7, 0);
        n8 = new InfectionNode(8, 0);
        n9 = new InfectionNode(9, 0);
        n10 = new InfectionNode(10, 0);
        n11 = new InfectionNode(11, 0);
        g.addNodes(nSymptomatic, nSecondRoot, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11);

        //Everyone has the app if percentage higher than 50
        randomAppPercentages = new HashMap();
        for (int i = 0; i < 11; i++) {
            randomAppPercentages.put(i, 0.5);
        }

        //subtree 1
        addContact(nSymptomatic, n2, 13, true);//contact that results in an infection
        addContact(nSymptomatic, n2, 14, false);//last contact is 10 days before alert
        addContact(n2, n3, 26, true);//n2 is still isolating
        addContact(n2, n4, 30, true);//n2 is no longer isolating
        addContact(n3, n5, 60, true);//part of chain

        //subtree2
        addContact(nSymptomatic, n6, 7, true);//infected, but no recent contact
        addContact(n6, n7, 25, true);//chain also infected

        //subtree3
        addContact(nSecondRoot, n8, 10, true);//infected via different path long ago
        addContact(nSymptomatic, n8, 20, false);//recent contact with alerted Node
        addContact(n8, n9, 25, true);//infection from n8 in isolation window

        //subtree4. Alertednode isolates and prevents
        addContact(nSymptomatic, n10, 25, true);//contact in isolation period
        addContact(n10, n11, 26, true);//chain from n11

        events = new ArrayList();
        events.add(ve);
        g.addEventData(events);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of addPolicyData method, of class Policy1a.
     */
    @Test
    public void testAddPolicyData() {
        System.out.println("addPolicyData");

        List<Event> events = new ArrayList();
        events.add(ae1);

        Policy1x instance = new Policy1x(g, events, contactsFromUser, randomAppPercentages);
        instance.addPolicyData();

        assertEquals(0, n2.policies.size());
        assertEquals(0, n3.policies.size());
        assertEquals(0, n4.policies.size());
        assertEquals(0, n5.policies.size());
        assertEquals(0, n6.policies.size());
        assertEquals(0, n7.policies.size());
        assertEquals(0, n8.policies.size());
        assertEquals(0, n9.policies.size());

        assertEquals(1, n10.policies.size());
        assertTrue(n10.policies.contains("1xOrigin"));

        assertEquals(1, n11.policies.size());
        assertTrue(n11.policies.contains("1x"));
    }

    @Test
    public void testNoApp() {
        System.out.println("addPolicyDataNoApp");

        List<Event> events = new ArrayList();
        events.add(ae1);

        Policy1x instance = new Policy1x(g, events, contactsFromUser, randomAppPercentages);
        instance.addPolicyData();

        assertEquals(0, nSymptomatic.policies.size());
        assertEquals(0, n2.policies.size());
        assertEquals(0, n3.policies.size());
        assertEquals(0, n4.policies.size());
        assertEquals(0, n5.policies.size());
        assertEquals(0, n6.policies.size());
        assertEquals(0, n7.policies.size());
        assertEquals(0, n8.policies.size());
        assertEquals(0, n9.policies.size());

        assertEquals(1, n10.policies.size());
        assertTrue(n10.policies.contains("1xOrigin"));

        assertEquals(1, n11.policies.size());
        assertTrue(n11.policies.contains("1x"));
    }
}
