/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfectionTreeGenerator.Graph;

import java.util.ArrayList;
import java.util.List;

/**
 * A node is uniquely identified by {@code id}.
 *
 * @author MaxSondag
 */
public class Node<E extends Edge> {

    public final int id;
    //Transient removes the edges this node is involved in for serialization. Required for JSON as otherwise there is an infite loop
    public transient List<E> edges = new ArrayList();//Edges involving this node

    /**
     * Unit weight by default
     */
    public double weight = 1.0;

    public Node(int id) {
        this.id = id;
    }

    public Node(int id, double weight) {
        this.id = id;
        this.weight = weight;
    }

    public String toJson() {
        return "{\"id\":" + id
                + "}";
    }

    public void addEdge(E e) {
        assert (e != null);
        assert (!edges.contains(e));
        edges.add(e);
    }

    public void removeEdge(E e) {
        assert (e != null);
        assert (edges.contains(e));
        edges.remove(e);
    }

    public List<E> getIncomingEdges() {
        List<E> incomingEdges = new ArrayList();
        for (E e : edges) {
            if (e.target == this) {
                incomingEdges.add(e);
            }
        }
        return incomingEdges;
    }

    public List<E> getOutgoingEdges() {
        List<E> outgoingEdges = new ArrayList();
        for (E e : edges) {
            if (e.target != this) {
                outgoingEdges.add(e);
            }
        }
        return outgoingEdges;
    }

    /**
     * Returns a deepcopy of the properties of the node. Id will be the same,
     * edges will be empty
     *
     * @return
     */
    public Node deepCopy() {
        Node n = new Node(id, weight);
        return n;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + this.id;
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
        final Node<?> other = (Node<?>) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

}
