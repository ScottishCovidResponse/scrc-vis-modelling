/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfectionTreeGenerator.Event;

import InfectionTreeGenerator.Graph.Infection.InfectionEdge;
import InfectionTreeGenerator.Graph.Infection.InfectionNode;
import java.util.List;

/**
 *
 * @author MaxSondag
 */
public class InfectionEvent extends Event {

    /**
     * Which node has infected this node
     */
    public final Integer sourceId;

    /**
     * In case of an initial infection, sourceId = null;
     */
    public InfectionEvent(int nodeId, Integer sourceId, double time, String newStatus, String additionalInfo) {
        super(nodeId, time == 0 ? 0 : time - 0.5, newStatus, additionalInfo);//time minus 0.5 (unless it is a root infection) to map to infectionmap 
        this.sourceId = sourceId;
    }

    @Override
    public void addMetaData(InfectionNode n) {
        if (time == 0) {
            n.addRootInfection(time, n.id);
        } else {
            //can be multiple infectionevents at the same timestep. Take the one that corresponds to the graph
            if (correspondsToInfectionMap(n)) {
                n.addVirusProgression(time, "EXPOSED");
                n.sourceInfectionId = sourceId;
            }
        }
    }

    /**
     * Returns whether this infectionevent corresponds to the graph for node N
     *
     * @param n
     * @return
     */
    private boolean correspondsToInfectionMap(InfectionNode n) {
        List<InfectionEdge> incomingEdges = n.getIncomingEdges();
        InfectionEdge e = incomingEdges.get(0);

        return e.source.id == sourceId && e.target.id == nodeId;
    }

}
