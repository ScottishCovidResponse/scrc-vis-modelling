/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfectionTreeGenerator.Graph.Infection;

import Utility.Log;
import InfectionTreeGenerator.Event.Event;
import InfectionTreeGenerator.Graph.Graph;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author MaxSondag
 */
public class InfectionGraph extends Graph<InfectionNode, InfectionEdge> {

    public void addEventData(List<Event> events) {
        for (Event e : events) {
            InfectionNode n = getNode(e.nodeId);
            if (n != null) {
                e.addMetaData(n);
            }
        }

        for (InfectionNode n : getNodes()) {
            if (n.sourceInfectionId == null) {
                Log.printOnce("No sourceNode for node with id: " + n.sourceInfectionId);
            }
        }
    }

    @Override
    public InfectionNode getNode(int id) {
        //Override for convinience of type
        return (InfectionNode) nodeMapping.get(id);
    }

    @Override
    public Collection<InfectionNode> getNodes() {
        //Override for convinience of type
        return nodeMapping.values();
    }

    @Override
    public Collection<InfectionEdge> getEdges() {
        //Override for convinience of type
        return edgeMapping.values();
    }

    public InfectionGraph deepCopy() {
        InfectionGraph newG = new InfectionGraph();
        newG.id = id;

        for (InfectionNode n : getNodes()) {
            InfectionNode newN = new InfectionNode(n.id, n.exposedTime);
            newG.addNode(newN);
        }
        for (InfectionEdge e : getEdges()) {
            InfectionEdge newE = new InfectionEdge(newG.getNode(e.source.id), newG.getNode(e.target.id), e.exposedTime);
            newG.addEdge(newE);
        }
        return newG;
    }

}
