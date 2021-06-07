/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfectionTreeGenerator.Event;

import InfectionTreeGenerator.Graph.Infection.InfectionNode;

/**
 *
 * @author MaxSondag
 */
public abstract class Event {

    public final int nodeId;
    public final double time;
    public final String newStatus;
    public final String additionalInfo;

    public Event(int nodeId, double time, String newStatus, String additionalInfo) {
        this.nodeId = nodeId;
        this.time = time;
        this.newStatus = newStatus.replaceAll("\"", "");//remove " from the string.
        this.additionalInfo = additionalInfo.replaceAll("\"", "");//remove " from the string.
    }

    public abstract void addMetaData(InfectionNode n);

    public double getTime() {
        return time;
    }
}
