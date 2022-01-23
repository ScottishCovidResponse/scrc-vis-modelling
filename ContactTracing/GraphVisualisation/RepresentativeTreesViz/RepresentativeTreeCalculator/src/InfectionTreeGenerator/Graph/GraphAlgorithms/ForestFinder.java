/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfectionTreeGenerator.Graph.GraphAlgorithms;

import InfectionTreeGenerator.Graph.Edge;
import InfectionTreeGenerator.Graph.Graph;
import InfectionTreeGenerator.Graph.GraphFactory;
import InfectionTreeGenerator.Graph.Node;
import InfectionTreeGenerator.Graph.Tree;
import Utility.Log;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author MaxSondag
 */
public class ForestFinder<G extends Graph, T extends Tree, N extends Node, E extends Edge> {

    G completeGraph;
    private final String graphType;

    /**
     *
     * @param completeGraph
     * @param GraphType
     */
    public ForestFinder(G completeGraph, Class treeType) {
        this.completeGraph = completeGraph;
        this.graphType = treeType.getSimpleName();
    }

    /**
     * Returns a set of graphs which are all trees. References of nodes and
     * edges will have changed. Each tree will have the id of the root node as
     * an id.
     *
     * @return
     */
    public Set<T> getForest() {
        Set<T> trees = new HashSet();

        HashMap<Integer, T> treeMap = new HashMap();

        //start by making all nodes the root of a tree, and merge them together
        Collection<N> nodes = completeGraph.getNodes();
        for (N node : nodes) {
            T newTree = GraphFactory.getNewGraph(graphType);
            newTree.addNode(node.deepCopy());

            //give the tree the id of the root
            newTree.id = node.id;
            trees.add(newTree);
            treeMap.put(node.id, newTree);
        }

        Collection<E> edges = completeGraph.getEdges();
        System.out.println("Total edges to colate: " + edges.size());
        int count = 0;
        for (E e : edges) {
            T sourceTree = treeMap.get(e.source.id); //holds the Tree the source of this edge is in.
            T targetTree = treeMap.get(e.target.id);//holds the Tree the target of this edge is in.

            //ends in two existing trees, merge them and continue with next edge
            mergeTrees(trees, treeMap, sourceTree, targetTree, e);//merge the trees as they are connected
            count++;
            Log.printProgress("Collated " + count + " edges", 10000);
        }

        return trees;
    }

    /**
     * Merge the targetTree graph into the sourceTree graph.
     *
     * @param sourceTree
     * @param targetTree
     * @param connectingEdge
     */
    private void mergeTrees(Set<T> trees, HashMap<Integer, T> treeMap, T sourceTree, T targetTree, Edge connectingEdge) {
        //merge the targetTree into the sourcetree

        //add the nodes
        Collection<N> targetNodes = targetTree.getNodes();
        for (N n : targetNodes) {
            sourceTree.addNodes(n);
            //remap the tree that this node is in from targetTree to sourceTree
            treeMap.put(n.id, sourceTree);
        }

        //add the edges
        Collection<E> edges = targetTree.getEdges();
        for (E e : edges) {
            sourceTree.addEdgeWithoutNodeUpdate(e);
        }

        //add the connecting edge and remove targettree as it is merges
        N startNode = (N) sourceTree.getNode(connectingEdge.source.id);
        N targetNode = (N) targetTree.getNode(connectingEdge.target.id);

        //make a copy of the edge, as the nodes have been copied initially as well
        Edge eCopy = connectingEdge.deepCopy(startNode, targetNode);
        sourceTree.addEdge(eCopy);

        //remove the targettrees from the list as it has been handles
        trees.remove(targetTree);
    }

}
