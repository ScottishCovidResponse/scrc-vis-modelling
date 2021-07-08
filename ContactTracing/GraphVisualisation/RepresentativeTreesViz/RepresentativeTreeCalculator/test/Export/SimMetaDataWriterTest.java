/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Export;

import Contact.Contact;
import Export.PolicyCompartmentsJson.Compartment;
import Policy.Policy;
import Policy.Policy0;
import Policy.Policy1a;
import Policy.Policy1b;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import InfectionTreeGenerator.Event.Event;
import InfectionTreeGenerator.Event.InfectionEvent;
import InfectionTreeGenerator.Event.VirusEvent;
import InfectionTreeGenerator.Graph.Infection.InfectionEdge;
import InfectionTreeGenerator.Graph.Infection.InfectionGraph;
import InfectionTreeGenerator.Graph.Infection.InfectionNode;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
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
public class SimMetaDataWriterTest {

    public SimMetaDataWriterTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    InfectionGraph ig;
    List<Event> events;
    HashMap<Integer, Set<Contact>> contactsFromUser;
    ArrayList<Policy> policies;
    String testFileLocation = "./test.json";

    @Before
    public void setUp() {

        ig = new InfectionGraph();
        events = new ArrayList();
        contactsFromUser = new HashMap();
        policies = new ArrayList();

        InfectionNode n1 = new InfectionNode(0, 0);
        InfectionNode n2 = new InfectionNode(1, 2);
        ig.addNode(n1);
        ig.addNode(n2);

        addContact(1, 2, 1, false);//normal contact
        addContact(0, 1, 2, true);//contact that results in infection

        n1.policies.add("0");
        n2.policies.add("0");
        policies.add(new Policy0(ig, events, contactsFromUser, null));

        n2.policies.add("1aA50");//add a policy
        policies.add(new Policy1a(ig, events, contactsFromUser, null, 0.5));

        n1.addVirusProgression(0, "Initial");
        n1.addVirusProgression(1, "SYMPTOMATIC");
        n1.addVirusProgression(5, "SEVERELY_SYMPTOMATIC");
        n1.addVirusProgression(8, "DEAD");

        n2.addVirusProgression(2, "EXPOSED");
        n2.addVirusProgression(3, "SYMPTOMATIC");
        n2.addVirusProgression(8, "SEVERELY_SYMPTOMATIC");
        n2.addVirusProgression(9, "RECOVERED");

        events.add(new InfectionEvent(1, null, 0, "", ""));
        events.add(new VirusEvent(0, 1, "SYMPTOMATIC", "EXPOSED"));
        events.add(new VirusEvent(0, 5, "SEVERELY_SYMPTOMATIC", "EXPOSED"));
        events.add(new VirusEvent(0, 8, "DEAD", "SEVERELY_SYMPTOMATIC"));

        events.add(new InfectionEvent(1, 0, 2.5, "", ""));//add 0.5 as these are off by 0.5
        events.add(new VirusEvent(1, 3, "SYMPTOMATIC", "EXPOSED"));
        events.add(new VirusEvent(1, 8, "SEVERELY_SYMPTOMATIC", "EXPOSED"));
        events.add(new VirusEvent(1, 9, "RECOVERED", "SEVERELY_SYMPTOMATIC"));

    }

    @After
    public void tearDown() {
        File f = new File(testFileLocation);
        f.delete();
    }

