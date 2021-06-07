package Policy;

import Contact.Contact;
import Utility.Randomizer;
import InfectionTreeGenerator.Event.Event;
import InfectionTreeGenerator.Graph.Infection.InfectionGraph;
import InfectionTreeGenerator.Graph.Infection.InfectionNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author MaxSondag
 */
public class PolicySimulator {

    /**
     * Calculated infectiongraph
     */
    private InfectionGraph ig;
    /**
     * Lists of policies we want to simulate
     */
    private List<Policy> policies;
    /**
     * All events occuring in the graph
     */
    private List<Event> events;
    /**
     * Map holding for all nodes which contacts they have had. Includes nodes
     * not in the graph.
     */
    private HashMap<Integer, Set<Contact>> contactsFromUser;

    /**
     * Random double for each node by id that indicates from which percentage
     * upwards they have the contact tracing app
     */
    private HashMap<Integer, Double> randomAppPercentages = new HashMap();

    public PolicySimulator(InfectionGraph ig, List<Event> events, HashMap<Integer, Set<Contact>> contactsFromUser) {
        this.ig = ig;
        this.events = events;
        this.contactsFromUser = contactsFromUser;

        //add app percentages
        Set<Integer> keySet = contactsFromUser.keySet();
        //sort the set to ensure determinist behaviour
        TreeSet<Integer> sortedKeySet = new TreeSet(keySet);

        for (Integer id : sortedKeySet) {
            double randomPercentage = Randomizer.getRandomDouble();
            randomAppPercentages.put(id, randomPercentage);
        }

        //add all implemented policies
        this.policies = new ArrayList();
        for (double appPercentage = 0; appPercentage < 1; appPercentage += 0.1) {//Increments of 10% for the appPercentage
            policies.add(new Policy1a(ig, events, contactsFromUser, randomAppPercentages, appPercentage));
            policies.add(new Policy1b(ig, events, contactsFromUser, randomAppPercentages, appPercentage));
            policies.add(new Policy1x(ig, events, contactsFromUser, randomAppPercentages));

            //original 5363kb ~5MB
            //with 14*14 extra 
            Double[] contactOptionsArray = {1.0, 3.0, 7.0, 14.0};
            Double[] isolationOptionsArray = {3.0, 7.0, 14.0};

            for (double contactTime : contactOptionsArray) {
                for (double isolationTime : isolationOptionsArray) {
                    policies.add(new Policy1c(ig, events, contactsFromUser, randomAppPercentages, appPercentage, contactTime, isolationTime));
                }
            }
        }

    }

    public void applyAllPolicies() {
        for (Policy p : policies) {
            p.addPolicyData();
        }
    }

    public List<Policy> getAppliedPolicies() {
        return policies;
    }

}
