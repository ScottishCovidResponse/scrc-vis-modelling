/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Policy;

import Contact.Contact;
import InfectionTreeGenerator.Event.Event;
import InfectionTreeGenerator.Graph.Infection.InfectionGraph;
import InfectionTreeGenerator.Graph.Infection.InfectionNode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author MaxSondag
 */
public class Policy0 extends Policy {

    public Policy0(InfectionGraph ig, List<Event> events, HashMap<Integer, Set<Contact>> contactsFromUser, HashMap<Integer, Double> randomAppPercentages) {
        super(ig, events, contactsFromUser, randomAppPercentages,0);
    }

    @Override
    protected Set<Integer> getIsolatorIds(int nodeId, TimeWindow contactWindow) {
        return new HashSet();
    }

    @Override
    protected TimeWindow getContactWindow(double symptomaticTime) {
        return new TimeWindow(0, 0);
    }

    @Override
    protected TimeWindow getIsolateWindow(double symptomaticTime, InfectionNode isolatedNode, InfectionNode symptomaticNode) {
        return new TimeWindow(0, 0);
    }

    @Override
    public String getPolicyString() {
        return "";
    }

}
