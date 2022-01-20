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

    public List<Integer> getDominatingSet(int maxDistance, List<Tree> trees, TreeDistanceMeasure dm) {
        ArrayList<Integer> dominatingSet = new ArrayList();
        //nodes that are not connected always need to be in

        int count = 0;
        //Trivial algorithm. Go through the nodes. For each node if it is not yet dominated, add it to the set.
        for (Tree t : trees) {
            Log.printProgress("Calculating dominating set, tree " + count + " out of " + trees.size(), 100);

            if (!isDominated(dominatingSet, t, trees, dm, maxDistance)) {
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
    public List<Integer> trimDominatingSet(List<Integer> currentDomSet, List<Tree> trees, TreeDistanceMeasure dm, int maxDistance) {
        //start by taking all trees, and keep removing them as long as the result remaind a dominating set
        ArrayList<Integer> trimmedDomSet = new ArrayList(currentDomSet);
        ArrayList<Integer> idsToConsider = new ArrayList(currentDomSet);

        int count = 0;
        for (Integer id : idsToConsider) {
            Log.printProgress("Calculating trimmed dominating set, tree " + count + " out of " + trees.size(), 100);
            //check if removing node with id from the dominating set still gives a dominating set
            trimmedDomSet.remove(id);
            if (!isDominatingSet(trimmedDomSet, trees, dm, maxDistance)) {
                //not a dominating set anymore, put it back
                trimmedDomSet.add(id);
            }
            count++;
        }

        return trimmedDomSet;
    }

    private boolean isDominatingSet(ArrayList<Integer> candidateDomSet, List<Tree> trees, TreeDistanceMeasure dm, int maxDistance) {
        for (Tree t : trees) {
            if (!isDominated(candidateDomSet, t, trees, dm, maxDistance)) {
                return false;//t is not dominated
            }
        }
        return true;//all nodes are dominated
    }

    private boolean isDominated(ArrayList<Integer> domSet, Tree t, List<Tree> trees, TreeDistanceMeasure dm, double maxDistance) {
        if (domSet.contains(t.id)) {
            return true;
        }

        //go through all trees in the dominating set, and check if one of them dominates this tree
        for (Integer id : domSet) {
            Tree tId = getTreeById(trees, id);
            double distance = dm.getDistance(tId, t);
            if (distance <= maxDistance) {
                return true; //t is dominated by tId
            }
        }
        //no tree dominates this node
        return false;
    }

    private Tree getTreeById(List<Tree> trees, Integer id) {
        for (Tree t : trees) {
            if (t.id == id) {
                return t;
            }
        }
        System.err.println("No tree with id:" + id + "exists");
        return null;
    }

}
