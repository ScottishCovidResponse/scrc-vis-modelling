/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfectionTreeGenerator.Graph;

import InfectionTreeGenerator.Graph.Infection.InfectionEdge;
import InfectionTreeGenerator.Graph.Infection.InfectionNode;
import Utility.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * By default the graph is directed
 *
 * @author MaxSondag
 */
public class Graph<N extends Node, E extends Edge> {

    /**
     * Not automatically set
     */
    public int id;

    /**
     * Maps id's to nodes
     */
    protected HashMap<Integer, N> nodeMapping = new HashMap();
    /**
     * Maps the id's of the nodes of the edge to an edge
     */
    protected HashMap<Pair<Integer, Integer>, E> edgeMapping = new HashMap();

    public void addNodes(N... nodesToAdd) {
        for (N n : nodesToAdd) {
            addNode(n);
        }
    }

    public void addNode(N n) {
        assert (!nodeMapping.containsKey(n.id));
        nodeMapping.put(n.id, n);
    }

    public void addNodesIfNotPresent(N... nodesToAdd) {
        for (N n : nodesToAdd) {
            addNodeIfNotpresent(n);
        }
    }

    /**
     * Adds the node if there is no node with the same id already present
     *
     * @param n
     */
    public void addNodeIfNotpresent(N n) {
        if (!nodeMapping.containsKey(n.id)) {
            nodeMapping.put(n.id, n);
        }
    }

    public void addEdges(E... edgesToAdd) {
        for (E e : edgesToAdd) {
            addEdge(e);
        }
    }

    /**
     * Adds the edge to the graph and updates the nodes to reference this edge.
     *
     * @param e
     */
    public void addEdge(E e) {
        Pair pair = new Pair(e.source.id, e.target.id);
        assert (!edgeMapping.containsKey(pair));
        edgeMapping.put(pair, e);

        e.source.addEdge(e);
        e.target.addEdge(e);
    }

    /**
     * Adds and edge {@code e} to the graph if the pair
     * ({@code e.source.id,e.target.id}) is not yet in the graph
     *
     * @param e
     */
    public void addEdgeIfNotPresent(E e) {
        Pair pair = new Pair(e.source.id, e.target.id);
        if (!edgeMapping.containsKey(pair)) {
            addEdge(e);
        }
    }

    /**
     * Adds the edge to the graph, but does not add it to the nodes.
     *
     * @param e
     */
    public void addEdgesWithoutNodeUpdate(E... edgesToAdd) {
        for (E e : edgesToAdd) {
            addEdgeWithoutNodeUpdate(e);
        }
    }

    /**
     * Adds the edge to the graph, but does not add it to the nodes.
     *
     * @param e
     */
    public void addEdgeWithoutNodeUpdate(E e) {
        Pair pair = new Pair(e.source.id, e.target.id);
        assert (!edgeMapping.containsKey(pair));
        edgeMapping.put(pair, e);
    }

    public void removeEdge(E e) {
        Pair pair = new Pair(e.source.id, e.target.id);
        assert (edgeMapping.containsKey(pair));
        edgeMapping.remove(pair);

        e.source.removeEdge(e);
        e.target.removeEdge(e);
    }

    public boolean hasNodeWithId(int id) {
        return nodeMapping.containsKey(id);
    }

    public N getNode(int id) {
        return nodeMapping.get(id);
    }

    public Collection<N> getNodes() {
        return nodeMapping.values();
    }

    public Collection<E> getEdges() {
        return edgeMapping.values();
    }

    public E getEdge(int id1, int id2) {
        return edgeMapping.get(new Pair(id1, id2));
    }

    public Collection<E> getEdges(N n) {
        Set<E> nEdges = new HashSet();
        for (E e : edgeMapping.values()) {
            if (e.source == n || e.target == n) {
                nEdges.add(e);
            }
        }
        return nEdges;
    }

    public Graph deepCopy() {
        Graph g = GraphFactory.getNewGraph(this.getClass().getSimpleName());

        HashMap<Integer, N> newNodeMapping = new HashMap();

        for (N n : getNodes()) {
            N newN = (N) n.deepCopy();
            g.addNode(newN);
            newNodeMapping.put(newN.id, newN);
        }

        for (E e : getEdges()) {
            E newE = (E) e.deepCopy(newNodeMapping);
            g.addEdge(newE);
        }

        return g;
    }

    public void removeEdges(Set<E> toRemove) {
        for (E e : toRemove) {
            removeEdge(e);
        }
    }

    /**
     * Returns all nodes reachable from {@code node}
     *
     * @param startN
     * @return
     */
    public Collection<N> getReachableNodes(N startN) {
        Set<N> reachableNodes = new HashSet();//nodes that can be reached from startN
        reachableNodes.add(startN);

        Queue<N> unvisitedNodes = new LinkedList();//nodes that can be reached from startN but haven't been visited yet.
        unvisitedNodes.add(startN);

        while (!unvisitedNodes.isEmpty()) {
            N n = unvisitedNodes.poll();

            List<E> outgoingEdges = n.getOutgoingEdges();
            for (E e : outgoingEdges) {
                N target = (N) e.target;
                if (!reachableNodes.contains(target)) {//not going in a loop
                    unvisitedNodes.add(target);//add it to the queue
                    reachableNodes.add(target);//can be reached
                }

            }
        }

        return reachableNodes;
    }

    /**
     * Is there a directed cycle reachable from node n?
     *
     * @param n
     * @return
     */
    public boolean checkDirectedCycle(N n) {
        HashSet<N> visitedNodes = new HashSet();
        return checkDirectedCycle(n, null, visitedNodes);
    }

    protected boolean checkDirectedCycle(N currentNode, N previousNode, HashSet<N> visitedNodes) {
        visitedNodes.add(currentNode);
        List<E> outgoingEdges = (List<E>) currentNode.getOutgoingEdges();
        for (E edge : outgoingEdges) {
            N target = (N) edge.target;
            if (target == previousNode) {
                continue;//not allowed to go back
            }

            if (visitedNodes.contains(target)) {
                System.out.println(edge.toString());//print the cycle
                return true;
            }
            if (checkDirectedCycle(target, currentNode, visitedNodes)) {
                System.out.println(edge.toString());//print the cycle
                return true;
            }
        }
        return false;
    }

}
