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
public class AlertEvent extends Event {

    public AlertEvent(int nodeId, double time, String newStatus, String additionalInfo) {
        super(nodeId, time, newStatus, additionalInfo);
    }

    @Override
    public void addMetaData(InfectionNode in) {
        in.addAlert(time,newStatus);
    }

}
