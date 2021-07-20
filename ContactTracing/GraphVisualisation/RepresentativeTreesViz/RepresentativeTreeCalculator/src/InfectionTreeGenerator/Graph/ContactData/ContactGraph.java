/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfectionTreeGenerator.Graph.ContactData;

import Import.RealData.MetaData;
import InfectionTreeGenerator.Graph.Graph;
import InfectionTreeGenerator.Graph.GraphFactory;
import InfectionTreeGenerator.Graph.Infection.InfectionNode;
import Utility.Pair;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * Temporal graph of contacts.
 *
 * @author MaxSondag
 */
public class ContactGraph {

    //TODO: Should be able to refactor using inheritance to duplicate some of the functionality without code duplication
    /**
     * Not automatically set
     */
    public int id;

    /**
     * Maps id's to nodes
     */
    protected HashMap<Integer, ContactNode> nodeMapping = new HashMap();
    /**
     * Maps the id's of the nodes of the edge to an edge
     */
    protected HashMap<Pair<Integer, Integer>, Set<ContactEdge>> edgeMapping = new HashMap();

    public void addNodes(ContactNode... nodesToAdd) {
        for (ContactNode n : nodesToAdd) {
            addNode(n);
        }
    }

    public void addNode(ContactNode n) {
        assert (!nodeMapping.containsKey(n.id));
        nodeMapping.put(n.id, n);
    }

    public void addNodesIfNotPresent(ContactNode... nodesToAdd) {
        for (ContactNode n : nodesToAdd) {
            addNodeIfNotpresent(n);
        }
    }

    public void addNodeIfNotpresent(ContactNode n) {
        if (!nodeMapping.containsKey(n.id)) {
            nodeMapping.put(n.id, n);
        }
    }

    public void addEdges(ContactEdge... edgesToAdd) {
        for (ContactEdge e : edgesToAdd) {
            addEdge(e);
        }
    }

    /**
     * Adds the edge to the graph and updates the nodes to reference this edge.
     *
     * @param e
     */
    public void addEdge(ContactEdge e) {
        addEdgeWithoutNodeUpdate(e);

        e.source.addEdge(e);
        e.target.addEdge(e);
    }

    /**
     * Adds the edge to the graph, but does not add it to the nodes.
     *
     * @param e
     */
    public void addEdgesWithoutNodeUpdate(ContactEdge... edgesToAdd) {
        for (ContactEdge e : edgesToAdd) {
            addEdgeWithoutNodeUpdate(e);
        }
    }

    /**
     * Adds the edge to the graph, but does not add it to the nodes.
     *
     * @param e
     */
    public void addEdgeWithoutNodeUpdate(ContactEdge e) {
        Pair pair = new Pair(e.source.id, e.target.id);

        Set edgeSet = edgeMapping.getOrDefault(pair, new HashSet());
        edgeSet.add(e);
        edgeMapping.put(pair, edgeSet);
    }

    public void removeEdge(ContactEdge e) {
        Pair pair = new Pair(e.source.id, e.target.id);
        assert (edgeMapping.containsKey(pair));
        edgeMapping.get(pair).remove(e);

        e.source.removeEdge(e);
        e.target.removeEdge(e);
    }

    public boolean hasNodeWithId(int id) {
        return nodeMapping.containsKey(id);
    }

    public ContactNode getNode(int id) {
        return nodeMapping.get(id);
    }

    public Collection<ContactNode> getNodes() {
        return nodeMapping.values();
    }

    public Collection<ContactEdge> getEdges() {
        Set<ContactEdge> edges = new HashSet();
        for (Set<ContactEdge> eSet : edgeMapping.values()) {
            for (ContactEdge e : eSet) {
                edges.add(e);
            }
        }
        return edges;
    }

    public Set<ContactEdge> getEdgeSet(int id1, int id2) {
        return edgeMapping.get(new Pair(id1, id2));
    }

    /**
     * Get all edges starting or ending in the node with id {@code id}
     */
    public Set<ContactEdge> getEdges(int id) {
        ContactNode cn = getNode(id);
        return getEdges(cn);
    }

    //Get all edges starting or ending in node n
    public Set<ContactEdge> getEdges(ContactNode n) {
        Set<ContactEdge> nEdges = new HashSet();
        for (Set<ContactEdge> eSet : edgeMapping.values()) {
            for (ContactEdge e : eSet) {
                if (e.source == n || e.target == n) {
                    nEdges.add(e);
                }
            }
        }
        return nEdges;
    }

    public Graph deepCopy() {
        Graph g = GraphFactory.getNewGraph(this.getClass().getSimpleName());

        HashMap<Integer, ContactNode> newNodeMapping = new HashMap();

        for (ContactNode n : getNodes()) {
            ContactNode newN = (ContactNode) n.deepCopy();
            g.addNode(newN);
            newNodeMapping.put(newN.id, newN);
        }

        for (ContactEdge e : getEdges()) {
            ContactEdge newE = (ContactEdge) e.deepCopy(newNodeMapping);
            g.addEdge(newE);
        }

        return g;
    }

    public void removeEdges(Set<ContactEdge> toRemove) {
        for (ContactEdge e : toRemove) {
            removeEdge(e);
        }
    }

    /**
     * Returns all nodes reachable from {@code node}
     *
     * @param startN
     * @return
     */
    public Collection<ContactNode> getReachableNodes(ContactNode startN) {
        Set<ContactNode> reachableNodes = new HashSet();//nodes that can be reached from startN
        reachableNodes.add(startN);

        Queue<ContactNode> unvisitedNodes = new LinkedList();//nodes that can be reached from startN but haven't been visited yet.
        unvisitedNodes.add(startN);

        while (!unvisitedNodes.isEmpty()) {
            ContactNode n = unvisitedNodes.poll();

            List<ContactEdge> outgoingEdges = n.getOutgoingEdges();
            for (ContactEdge e : outgoingEdges) {
                ContactNode target = e.target;
                if (!reachableNodes.contains(target)) {//not going in a loop
                    unvisitedNodes.add(target);//add it to the queue
                    reachableNodes.add(target);//can be reached
                }

            }
        }

        return reachableNodes;
    }

    /**
     * Adds the amount of contacts each node has had to the metadata of this
     * node.
     */
    public void addContactsAmountToMetadata() {
        for (ContactNode cn : getNodes()) {
            int amount = cn.getUniqueContacts().size();
            MetaData md = new MetaData("numberOfContacts", "Integer", "" + amount);
            cn.addToMetaData(md);
        }

    }
}
