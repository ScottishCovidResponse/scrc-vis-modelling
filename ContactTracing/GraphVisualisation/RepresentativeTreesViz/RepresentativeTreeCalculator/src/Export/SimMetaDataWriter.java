/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Export;

import Contact.Contact;
import Export.PolicyCompartmentsJson.Compartment;
import Policy.Policy;
import Utility.Log;
import com.google.gson.Gson;
import InfectionTreeGenerator.Event.AlertEvent;
import InfectionTreeGenerator.Event.Event;
import InfectionTreeGenerator.Graph.Infection.InfectionGraph;
import InfectionTreeGenerator.Graph.Infection.InfectionNode;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 *
 * @author MaxSondag
 */
public class SimMetaDataWriter {

    private Set<PolicyCompartmentsJson> compartmentData = new HashSet();

    public SimMetaDataWriter(InfectionGraph ig, List<Event> events, HashMap<Integer, Set<Contact>> contacts, List<Policy> policies) {
        calculateCompartmentsOverTime(ig, events, contacts, policies);
    }

    private void calculateCompartmentsOverTime(InfectionGraph ig, List<Event> events, HashMap<Integer, Set<Contact>> contacts, List<Policy> policies) {
        Set<Integer> allNodeIds = getAllNodeIds(contacts);

        TreeSet<Double> timeSteps = getSortedTimeSteps(events);
        Compartment[] compartments = Compartment.values();

        for (Policy p : policies) {
            PolicyCompartmentsJson cJson = new PolicyCompartmentsJson(p);
            for (Compartment c : compartments) {
                TreeMap<Double, Integer> nodesOverTime = getNodesOverTime(allNodeIds, timeSteps, ig, c, p);
                cJson.addNodesOverTimeForPolicy(c, nodesOverTime);
            }
            compartmentData.add(cJson);
        }
    }

    public void writeMetaDataFile(String outputFileLocation) throws IOException {
        Gson gson = new Gson();

        FileWriter fw = new FileWriter(outputFileLocation);
        gson.toJson(compartmentData, fw);
        fw.flush();
        fw.close();
    }

    private TreeMap<Double, Integer> getNodesOverTime(Set<Integer> allNodeIds, TreeSet<Double> timeSteps, InfectionGraph ig, Compartment c, Policy p) {
        TreeMap<Double, Integer> nodesOverTime = new TreeMap();

        for (double time : timeSteps) {
            //get the nodeCount without a policy
            int nodeCount = getNodesInCompartement(allNodeIds, ig, time, c, p);
            nodesOverTime.put(time, nodeCount);
        }
        return nodesOverTime;
    }

    /**
     * Returns how many nodes are in compartement {@code c} at time {@code t}
     *
     * @param allNodeIds
     * @param ig
     * @param time
     * @param c
     * @param p null if policy0
     * @return
     */
    private int getNodesInCompartement(Set<Integer> allNodeIds, InfectionGraph ig, double time, Compartment c, Policy p) {
        int count = 0;
        for (Integer nodeId : allNodeIds) {
            InfectionNode in = ig.getNode(nodeId);
            if (in == null) {//not in the infectiongraph, thuse never exposed
                if (c == Compartment.SUSPECTIBLE) {
                    count++;
                }
            } else {
                Compartment nodeCompartement = getNodeCompartement(in, time);
                if (in.policies.contains(p.getPolicyString())) {
                    if (c == Compartment.SUSPECTIBLE) {//prevented by policy, so none of the other state can happen
                        count++;
                    }
                } else {
                    if (nodeCompartement == c) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    /**
     * Returns the compartment of node {@code n} at time {@code time}
     *
     * @param n
     * @param time
     * @return
     */
    private Compartment getNodeCompartement(InfectionNode n, double time) {
        Map.Entry<Double, String> lastProgression = n.virusProgression.floorEntry(time);
        if (lastProgression == null) {
            return Compartment.SUSPECTIBLE;
        } else {
            String value = lastProgression.getValue();
            if (value == "Initial") {
                value = "EXPOSED";
            }
            return Compartment.valueOf(value);
        }
    }

    private TreeSet<Double> getSortedTimeSteps(List<Event> events) {
        TreeSet<Double> times = new TreeSet();
        for (Event e : events) {
            times.add(e.time);
        }
        return times;
    }

    /**
     * Returns the total amount of nodes in the simulation
     *
     * @param contacts
     * @return
     */
    private int getTotalAmountOfNodes(HashMap<Integer, Set<Contact>> contacts) {
        Set<Integer> nodeIds = new HashSet();
        for (Set<Contact> cSet : contacts.values()) {
            for (Contact c : cSet) {
                //add the nodeIds to the set. It's a set,so we will end with all unique id's.
                nodeIds.add(c.startNodeId);
                nodeIds.add(c.endNodeId);
            }
        }
        return nodeIds.size();
    }

    /**
     * Returns the amount of positive tests at time {@code time}
     *
     * @param events
     * @param time
     * @return
     */
    private int getPositiveTests(List<Event> events, double time) {
        List<AlertEvent> positiveTests = events.stream()
                .filter(e -> eventAtTime(e, time))//correct time
                .filter(e -> isAlertEvent(e))//alert events
                .map(e -> (AlertEvent) e)//cast
                .filter(ae -> ae.newStatus.equals("TESTED_POSITIVE"))//positive test
                .collect(Collectors.toList());
        return positiveTests.size();
    }

    /**
     * Returns the amount of negative tests at time {@code time}
     *
     * @param events
     * @param time
     * @return
     */
    private int getNegativeTests(List<Event> events, double time) {
        List<AlertEvent> positiveTests = events.stream()
                .filter(e -> eventAtTime(e, time))//correct time
                .filter(e -> isAlertEvent(e))//alert events
                .map(e -> (AlertEvent) e)//cast
                .filter(ae -> ae.newStatus.equals("TESTED_NEGATIVE"))//NEGATIVE TEST test
                .collect(Collectors.toList());
        return positiveTests.size();
    }

    private Set<Integer> getAllNodeIds(HashMap<Integer, Set<Contact>> contacts) {
        return contacts.keySet();
    }

    private boolean eventAtTime(Event e, double time) {
        return e.time == time;
    }

    private boolean isAlertEvent(Event e) {
        return e instanceof AlertEvent;
    }

}
