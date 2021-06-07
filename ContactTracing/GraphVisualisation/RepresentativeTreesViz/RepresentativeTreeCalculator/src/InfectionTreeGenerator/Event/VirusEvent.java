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
public class VirusEvent extends Event {

    public String oldStatus;

    public VirusEvent(int nodeId, double time, String newStatus, String additionalInfo) {
        super(nodeId, time, newStatus, additionalInfo);
        oldStatus = additionalInfo.substring(additionalInfo.indexOf(": ") + 2);

    }

    @Override
    public void addMetaData(InfectionNode n) {
        n.addVirusProgression(time, newStatus);
    }

}
