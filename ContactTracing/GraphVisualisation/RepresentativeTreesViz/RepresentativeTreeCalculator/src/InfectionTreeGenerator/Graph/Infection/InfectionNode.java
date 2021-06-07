/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfectionTreeGenerator.Graph.Infection;

import InfectionTreeGenerator.Graph.Node;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author MaxSondag
 */
public class InfectionNode extends Node<InfectionEdge> {

    /**
     * Holds all policies under which this node is no longer infected.
     */
    public Set<String> policies = new HashSet();

    /**
     * Holds all alerts for this node sorted by ascending time
     */
    public TreeMap<Double, String> alerts = new TreeMap();//time,alertdescription
    /**
     * Holds the progression of the disease and the associated timesteps sorted
     * by ascending time
     */
    public TreeMap<Double, String> virusProgression = new TreeMap();//time,currentStatus

    /**
     * Holds the id of the node that has infected this node
     */
    public Integer sourceInfectionId = null;//the id of the node that has infected this node.

    /**
     * Holds when this node was exposed
     */
    public double exposedTime;

    public String infectionLocation;

    public int age;

    public InfectionNode(int id, double exposedTime) {
        super(id);
        this.exposedTime = exposedTime;
    }

    public void addAlert(double time, String alertMessage) {
        alerts.put(time, alertMessage);
    }

    public void addRootInfection(double exposedTime, int sourceNodeId) {
        this.exposedTime = exposedTime;
        this.sourceInfectionId = sourceNodeId;
        virusProgression.put(0.0, "Initial");
    }

    public void addVirusProgression(double time, String newState) {
        virusProgression.put(time, newState);
    }

    public void addPolicies(Set<String> policies) {
        this.policies.addAll(policies);
    }

    @Override
    public InfectionNode deepCopy() {
        InfectionNode n = new InfectionNode(id, exposedTime);
        n.alerts.putAll(alerts);
        n.virusProgression.putAll(virusProgression);
        n.sourceInfectionId = sourceInfectionId;
        return n;
    }

    public void addPolicy(String policyString) {
        policies.add(policyString);
    }

    /**
     * Returns the time this node becomes symptomatic or null if the node never
     * becomes symptomatic
     *
     * @return
     */
    public Double getSymptomaticTime() {
        for (Entry<Double, String> e : virusProgression.entrySet()) {
            if ("SYMPTOMATIC".equals(e.getValue())) {
                return e.getKey();
            }
        }
        return null;
    }

}
