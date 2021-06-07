/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Policy;

import Contact.Contact;
import Utility.Log;
import InfectionTreeGenerator.Event.Event;
import InfectionTreeGenerator.Graph.Infection.InfectionGraph;
import InfectionTreeGenerator.Graph.Infection.InfectionNode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static java.util.stream.Collectors.toSet;

/**
 *
 * @author MaxSondag
 */
public abstract class DirectPolicy extends Policy {

    public DirectPolicy(InfectionGraph ig, List<Event> events, HashMap<Integer, Set<Contact>> contactsFromUser, HashMap<Integer, Double> randomAppPercentages, double appPercentage) {
        super(ig, events, contactsFromUser, randomAppPercentages, appPercentage);
    }

    protected Set<Integer> getIsolatorIds(int nodeId, TimeWindow contactWindow) {
        Set<Integer> isolatorIds = new HashSet();
        isolatorIds.add(nodeId);
        if (!hasTracingApp(nodeId)) {//root has no tracing app.
            return isolatorIds;
        }
        
        //get all contacts from nodeId in the window
        Set<Integer> contactIds = getContactsIdsFromNodeIdInWindow(nodeId, contactWindow);
        for (Integer contactId : contactIds) {
            //contacts needs to have the app as well
            if (hasTracingApp(contactId)) {
                isolatorIds.add(contactId);
            }
        }
        return isolatorIds;
    }

}
