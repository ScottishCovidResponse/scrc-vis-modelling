/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfectionTreeGenerator.Graph.GraphAlgorithms.RepresentativeTree;

import Utility.Log;
import InfectionTreeGenerator.Graph.Node;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author MaxSondag
 */
public class RepresentativeNode extends Node<RepresentativeEdge> {

    /**
     * Holds the edit distances at which point additional nodes are represented
     * by this node
     */
    private HashMap<Integer, List<Node>> representsNodes = new HashMap();

    public RepresentativeNode(int id) {
        super(id);
    }

    private int maxEditDistance;

    public void addMaximum(int maxEditDistance) {
        Log.printOnce("Hacky hack for gathering data. No need for maxEditDistance in every node (probably)");
        this.maxEditDistance = maxEditDistance;
    }

    @Override
    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"id\":" + id + ",");
        sb.append("\"maxEditDistance\":" + maxEditDistance + ","); //TODO: REMOVE
        sb.append("\"representation\":{");

        int maxDistance = getMaxEditDistance();
        for (int i = 0; i <= maxDistance; i++) {
            if (representsNodes.containsKey(i)) {//only print if there is new information
                sb.append("\"" + i + "\":");
                sb.append("[");
                for (Node n : representsNodes.get(i)) {
                    sb.append(n.toJson());
                    sb.append(",");
                }

                if (sb.charAt(sb.length() - 1) == ',') {
                    sb.setLength(sb.length() - 1);//remove last comma
                }
                sb.append("],");
            }
        }
        if (sb.charAt(sb.length() - 1) == ',') {
            sb.setLength(sb.length() - 1);//remove last comma
        }
        sb.append("}");
        sb.append("}");
        return sb.toString();
    }

    private int getMaxEditDistance() {
        int maxDistance = 0;
        for (Integer distance : representsNodes.keySet()) {
            maxDistance = Math.max(maxDistance, distance);
        }
        return maxDistance;
    }

    @Override
    public Node deepCopy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public HashMap<Integer, List<Node>> getRepresentNodesMapping() {
        return representsNodes;
    }

    public void addToRepresentsNodes(int editDistance, Node newN) {
        List<Node> representNodes = getRepresentNodes(editDistance);
        representNodes.add(newN);
        representsNodes.put(editDistance, representNodes);
    }

    /**
     * Returns the nodes this node additionally represents at editdistance
     * @param editDistance
     * @return 
     */
    public List<Node> getRepresentNodes(int editDistance) {
        return representsNodes.getOrDefault(editDistance, new ArrayList());
    }


}
