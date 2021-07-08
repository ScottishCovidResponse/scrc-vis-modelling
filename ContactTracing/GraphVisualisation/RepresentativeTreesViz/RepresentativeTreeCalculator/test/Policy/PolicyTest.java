/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Policy;

import Contact.Contact;
import InfectionTreeGenerator.Event.Event;
import InfectionTreeGenerator.Graph.Infection.InfectionEdge;
import InfectionTreeGenerator.Graph.Infection.InfectionGraph;
import InfectionTreeGenerator.Graph.Infection.InfectionNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Test;

/**
 *
 * @author MaxSondag
 */
public class PolicyTest {

    InfectionGraph g;
    List<Event> events;
    HashMap<Integer, Set<Contact>> contactsFromUser;
    HashMap<Integer, Double> randomAppPercentages;

    public void addContact(InfectionNode startNode, InfectionNode endNode, double time, boolean infectionContact) {
        Contact c = new Contact(time, startNode.id, endNode.id, "");
        addToContactSet(c);
        //inverted contact occurs as well
        c = new Contact(time, endNode.id, startNode.id, "");
        addToContactSet(c);

        if (infectionContact) {
            InfectionEdge ie = new InfectionEdge(startNode, endNode, time);
            g.addEdge(ie);
            endNode.exposedTime = time;
        }
    }

    private void addToContactSet(Contact c) {
        Set contactSet = contactsFromUser.getOrDefault(c.startNodeId, new HashSet());
        contactSet.add(c);
        contactsFromUser.put(c.startNodeId, contactSet);
    }

    @Test
    public void addPolicyDataTest() {
        //stub to make hierarchy work for now
    }

}
