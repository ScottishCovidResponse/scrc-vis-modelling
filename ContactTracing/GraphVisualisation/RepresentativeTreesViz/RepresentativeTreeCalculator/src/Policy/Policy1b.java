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
 *
 * @author MaxSondag
 */
public class Policy1b extends Policy1c {

    public Policy1b(InfectionGraph ig, List<Event> events, HashMap<Integer, Set<Contact>> contactsFromUser, HashMap<Integer, Double> randomAppPercentages, double appPercentage) {
        super(ig, events, contactsFromUser, randomAppPercentages, appPercentage, 14, 14);
    }

    @Override
    public String getPolicyString() {
        int appI = (int) Math.round(appPercentage * 100);
        return "1bA" + appI;
    }

}
