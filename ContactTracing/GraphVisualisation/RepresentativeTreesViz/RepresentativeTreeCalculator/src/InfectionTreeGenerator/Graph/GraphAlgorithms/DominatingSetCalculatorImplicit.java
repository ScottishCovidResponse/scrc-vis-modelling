/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfectionTreeGenerator.Graph.GraphAlgorithms;

import InfectionTreeGenerator.Graph.Edge;
import InfectionTreeGenerator.Graph.Graph;
import InfectionTreeGenerator.Graph.GraphAlgorithms.DistanceMeasures.TreeDistanceMeasure;
import InfectionTreeGenerator.Graph.Node;
import InfectionTreeGenerator.Graph.Tree;
import Utility.Log;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Uses an implicit graph representation instead of an explicit one to save
 * meory
 *
 * @author MaxSondag
 */
public class DominatingSetCalculatorImplicit {

    List<Tree> trees;
    TreeDistanceMeasure dm;

    HashMap<Integer, Tree> treeById = new HashMap();

    public DominatingSetCalculatorImplicit(List<Tree> trees, TreeDistanceMeasure dm) {
        this.trees = trees;
        this.dm = dm;

        for (Tree t : trees) {
            treeById.put(t.id, t);
        }
    }

    public List<Integer> getDominatingSet(int maxDistance) {
        ArrayList<Integer> dominatingSet = new ArrayList();
        //nodes that are not connected always need to be in

        int count = 0;
        //Trivial algorithm. Go through the nodes. For each node if it is not yet dominated, add it to the set.
        for (Tree t : trees) {
            Log.printProgress("Calculating dominating set, tree " + count + " out of " + trees.size(), 1000);

            if (!isDominated(dominatingSet, t, maxDistance)) {
                dominatingSet.add(t.id);
            }

            count++;
        }
        return dominatingSet;
    }

    /**
     * Returns a new dominating set containing only the ids of the nodes from
     * {@code currentDsIds} to dominate trees
     *
     * @param currentDomSet
     * @param trees
     * @param dm
     * @param maxDistance
     * @return
     */
    public List<Integer> trimDominatingSet(List<Integer> currentDomSet, int maxDistance) {
        //start by taking all trees, and keep removing them as long as the result remaind a dominating set
        ArrayList<Integer> trimmedDomSet = new ArrayList(currentDomSet);
        ArrayList<Integer> idsToConsider = new ArrayList(currentDomSet);

        int count = 0;
        for (Integer id : idsToConsider) {
            Log.printProgress("Calculating trimmed dominating set, tree " + count + " out of " + trees.size(), 1000);
            //check if removing node with id from the dominating set still gives a dominating set
            trimmedDomSet.remove(id);
            if (!isDominatingSet(trimmedDomSet, maxDistance)) {
                //not a dominating set anymore, put it back
                trimmedDomSet.add(id);
            }
            count++;
        }

        return trimmedDomSet;
    }

    private boolean isDominatingSet(ArrayList<Integer> candidateDomSet, int maxDistance) {
        for (Tree t : trees) {
            if (!isDominated(candidateDomSet, t, maxDistance)) {
                return false;//t is not dominated
            }
        }
        return true;//all nodes are dominated
    }

    private boolean isDominated(ArrayList<Integer> domSet, Tree t, double maxDistance) {
        if (domSet.contains(t.id)) { //if the node is in the dominating set it is clearly dominated
            return true;
        }

        //go through all trees in the dominating set, and check if one of them dominates this tree
        for (Integer id : domSet) {
            Tree tId = treeById.get(id);
            double distance = dm.getDistance(tId, t);
            if (distance <= maxDistance) {
                return true; //t is dominated by tId
            }
        }
        //no tree dominates this node
        return false;
    }

    /**
     * Returns for a given graphId the set of trees that it is "assigned" to to
     * dominate.Each tree is only assigned to one other tree even if dominated
     * by more trees. If a tree is in the dominating set, it always dominates
     * itself
     *
     * @param dsIds Set of tree ids that are in the dominating set.
     * @param maxDistance allowed for a node to be dominated
     * @param dm the distance measure used
     * @return
     */
    public HashMap<Integer, List<Tree>> calculateDominationMapping(List<Integer> dsIds, double maxDistance, TreeDistanceMeasure dm) {
        HashMap<Integer, List<Tree>> mapping = new HashMap();

        //make a copy so we can freely delete those that we have mapped
        List<Tree> remainingTrees = new ArrayList();
        remainingTrees.addAll(trees);

        for (Integer domId : dsIds) {
            //the current tree we are looking into the dominance relations of
            Tree tDomId = treeById.get(domId);
            List<Tree> dominatedByT = new ArrayList();

            //it dominates itself
            dominatedByT.add(tDomId);

            //and all trees from {remainingtrees} within the specified distance
            for (Tree t : remainingTrees) {
                if (dsIds.contains(t.id)) { //Ignore nodes in the dominating set
                    continue;
                }
                double distance = dm.getDistance(tDomId, t);
                if (distance <= maxDistance) {
                    dominatedByT.add(t);
                }
            }

            mapping.put(domId, dominatedByT);

            //a tree can only be dominated once
            remainingTrees.removeAll(dominatedByT);
        }
        assert (remainingTrees.isEmpty());
        return mapping;
    }

}
