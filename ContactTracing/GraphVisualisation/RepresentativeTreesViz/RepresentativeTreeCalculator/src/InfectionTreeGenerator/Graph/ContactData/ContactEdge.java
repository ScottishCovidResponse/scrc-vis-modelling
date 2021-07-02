/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfectionTreeGenerator.Graph.ContactData;

import InfectionTreeGenerator.Graph.Edge;
import InfectionTreeGenerator.Graph.Node;
import java.util.HashMap;

/**
 *
 * @author MaxSondag
 */
public class ContactEdge extends Edge<Node> {

    public double contactTime;

    public ContactEdge(Node start, Node target, double contactTime, int infectionChanceWeight) {
        super(start, target);
        this.contactTime = contactTime;
        this.weight = infectionChanceWeight;
    }

    @Override
    public String toString() {
        return "Edge{" + "start=" + source.id + ", end=" + target.id + ", time=" + contactTime + ", weight=" + weight + "}";
    }

    @Override
    public String toJson() {
        return "{\"sourceId\":" + source.id + ","
                + "\"targetId\":" + target.id + ","
                + "\"contactTime\":" + contactTime + ","
                + "\"weight\":" + weight
                + "}";
    }

    @Override
    public Edge deepCopy(HashMap<Integer, Node> newNodes) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
