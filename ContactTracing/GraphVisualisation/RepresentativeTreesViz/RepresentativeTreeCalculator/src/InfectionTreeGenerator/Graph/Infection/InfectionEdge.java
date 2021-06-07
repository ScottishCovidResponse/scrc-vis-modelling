/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfectionTreeGenerator.Graph.Infection;

import InfectionTreeGenerator.Graph.Edge;
import java.util.HashMap;

/**
 *
 * @author MaxSondag
 */
public class InfectionEdge extends Edge<InfectionNode> {

    public double exposedTime;

    public InfectionEdge(InfectionNode start, InfectionNode target, double exposedTime) {
        super(start, target);
        this.exposedTime = exposedTime;
    }

    @Override
    public String toString() {
        return "Edge{" + "start=" + source.id + ", end=" + target.id + ", time=" + exposedTime + '}';
    }

    @Override
    public String toJson() {
        return "{\"sourceId\":" + source.id + ","
                + "\"targetId\":" + target.id + ","
                + "\"exposedTime\":" + exposedTime
                + "}";
    }

    @Override
    public Edge deepCopy(HashMap<Integer, InfectionNode> newNodes) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }



}
