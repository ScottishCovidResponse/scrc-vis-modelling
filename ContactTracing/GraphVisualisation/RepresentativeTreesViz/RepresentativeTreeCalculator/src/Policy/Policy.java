/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Policy;

import Contact.Contact;
import Utility.Log;
import InfectionTreeGenerator.Event.AlertEvent;
import InfectionTreeGenerator.Event.Event;
import InfectionTreeGenerator.Event.InfectionEvent;
import InfectionTreeGenerator.Graph.Infection.InfectionEdge;
import InfectionTreeGenerator.Graph.Infection.InfectionGraph;
import InfectionTreeGenerator.Graph.Infection.InfectionNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static java.util.stream.Collectors.toSet;

/**
 *
 * @author MaxSondag
 */
public abstract class Policy {

    InfectionGraph ig;
    List<Event> events;
    HashMap<Integer, Set<Contact>> contactsFromUser;
    HashMap<Integer, Double> randomAppPercentages;
    double appPercentage;

    public Policy(InfectionGraph ig, List<Event> events, HashMap<Integer, Set<Contact>> contactsFromUser, HashMap<Integer, Double> randomAppPercentages, double appPercentage) {
        this.ig = ig;
        this.events = events;
        this.contactsFromUser = contactsFromUser;
        this.randomAppPercentages = randomAppPercentages;
        assert (appPercentage >= 0 && appPercentage <= 1);
        this.appPercentage = appPercentage;
    }

    /**
     * Adds the policy information to each node in the graph that this policy
     * affects.
     */
    public void addPolicyData() {

        Collection<InfectionNode> nodes = ig.getNodes();
        for (InfectionNode symptomaticNode : nodes) {
            //nodes isolate once they become symptomatic
            Double symptomaticTime = symptomaticNode.getSymptomaticTime();
            if (symptomaticTime == null) {
                continue;
            }
            int symptomaticNodeId = symptomaticNode.id;

            TimeWindow contactWindow = getContactWindow(symptomaticTime);

            //get all the contacts of the symptomaticNode that will be isolating 
            Set<Integer> isolatorIds = getIsolatorIds(symptomaticNodeId, contactWindow);

            //go through all nodes that are isolating, and if they would cause any infection (including themselves in their isolation time), mark their infection chain
            for (Integer isolateId : isolatorIds) {
                if (!ig.hasNodeWithId(isolateId)) {//node never becomes infected, so isolation has no effect on the graph
                    continue;
                }

                InfectionNode isolatedNode = ig.getNode(isolateId);
                TimeWindow isolateWindow = getIsolateWindow(symptomaticTime, isolatedNode, symptomaticNode);

                //if the node itself was infected in the time window if it was not isolating, mark it and it's chain
                if (infectedInTimeWindow(isolateWindow, isolatedNode)) {
                    markInfectionChain(isolatedNode, getPolicyString());
                    continue;
                }

                //get the nodes infected by the isolating node in its isolationWindow.
                List<InfectionNode> directInfections = getDirectInfectionsInTimeWindow(isolateWindow, isolatedNode);
                for (InfectionNode n : directInfections) {
                    markInfectionChain(n, getPolicyString());
                }
            }
        }
    }

    /**
     * Returns all alert events that involve a positive test.
     *
     * @param events
     * @return
     */
    protected List<AlertEvent> getPositiveAlerts() {
        List<AlertEvent> positiveAlerts = new ArrayList();
        for (Event e : events) {
            if (e.getClass() == AlertEvent.class) {
                AlertEvent ae = (AlertEvent) e;
                if ("TESTED_POSITIVE".equals(ae.newStatus)) {
                    positiveAlerts.add(ae);
                }

            }
        }
        return positiveAlerts;
    }

    /**
     * Chainroot is prevented by this policy, mark the entire chain as not being
     * infected in this infectionMap. Chaintroot is additionaly marked as the
     * origin of the prevention
     *
     * @param chainRoot
     * @param policyString
     */
    protected void markInfectionChain(InfectionNode chainRoot, String policyString) {
        Collection<InfectionNode> reachableNodes = ig.getReachableNodes(chainRoot);
        //root is marked as an origin of the policy.
        reachableNodes.remove(chainRoot);
        chainRoot.addPolicy(policyString + "Origin");

        for (InfectionNode rN : reachableNodes) {
            rN.addPolicy(policyString);
        }
    }

    /**
     * Returns whether exposedTime is within the timeWindow
     *
     *
     * @param exposedTime
     * @return
     */
    protected boolean infectedInTimeWindow(TimeWindow timeWindow, InfectionNode n) {
        return timeWindow.withinWindow(n.exposedTime);
    }

    /**
     * Returns all nodes that are directly infected by {@code node} in the time
     * window
     *
     * @return
     */
    protected List<InfectionNode> getDirectInfectionsInTimeWindow(TimeWindow timeWindow, InfectionNode node) {
        List<InfectionNode> infectedNodes = new ArrayList();

        List<InfectionEdge> outgoingEdges = node.getOutgoingEdges();
        for (InfectionEdge e : outgoingEdges) {
            InfectionNode n = e.target;
            if (infectedInTimeWindow(timeWindow, n)) {
                infectedNodes.add(n);
            }
        }
        return infectedNodes;
    }

    protected Set<Integer> getContactsIdsFromNodeIdInWindow(int nodeId, Policy.TimeWindow contactWindow) {
        Set<Contact> contacts = contactsFromUser.get(nodeId);
        Set<Integer> contactIds = contacts.stream()
                .filter(c -> contactWindow.withinWindow(c.time))//keep only those within the window
                .map(c -> c.endNodeId)//get ids
                .collect(toSet());//return set
        return contactIds;
    }

    protected Set<Contact> getContactsFromNodeIdInWindow(int nodeId, Policy.TimeWindow contactWindow) {
        Set<Contact> contacts = contactsFromUser.get(nodeId);
        Set<Contact> filteredContacts = contacts.stream()
                .filter(c -> contactWindow.withinWindow(c.time))//keep only those within the window
                .collect(toSet());//return set
        return filteredContacts;
    }

    protected boolean hasTracingApp(Integer id) {
        double p1 = randomAppPercentages.get(id);
        return p1 < appPercentage;
    }

    protected boolean isInfectedNode(Integer id) {
        return ig.hasNodeWithId(id);
    }

    /**
     * Returns all nodeIds that need to be isolated by the policy. Includes node
     * not in the infectiongraph
     *
     * @param symptomaticNodeId
     * @param contactWindow
     * @return
     */
    protected abstract Set<Integer> getIsolatorIds(int symptomaticNodeId, TimeWindow contactWindow);

    /**
     * Returns the timewindow for which contacts should isolate
     *
     * @param sypmtomaticTime
     * @return
     */
    protected abstract TimeWindow getContactWindow(double sypmtomaticTime);

    /**
     * Returns the timewindow for which {@code isolatedNode} should isolate
     *
     * @param alertTime
     * @param isolatedNode
     * @return
     */
    protected abstract TimeWindow getIsolateWindow(double sypmtomaticTime, InfectionNode isolatedNode, InfectionNode symptomaticNode);

    public abstract String getPolicyString();

    protected class TimeWindow {

        protected double startTime;
        protected double endTime;

        public TimeWindow(double startTime, double endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
        }

        protected boolean withinWindow(double time) {
            return startTime <= time && time < endTime;
        }

    }
}
