/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfectionTreeGenerator.Graph.GraphAlgorithms.RepresentativeTree;

import Utility.Log;
import InfectionTreeGenerator.Graph.Edge;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author MaxSondag
 */
public class RepresentativeEdge extends Edge<RepresentativeNode> {

    /**
     * Holds the edit distances at which point additional edges are represented
     * by this edge
     */
    private HashMap<Integer, List<Edge>> representsEdges = new HashMap();

    public RepresentativeEdge(RepresentativeNode source, RepresentativeNode target) {
        super(source, target);
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
        sb.append("\"source\":" + source.id + ",");
        sb.append("\"target\":" + target.id + ",");
        sb.append("\"maxEditDistance\":" + maxEditDistance + ","); //TODO: REMOVE
        sb.append("\"representation\":{");
        int maxDistance = getMaximumDistance();
        for (int i = 0; i <= maxDistance; i++) {
            if (representsEdges.containsKey(i)) {
                sb.append("\"" + i + "\":");
                sb.append("[");
                for (Edge e : representsEdges.get(i)) {
                    sb.append(e.toJson());
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

    private int getMaximumDistance() {
        int maxDistance = 0;
        for (Integer distance : representsEdges.keySet()) {
            maxDistance = Math.max(maxDistance, distance);
        }
        return maxDistance;
    }

    @Override
    public Edge deepCopy(HashMap<Integer, RepresentativeNode> newNodes) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<Edge> getRepresentEdges(int distance) {
        return representsEdges.getOrDefault(distance, new ArrayList());
    }

    public void addToRepresentsEdges(int editDistance, Edge newEdge) {
        List<Edge> representEdges = getRepresentEdges(editDistance);
        representEdges.add(newEdge);
        representsEdges.put(editDistance, representEdges);
    }
}