    /**
     * Test of writeMetaDataFile method, of class SimMetaDataWriter.
     */
    @Test
    public void testWriteMetaDataFile() throws Exception {
        System.out.println("writeMetaDataFile");
        SimMetaDataWriter instance = new SimMetaDataWriter(ig, events, contactsFromUser, policies);
        instance.writeMetaDataFile(testFileLocation);

        JsonReader reader = new JsonReader(new FileReader(testFileLocation));
        Gson gson = new Gson();
        PolicyCompartmentsJson[] jsonTreeList = gson.fromJson(reader, PolicyCompartmentsJson[].class);

        assertEquals(2, jsonTreeList.length);

        Policy policy0 = policies.get(0);
        PolicyCompartmentsJson policy0Compartments = getSpecificPolicyCompartmentJson(policy0, jsonTreeList);
        assertEquals(Compartment.values().length, policy0Compartments.nodesOverTimePerCompartment.size());

        //check suspecitble compartement compartement
        TreeMap<Double, Integer> compartmentArray = policy0Compartments.nodesOverTimePerCompartment.get(Compartment.SUSPECTIBLE);
        assertEquals(7, compartmentArray.size());
        assertEquals(2, (int) compartmentArray.get(0.0));
        assertEquals(2, (int) compartmentArray.get(1.0));
        assertEquals(1, (int) compartmentArray.get(2.0));
        assertEquals(1, (int) compartmentArray.get(3.0));
        assertEquals(1, (int) compartmentArray.get(5.0));
        assertEquals(1, (int) compartmentArray.get(8.0));
        assertEquals(1, (int) compartmentArray.get(9.0));

        compartmentArray = policy0Compartments.nodesOverTimePerCompartment.get(Compartment.EXPOSED);
        assertEquals(7, compartmentArray.size());
        assertEquals(1, (int) compartmentArray.get(0.0));
        assertEquals(0, (int) compartmentArray.get(1.0));
        assertEquals(1, (int) compartmentArray.get(2.0));
        assertEquals(0, (int) compartmentArray.get(3.0));
        assertEquals(0, (int) compartmentArray.get(5.0));
        assertEquals(0, (int) compartmentArray.get(8.0));
        assertEquals(0, (int) compartmentArray.get(9.0));

        compartmentArray = policy0Compartments.nodesOverTimePerCompartment.get(Compartment.SYMPTOMATIC);

        assertEquals(7, compartmentArray.size());
        assertEquals(0, (int) compartmentArray.get(0.0));
        assertEquals(1, (int) compartmentArray.get(1.0));
        assertEquals(1, (int) compartmentArray.get(2.0));
        assertEquals(2, (int) compartmentArray.get(3.0));
        assertEquals(1, (int) compartmentArray.get(5.0));
        assertEquals(0, (int) compartmentArray.get(8.0));
        assertEquals(0, (int) compartmentArray.get(9.0));

        compartmentArray = policy0Compartments.nodesOverTimePerCompartment.get(Compartment.SEVERELY_SYMPTOMATIC);
        assertEquals(7, compartmentArray.size());
        assertEquals(0, (int) compartmentArray.get(0.0));
        assertEquals(0, (int) compartmentArray.get(1.0));
        assertEquals(0, (int) compartmentArray.get(2.0));
        assertEquals(0, (int) compartmentArray.get(3.0));
        assertEquals(1, (int) compartmentArray.get(5.0));
        assertEquals(1, (int) compartmentArray.get(8.0));
        assertEquals(0, (int) compartmentArray.get(9.0));

        compartmentArray = policy0Compartments.nodesOverTimePerCompartment.get(Compartment.DEAD);
        assertEquals(7, compartmentArray.size());
        assertEquals(0, (int) compartmentArray.get(0.0));
        assertEquals(0, (int) compartmentArray.get(1.0));
        assertEquals(0, (int) compartmentArray.get(2.0));
        assertEquals(0, (int) compartmentArray.get(3.0));
        assertEquals(0, (int) compartmentArray.get(5.0));
        assertEquals(1, (int) compartmentArray.get(8.0));
        assertEquals(1, (int) compartmentArray.get(9.0));

        compartmentArray = policy0Compartments.nodesOverTimePerCompartment.get(Compartment.RECOVERED);
        assertEquals(7, compartmentArray.size());
        assertEquals(0, (int) compartmentArray.get(0.0));
        assertEquals(0, (int) compartmentArray.get(1.0));
        assertEquals(0, (int) compartmentArray.get(2.0));
        assertEquals(0, (int) compartmentArray.get(3.0));
        assertEquals(0, (int) compartmentArray.get(5.0));
        assertEquals(0, (int) compartmentArray.get(8.0));
        assertEquals(1, (int) compartmentArray.get(9.0));

        Policy policy1aA50 = policies.get(1);
        PolicyCompartmentsJson policy1aA50Compartments = getSpecificPolicyCompartmentJson(policy1aA50, jsonTreeList);
        assertEquals(Compartment.values().length, policy1aA50Compartments.nodesOverTimePerCompartment.size());

        //check suspecitble compartement compartement
        compartmentArray = policy1aA50Compartments.nodesOverTimePerCompartment.get(Compartment.SUSPECTIBLE);
        assertEquals(7, compartmentArray.size());
        assertEquals(2, (int) compartmentArray.get(0.0));
        assertEquals(2, (int) compartmentArray.get(1.0));
        assertEquals(2, (int) compartmentArray.get(2.0));
        assertEquals(2, (int) compartmentArray.get(3.0));
        assertEquals(2, (int) compartmentArray.get(5.0));
        assertEquals(2, (int) compartmentArray.get(8.0));
        assertEquals(2, (int) compartmentArray.get(9.0));

        //checke Symptomatic compartement
        compartmentArray = policy1aA50Compartments.nodesOverTimePerCompartment.get(Compartment.SYMPTOMATIC);
        assertEquals(7, compartmentArray.size());
        assertEquals(0, (int) compartmentArray.get(0.0));
        assertEquals(1, (int) compartmentArray.get(1.0));
        assertEquals(1, (int) compartmentArray.get(2.0));
        assertEquals(1, (int) compartmentArray.get(3.0));
        assertEquals(0, (int) compartmentArray.get(5.0));
        assertEquals(0, (int) compartmentArray.get(8.0));
        assertEquals(0, (int) compartmentArray.get(8.0));

        compartmentArray = policy1aA50Compartments.nodesOverTimePerCompartment.get(Compartment.SEVERELY_SYMPTOMATIC);
        assertEquals(7, compartmentArray.size());
        assertEquals(0, (int) compartmentArray.get(0.0));
        assertEquals(0, (int) compartmentArray.get(1.0));
        assertEquals(0, (int) compartmentArray.get(2.0));
        assertEquals(0, (int) compartmentArray.get(3.0));
        assertEquals(1, (int) compartmentArray.get(5.0));
        assertEquals(0, (int) compartmentArray.get(8.0));
        assertEquals(0, (int) compartmentArray.get(9.0));

        compartmentArray = policy1aA50Compartments.nodesOverTimePerCompartment.get(Compartment.RECOVERED);
        assertEquals(7, compartmentArray.size());
        assertEquals(0, (int) compartmentArray.get(0.0));
        assertEquals(0, (int) compartmentArray.get(1.0));
        assertEquals(0, (int) compartmentArray.get(2.0));
        assertEquals(0, (int) compartmentArray.get(3.0));
        assertEquals(0, (int) compartmentArray.get(5.0));
        assertEquals(0, (int) compartmentArray.get(8.0));
        assertEquals(0, (int) compartmentArray.get(9.0));

        compartmentArray = policy1aA50Compartments.nodesOverTimePerCompartment.get(Compartment.DEAD);
        assertEquals(7, compartmentArray.size());
        assertEquals(0, (int) compartmentArray.get(0.0));
        assertEquals(0, (int) compartmentArray.get(1.0));
        assertEquals(0, (int) compartmentArray.get(2.0));
        assertEquals(0, (int) compartmentArray.get(3.0));
        assertEquals(0, (int) compartmentArray.get(5.0));
        assertEquals(1, (int) compartmentArray.get(8.0));
        assertEquals(1, (int) compartmentArray.get(9.0));
    }

    private void addContact(int startId, int endId, double time, boolean infectionContact) {
        InfectionNode startNode = ig.getNode(startId);
        InfectionNode endNode = ig.getNode(endId);

        Contact c = new Contact(time, startId, endId, "");
        addToContactSet(c);
        //inverted contact occurs as well
        c = new Contact(time, endId, startId, "");
        addToContactSet(c);

        if (infectionContact) {
            InfectionEdge ie = new InfectionEdge(startNode, endNode, time);
            ig.addEdge(ie);
            endNode.exposedTime = time;
        }
    }

    private void addToContactSet(Contact c) {
        Set contactSet = contactsFromUser.getOrDefault(c.startNodeId, new HashSet());
        contactSet.add(c);
        contactsFromUser.put(c.startNodeId, contactSet);
    }

    private PolicyCompartmentsJson getSpecificPolicyCompartmentJson(Policy p, PolicyCompartmentsJson[] jsonTreeList) {
        for (PolicyCompartmentsJson pcj : jsonTreeList) {
            if (pcj.policyString.equals(p.getPolicyString())) {
                return pcj;
            }
        }
        return null;
    }
}
