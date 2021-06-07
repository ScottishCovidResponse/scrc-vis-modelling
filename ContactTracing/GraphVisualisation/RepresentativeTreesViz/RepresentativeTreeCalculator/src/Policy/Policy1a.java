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
 * Removes nodes for which the following rule holds: After a node n_1 become symptomatic at
 * time t , all adjacent nodes X will isolate for 14 days. If any
 * node x \in X was infected during these days, the infection chain starting
 * from x will be marked. If any node y was infected due to x during these days,
 * the chain starting from y will be marked.
 *
 * @author MaxSondag
 */
public class Policy1a extends DirectPolicy {

    public Policy1a(InfectionGraph ig, List<Event> events, HashMap<Integer, Set<Contact>> contactsFromUser, HashMap<Integer, Double> randomAppPercentages, double appPercentage) {
        super(ig, events, contactsFromUser, randomAppPercentages, appPercentage);
    }

    @Override
    protected TimeWindow getContactWindow(double symptomaticTime) {
        return new TimeWindow(symptomaticTime - 14, symptomaticTime);
    }

    @Override
    protected TimeWindow getIsolateWindow(double symptomaticTime, InfectionNode isolatedNode, InfectionNode symptomaticNode) {
        return new TimeWindow(symptomaticTime, symptomaticTime + 14);
    }

    @Override
    public String getPolicyString() {
        return "1aA" + (int) Math.round(appPercentage * 100);
    }
}
