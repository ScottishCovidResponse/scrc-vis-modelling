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
import java.util.List;
import java.util.Set;

/**
 * Only the node itself will isolate for 14 days
 * @author MaxSondag
 */
public class Policy1x extends DirectPolicy {

    public Policy1x(InfectionGraph ig, List<Event> events, HashMap<Integer, Set<Contact>> contactsFromUser,HashMap<Integer, Double> randomAppPercentages) {
        super(ig, events, contactsFromUser,randomAppPercentages,1);//appPercentage does not matter
    }

    @Override
    protected TimeWindow getContactWindow(double symptomaticTime) {
        return new TimeWindow(-1, -1);
    }

    @Override
    protected TimeWindow getIsolateWindow(double symptomaticTime, InfectionNode isolatedNode, InfectionNode symptomaticNode) {
        return new TimeWindow(symptomaticTime, symptomaticTime + 14);
    }

    @Override
    public String getPolicyString() {
        return "1x";
    }
}
