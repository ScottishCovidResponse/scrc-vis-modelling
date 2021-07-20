/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Export;

import Policy.Policy;
import java.util.HashMap;
import java.util.TreeMap;

/**
 *
 * @author MaxSondag
 */
public class PolicyCompartmentsJson {

    public enum Compartment {
        SUSPECTIBLE,
        EXPOSED,
        ASYMPTOMATIC,
        PRESYMPTOMATIC,
        SYMPTOMATIC,
        SEVERELY_SYMPTOMATIC,
        DEAD,
        RECOVERED
    };

    /**
     * The policy that this object represents
     */
    public String policyString;

    /**
     * For each compartment, holds how many nodes are in this compartment at a
     * point in time. Using a treemap as times are not integers, and this is
     * easier to parse in time for the visualization
     */
    public HashMap<Compartment, TreeMap<Double, Integer>> nodesOverTimePerCompartment = new HashMap();

    public PolicyCompartmentsJson(Policy p) {
        this.policyString = p.getPolicyString();
    }

    public void addNodesOverTimeForPolicy(Compartment c, TreeMap<Double, Integer> nodesOverTime) {
        nodesOverTimePerCompartment.put(c, nodesOverTime);
    }
}
