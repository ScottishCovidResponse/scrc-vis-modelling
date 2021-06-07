/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Policy;

import Contact.Contact;
import InfectionTreeGenerator.Event.Event;
import InfectionTreeGenerator.Graph.Infection.InfectionGraph;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static java.util.stream.Collectors.toSet;

/**
 *
 * @author MaxSondag
 */
public abstract class IndirectPolicy extends Policy {

    /**
     * All direct contacts that can be reached via the app
     */
    Set<Integer> directReachableContactIds = new HashSet();

    public IndirectPolicy(InfectionGraph ig, List<Event> events, HashMap<Integer, Set<Contact>> contactsFromUser, HashMap<Integer, Double> randomAppPercentages, double appPercentage) {
        super(ig, events, contactsFromUser, randomAppPercentages, appPercentage);
    }

    @Override
    protected Set<Integer> getIsolatorIds(int nodeId, TimeWindow contactWindow) {
        Set<Integer> isolatorIds = new HashSet();
        isolatorIds.add(nodeId);
        if (!hasTracingApp(nodeId)) {//root has no tracing app.
            return isolatorIds;
        }

        //get all contacts from nodeId in the window
        Set<Integer> directContacts = getContactsIdsFromNodeIdInWindow(nodeId, contactWindow);
        for (Integer directContactId : directContacts) {
            //contacts needs to have the app as well
            if (hasTracingApp(directContactId)) {
                isolatorIds.add(directContactId);
                directReachableContactIds.add(directContactId);

                //go through the secondary contacts and
                Set<Integer> secondaryContacts = getContactsIdsFromNodeIdInWindow(directContactId, contactWindow);
                for (Integer secondaryContactid : secondaryContacts) {
                    if (hasTracingApp(secondaryContactid)) {
                        isolatorIds.add(secondaryContactid);
                    }
                }
            }
        }
        return isolatorIds;
    }

}
