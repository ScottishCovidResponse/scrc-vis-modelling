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
import java.util.Collection;
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
     * @return
     */
    public Set<T> getForest() {
        Set<T> trees = new HashSet();

        //start by making all nodes the root of a tree, and merge them together
        Collection<N> nodes = completeGraph.getNodes();
        for (N node : nodes) {
            T newTree = GraphFactory.getNewGraph(graphType);
            newTree.addNode(node.deepCopy());

            //give the tree the id of the root
            newTree.id = node.id;
            trees.add(newTree);
        }

        Collection<E> edges = completeGraph.getEdges();
        for (E e : edges) {
            T sourceTree = null; //holds the Tree the source of this edge is in.
            T targetTree = null;//holds the Tree the source of this edge is in.

            //check which trees the edge ends in
            for (T t : trees) {
                if (t.hasNodeWithId(e.source.id)) {
                    assert (!t.hasNodeWithId(e.target.id));//if it is a tree this cannot happen
                    sourceTree = t;
                }
                if (t.hasNodeWithId(e.target.id)) {
                    assert (!t.hasNodeWithId(e.source.id));//if it is a tree this cannot happen
                    targetTree = t;
                }
            }
            assert (sourceTree != null);
            assert (targetTree != null);
            //ends in two existing trees, merge them and continue with next edge
            mergeTrees(trees, sourceTree, targetTree, e);//merge the trees as they are connected
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
    private void mergeTrees(Set<T> trees, T sourceTree, T targetTree, Edge connectingEdge) {
        //add the nodes
        Collection<N> nodes = targetTree.getNodes();
        for (N n : nodes) {
            sourceTree.addNodes(n);
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
