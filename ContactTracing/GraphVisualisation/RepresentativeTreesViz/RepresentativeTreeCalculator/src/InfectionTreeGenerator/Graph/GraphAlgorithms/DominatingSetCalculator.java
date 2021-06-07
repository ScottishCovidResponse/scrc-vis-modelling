/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfectionTreeGenerator.Graph.GraphAlgorithms;

import Utility.Log;
import InfectionTreeGenerator.Graph.Node;
import InfectionTreeGenerator.Graph.Edge;
import InfectionTreeGenerator.Graph.Graph;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author MaxSondag
 */
public class DominatingSetCalculator {

    /**
     * Returns a set of node-ids that is a dominating set.
     *
     * @param graph
     * @return
     */
    public List<Integer> getDominatingSet(Graph g) {
        Log.printOnce("getDominatingSet can be optimized to a simple approximation algorithm or an LP");

        ArrayList<Integer> dominatingSet = new ArrayList();

        //nodes that are not connected always need to be in
        Collection<Node> nodes = g.getNodes();

        //Trivial algorithm. Go through the nodes. For each node if it is not yet dominated, add it to the set.
        for (Node n : nodes) {
            if (!isDominated(g, dominatingSet, n)) {
                dominatingSet.add(n.id);
            }
        }
        return dominatingSet;
    }

    /**
     * Returns a new dominating set containing only the ids of the nodes from
     * {@code indSet} to dominate newG
     *
     * @param domSet A set of nodeIds.
     * @param newG A graph that is covered by {@code domSet}
     * @return
     */
    public ArrayList<Integer> trimDominatingSet(Graph newG, List<Integer> domSet) {
        //start by taking all node, and keep removing them as long as the result remaind a dominating set
        ArrayList<Integer> domSetTrimmed = new ArrayList(domSet);
        ArrayList<Integer> idsToConsider = new ArrayList(domSetTrimmed);

        for (Integer id : idsToConsider) {
            //check if removing node with id from the dominating set still gives a dominating set
            domSetTrimmed.remove(id);
            if (!isDominatingSet(newG, domSetTrimmed)) {//TODO: Can be optimized by only checking nodes around {id}
                //not a dominating set anymore, put it back
                domSetTrimmed.add(id);
            }
        }

        return domSetTrimmed;
    }

    private boolean isDominatingSet(Graph g, ArrayList<Integer> domSet) {
        Collection<Node> nodes = g.getNodes();
        for (Node n : nodes) {
            if (!isDominated(g, domSet, n)) {
                return false;
            }
        }
        return true;//all nodes are dominated
    }

    public boolean isDominated(Graph g, List<Integer> domSet, Node n) {

        if (domSet.contains(n.id)) {
            return true;
        }
        List<Edge> edges = n.edges;

        for (Edge e : edges) {//directed edge
            if (domSet.contains(e.source.id)) {
                return true;
            }
        }

        return false;//there is a node not dominated
    }
}
