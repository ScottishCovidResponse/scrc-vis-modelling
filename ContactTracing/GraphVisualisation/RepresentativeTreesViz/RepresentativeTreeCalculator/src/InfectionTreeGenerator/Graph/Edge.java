/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfectionTreeGenerator.Graph;

import java.util.HashMap;

/**
 * An edge is uniquely identified by {@code source.id} and {@code target.id}
 * @author MaxSondag
 */
public class Edge<N extends Node> {

    final public N source, target;
    /**
     * Weight of this edge, unit weight by default
     */
    public double weight = 1.0;//unit weight by default

    public Edge(N source, N target) {
        this.source = source;
        this.target = target;
    }

    public Edge(N source, N target, double weight) {
        this.source = source;
        this.target = target;
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "Edge{" + "start=" + source.id + ", end=" + target.id + '}';
    }

    public String toJson() {
        return "{\"source\":" + source.id + ","
                + "\"target\":" + target.id
                + "}";
    }

    /**
     * Returns a deepcopy of this node. Nodes assigned to source and target will
     * be taken from newNodes with the same id
     *
     * @param nodeMapping
     * @return
     */
    public Edge deepCopy(HashMap<Integer, N> nodeMapping) {
        Node newSource = nodeMapping.get(source.id);
        Node newTarget = nodeMapping.get(target.id);
        Edge e = new Edge(newSource, newTarget, weight);
        return e;
    }

    /**
     * Returns a deepcopy of this node. Nodes assigned to source and target will
     * be n1Copy and n2Copy
     *
     * @param nodeMapping
     * @return
     */
    public Edge deepCopy(Node sourceCopy, Node targetCopy) {
        assert (sourceCopy.id == source.id);
        assert (targetCopy.id == target.id);

        Node newSource = sourceCopy;
        Node newTarget = targetCopy;
        Edge e = new Edge(newSource, newTarget, weight);
        return e;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + this.target.id;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Edge<?> other = (Edge<?>) obj;
        if (this.source.id != other.source.id) {
            return false;
        }
        if (this.target.id != other.target.id) {
            return false;
        }
        return true;
    }

    
    
}
