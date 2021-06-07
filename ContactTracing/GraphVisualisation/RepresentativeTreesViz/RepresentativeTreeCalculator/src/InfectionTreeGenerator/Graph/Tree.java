/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfectionTreeGenerator.Graph;

import InfectionTreeGenerator.Graph.Infection.InfectionNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author MaxSondag
 */
//public class Tree<N extends Node<E>, E extends Edge<N>> extends Graph<N extends Node<E>, E extends Edge<N>> {
public class Tree<N extends Node<E>, E extends Edge<N>> extends Graph {

    //returns if possibleAncestor is an ancestor of n
    public boolean isAncestor(N possibleAncestor, N n) {

        N parent = getParent(n);
        if (parent == null) {//root node
            return false;
        } else if (parent.id == possibleAncestor.id) {
            return true;
        }
        //if we are not at the root and haven't found it yet, recurse up.
        return isAncestor(possibleAncestor, parent);
    }

    /**
     * Returns the parent or null if this node is the root
     *
     * @param n
     * @return
     */
    public N getParent(N n) {
        //At most a single incoming edge for trees
        List<E> incomingEdge = n.getIncomingEdges();
        if (incomingEdge.isEmpty()) {
            return null;
        } else {
            return incomingEdge.get(0).source;
        }
    }

    public N calculateRoot() {
        Collection<N> nodes = getNodes();
        //get a node from the collection
        N node = null;
        for (N n : nodes) {
            node = n;
            break;
        }
        //keep going towards it's ancestors untill we don't find a parent anymore.
        while (true) {
            N parent = getParent(node);
            if (parent == null) {//found the root
                return node;
            } else {//recurse
                node = parent;
            }
        }
    }

    public int getDepth() {
        HashMap<Integer, Set<N>> depthMap = getDepthMap();
        int maxDepth = -1;
        for (Integer depth : depthMap.keySet()) {
            maxDepth = Math.max(maxDepth, depth);
        }
        return maxDepth;
    }

    public int getDepth(N node) {
        HashMap<Integer, Set<N>> depthMap = getDepthMap();
        for (Entry<Integer, Set<N>> depthSet : depthMap.entrySet()) {
            if (depthSet.getValue().contains(node)) {
                return depthSet.getKey();
            }
        }
        throw new IllegalArgumentException("Node " + node + " is not in the tree");
    }

    /**
     * For each depth, returns how many nodes are on this level
     *
     * @return
     */
    private HashMap<Integer, Set<N>> getDepthMap() {
        N root = calculateRoot();
        HashMap<Integer, Set<N>> nodesPerDepth = new HashMap();
        HashSet d0 = new HashSet(Arrays.asList(root));

        nodesPerDepth.put(0, d0);
        int currentDepth = 0;
        while (nodesPerDepth.containsKey(currentDepth)) {
            //go through all the nodes, and add the next level
            Set<N> nodes = nodesPerDepth.get(currentDepth);
            Set<N> nextDepthSet = nodesPerDepth.getOrDefault(currentDepth + 1, new HashSet());

            for (N node : nodes) {
                List<E> edges = node.getOutgoingEdges();
                for (E edge : edges) {
                    nextDepthSet.add(edge.target);
                }
            }
            if (!nextDepthSet.isEmpty()) {
                nodesPerDepth.put(currentDepth + 1, nextDepthSet);
            }
            currentDepth++;
        }
        return nodesPerDepth;
    }

    /**
     * Sorts the edges of the tree from each node such that the middle edge in
     * the list the most nodes, then alternatingly left and down from most to
     * least nodes
     */
    public void sortTree() {
        Collection<N> nodes = getNodes();
        for (N n : nodes) {
            HashMap<E, Integer> countPerEdge = new HashMap();
            for (E e : n.edges) {
                countPerEdge.put(e, countNodesInSubtree(e.target));
            }

            ArrayList<E> edgesHighLow = new ArrayList();
            edgesHighLow.addAll(n.edges);
            edgesHighLow.sort(new Comparator<E>() {
                public int compare(E e1, E e2) {
                    return countPerEdge.get(e2).compareTo(countPerEdge.get(e1));//e2 compare to e1 to sort high to low
                }
            });

            int middle = (int) Math.floor(((double) n.edges.size()) / 2.0);
            //overwrite the existing array
            for (double i = 0; i < n.edges.size(); i++) {
                boolean add; //true if add from middle, false if subtract
                if (i % 2 == 1) {   //substract on odd row
                    add = false;
                } else {
                    add = true;
                }

                int offSet = (int) Math.floor((i + 1) / 2.0);
                int replaceI = middle;
                if (add) {
                    replaceI += offSet;
                } else {
                    replaceI -= offSet;
                }

                //replace the edge
                n.edges.add(replaceI, edgesHighLow.get((int) i));
                n.edges.remove(replaceI + 1);

            }
        }
    }

    private int countNodesInSubtree(N n) {
        List<E> outgoingEdges = n.getOutgoingEdges();
        int count = 1; //count yourself
        for (E e : outgoingEdges) {
            //count all subtrees
            count += countNodesInSubtree(e.target);
        }

        return count;
    }

    public Collection<N> getChildren(N n) {
        Collection<E> edges = getEdges(n);
        Collection<N> children = new ArrayList();
        for (E e : edges) {
            if (e.source == n) {//add outgoing edges
                children.add(e.target);
            }
        }
        return children;
    }

    
    
    
}
