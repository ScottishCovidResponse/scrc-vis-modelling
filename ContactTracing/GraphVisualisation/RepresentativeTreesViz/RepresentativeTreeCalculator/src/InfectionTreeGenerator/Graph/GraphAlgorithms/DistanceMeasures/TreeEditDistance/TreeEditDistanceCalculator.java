/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfectionTreeGenerator.Graph.GraphAlgorithms.DistanceMeasures.TreeEditDistance;

import Utility.Pair;
import InfectionTreeGenerator.Graph.Edge;
import InfectionTreeGenerator.Graph.GraphAlgorithms.DistanceMeasures.TreeDistanceMeasure;
import InfectionTreeGenerator.Graph.GraphAlgorithms.RepresentativeTree.RepresentativeTree;
import InfectionTreeGenerator.Graph.Node;
import InfectionTreeGenerator.Graph.Tree;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @author MaxSondag
 */
public class TreeEditDistanceCalculator<N extends Node, E extends Edge> implements TreeDistanceMeasure {

    /**
     * Holds the node mapping for the TED of all calculated pairs of trees so
     * far
     */
    public HashMap<Pair<Tree, Tree>, TEDMapping<N, E>> tedMapping = new HashMap();

    public TreeEditDistanceCalculator() {
    }

    @Override
    public int getDistance(Tree t1, Tree t2) {
        return getTreeEditDistance(t1, t2);
    }

    /**
     * Gets the tree between two graphs and stores the mapping of the minimal
     * tree edit distance
     *
     * @param t1
     * @param t2
     * @return
     */
    public int getTreeEditDistance(Tree t1, Tree t2) {
        //calculate using an lp
        TreeEditDistanceLPCplex lp = new TreeEditDistanceLPCplexPrioritizeDepth(t1, t2);
        int distance = lp.solve();
        TEDMapping<N, E> mapping = lp.getMapping();
        tedMapping.put(new Pair(t1, t2), mapping);
        tedMapping.put(new Pair(t2, t1), mapping.inverse());

        return distance;
    }

    public void calculateMapping(Tree t1, Tree t2) {
        //automatically computes the mapping as well
        getTreeEditDistance(t1, t2);

    }

}
