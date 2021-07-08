/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Policy;

import InfectionTreeGenerator.Event.AlertEvent;
import InfectionTreeGenerator.Event.Event;
import InfectionTreeGenerator.Event.InfectionEvent;
import InfectionTreeGenerator.Event.VirusEvent;
import InfectionTreeGenerator.Graph.Infection.InfectionGraph;
import InfectionTreeGenerator.Graph.Infection.InfectionNode;
import java.util.ArrayList;
import java.util.Arrays;
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
public class Policy1aTest extends PolicyTest {

    InfectionNode nSymptomatic, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11;

    public Policy1aTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        contactsFromUser = new HashMap();

        //Symptomatic at time 3
        VirusEvent ve = new VirusEvent(1, 3, "SYMPTOMATIC", "OLD:EXPOSED");

        //times set later
        g = new InfectionGraph();

        nSymptomatic = new InfectionNode(1, 1);
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
        g.addNodes(nSymptomatic, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11);

        //Everyone has the app if percentage higher than 50
        randomAppPercentages = new HashMap();
        for (int i = 0; i < 11; i++) {
            randomAppPercentages.put(i, 0.5);
        }

        //subtree 1
        addContact(nSymptomatic, n2, 1, false);
        addContact(nSymptomatic, n2, 2, true);//contact that results in an infection
        addContact(n2, n3, 10, true);
        addContact(n2, n4, 20, true);
        addContact(n3, n5, 30, true);

        //subtree2
        addContact(nSymptomatic, n6, 7, true);//contact infected after symptomatic
        addContact(n6, n7, 25, true);

        //subtree3
        addContact(nSymptomatic, n8, 1, false);//contact that doesn't result in an infection (yet)
        addContact(nSymptomatic, n8, 40, true);
        addContact(n8, n9, 42, true);

        //subtree4. Symptomaticnode isolates and prevents
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

        Policy1a instance = new Policy1a(g, events, contactsFromUser, randomAppPercentages, 1);
        instance.addPolicyData();

        assertEquals(0, nSymptomatic.policies.size());
        assertEquals(0, n2.policies.size());
        assertEquals(0, n4.policies.size());
        assertEquals(0, n8.policies.size());
        assertEquals(0, n9.policies.size());
        assertEquals(0, n10.policies.size());
        assertEquals(0, n11.policies.size());

        assertEquals(1, n3.policies.size());
        assertTrue(n3.policies.contains("1aA100Origin"));

        assertEquals(1, n5.policies.size());
        assertTrue(n5.policies.contains("1aA100"));

        assertEquals(1, n6.policies.size());
        assertTrue(n6.policies.contains("1aA100Origin"));

        assertEquals(1, n7.policies.size());
        assertTrue(n7.policies.contains("1aA100"));
    }

    /**
     * Test of addPolicyData method, of class Policy1a.
     */
    @Test
    public void testNoApp() {
        System.out.println("addPolicyDataNoApp");

        Policy1a instance = new Policy1a(g, events, contactsFromUser, randomAppPercentages, 0);
        instance.addPolicyData();

        assertEquals(0, nSymptomatic.policies.size());
        assertEquals(0, n2.policies.size());
        assertEquals(0, n3.policies.size());
        assertEquals(0, n4.policies.size());
        assertEquals(0, n5.policies.size());
        assertEquals(0, n8.policies.size());
        assertEquals(0, n9.policies.size());
        assertEquals(0, n10.policies.size());
        assertEquals(0, n11.policies.size());

        assertEquals(1, n6.policies.size());//direct result of symptomatic node isolating
        assertTrue(n6.policies.contains("1aA0Origin"));

        assertEquals(1, n7.policies.size());//chained to n6
        assertTrue(n7.policies.contains("1aA0"));
    }

}
