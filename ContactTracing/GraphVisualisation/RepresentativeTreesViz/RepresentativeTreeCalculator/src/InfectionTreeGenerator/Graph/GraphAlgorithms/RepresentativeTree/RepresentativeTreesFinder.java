/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfectionTreeGenerator.Graph.GraphAlgorithms.RepresentativeTree;

import Export.GraphWriter;
import Utility.Log;
import InfectionTreeGenerator.Graph.GraphAlgorithms.DistanceMeasures.TreeDistanceMeasure;
import InfectionTreeGenerator.Graph.GraphAlgorithms.DistanceMeasures.TreeEditDistance.TreeEditDistanceCalculator;
import InfectionTreeGenerator.Graph.GraphAlgorithms.DominatingSetCalculatorImplicit;
import InfectionTreeGenerator.Graph.Tree;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author MaxSondag
 */
public class RepresentativeTreesFinder {

    public final int MAXEDITDISTANCE = 100;

    /**
     * Calculates and writes to file representative trees based on the specified
     * distance measure. Trees of size 1 are always represented by a single
     * representative tree
     *
     * @param forest
     * @param startSize
     * @param endSize
     * @param dm
     * @param outputFilePrefix
     * @return
     * @throws IOException
     */
    public List<RepresentativeTree> getAndWriteRepresentativeTreeData(Set<Tree> forest, int startSize, int endSize, TreeDistanceMeasure dm, String outputFilePrefix) throws IOException {

        //get the forest
        //Group forest into size categories
        HashMap<Integer, List<Tree>> treesBySizeMap = new HashMap();
        for (Tree tree : forest) {
            //add the tree to the map with the correct amount of nodes.
            int size = tree.getNodes().size();
            List<Tree> treeList = treesBySizeMap.getOrDefault(size, new ArrayList<>());
            treeList.add((Tree) tree);
            treesBySizeMap.put(size, treeList);
        }
        for (Entry<Integer, List<Tree>> entry : treesBySizeMap.entrySet()) {
            Integer size = entry.getKey();
            List<Tree> trees = entry.getValue();

            Log.printProgress("To calculate: " + trees.size() + " trees with " + size + " nodes.");
        }

        Log.printProgress(treesBySizeMap.size() + " groups of trees to calculate");
        Log.printProgress(forest.size() + " total trees");

        List<RepresentativeTree> allRepTrees = new ArrayList();

        //for each group, calculate the representativeTrees
        for (int size = startSize; size < endSize; size++) {//go through them in order
            if (!treesBySizeMap.containsKey(size)) {
                continue;
            }

            List<Tree> trees = treesBySizeMap.get(size);
            Collection<RepresentativeTree> repTrees;
            if (size == 1 || size == 2) {
                System.out.println("Skipping " + trees.size() + " trivial cases");
                repTrees = calculateTrivialCases(trees);
            } else {
                Log.printProgress("Calculating for " + trees.size() + " trees with " + size + "nodes.");
                //calculate the representative trees
                repTrees = calculateRepresentativeTrees(trees, dm);
            }
            allRepTrees.addAll(repTrees);
            //write the representativeTrees
            GraphWriter tw = new GraphWriter();
            tw.writeRepresentativeTrees(outputFilePrefix + size + ".json", repTrees);
            System.out.println("Trees of size " + size + " written to file");
        }

        //returns the trees;
        return allRepTrees;

    }

    /**
     * Alternative method of calculating representativeTrees that is less memory
     * intensive
     *
     * @param trees
     * @param dm
     * @return
     */
    private Collection<RepresentativeTree> calculateRepresentativeTrees(List<Tree> trees, TreeDistanceMeasure dm) {

        //Start calculating representative trees.
        //We find it by using dominating set on filtered trees. The dominating set are the set of representative nodes
        //where all other nodes can transform into within {ted} graph change moves.
        DominatingSetCalculatorImplicit dsc = new DominatingSetCalculatorImplicit(trees, dm);

        //Init dominating set
        //start with getting a dominating graph on distance 0 and slowly add edges.
        List<Integer> currentDsIds = dsc.getDominatingSet(0);

        //initialize the representing trees, they only get filtered down, so these are all Representative trees that will exists.
        //maps from id to a representative tree
        HashMap<Integer, RepresentativeTree> repTrees = initRepTrees(currentDsIds, trees);

        //go through the edit distances
        for (int ted = 0; ted <= MAXEDITDISTANCE; ted++) {
            Log.printProgress("Calculating for distance " + ted, 1000);
            //trim the dominating set down instead of recalculating so we keep the original trees. Trees thus only disappear
            List<Integer> dsTrimmed = dsc.trimDominatingSet(currentDsIds, ted);

            //holds the set of trees that are assigned to a dominating tree at a certaindistance
            HashMap<Integer, List<Tree>> mapping = dsc.calculateDominationMapping(dsTrimmed, ted, dm);

            for (Integer id : dsTrimmed) {
                RepresentativeTree repTree = repTrees.get(id);
                List<Tree> treesMapped = mapping.get(id);

                //calculate the mapping from repTree to all treesMapped
                TreeEditDistanceCalculator tedC = new TreeEditDistanceCalculator();
                for (Tree tm : treesMapped) {
                    if (!repTree.treesAlreadyMapped.contains(tm)) {//not mapped yet, so mapping isn't stored yet
                        tedC.calculateMapping(repTree.originalTree, tm);
                    }
                }
                repTree.addToMapping(ted, treesMapped, tedC);
            }
            currentDsIds = dsTrimmed;
        }
        return repTrees.values();
    }

    private HashMap<Integer, RepresentativeTree> initRepTrees(List<Integer> dsIds, List<Tree> trees) {
        HashMap<Integer, RepresentativeTree> idMapping = new HashMap();
        for (Tree t : trees) {
            //only add the trees that are representing something.
            if (dsIds.contains(t.id)) {
                RepresentativeTree rt = new RepresentativeTree(t);
                idMapping.put(t.id, rt);
            }
        }
        return idMapping;
    }

    /**
     * Calculates representative trees that always have the same structure (all
     * of size 1 and 2), and represents them by a single tree.
     *
     * @param trees
     * @return
     */
    private Collection<RepresentativeTree> calculateTrivialCases(List<Tree> trees) {
        ArrayList<RepresentativeTree> repTrees = new ArrayList();
        if (trees.isEmpty()) {
            return repTrees;
        }
        //all trees will be represented by this tree
        RepresentativeTree repTree = new RepresentativeTree(trees.get(0));

        //add the mappings in the tedC.
        //TODO: Can be optimized further if needed by not calculating the LP at all.
        TreeEditDistanceCalculator tedC = new TreeEditDistanceCalculator();
        for (Tree tm : trees) {
            if (!repTree.treesAlreadyMapped.contains(tm)) {//not mapped yet, so mapping isn't stored yet
                tedC.calculateMapping(repTree.originalTree, tm);
            }
        }
        //add it to distance 0 regardless of the distance so that all trees always map to repTree
        repTree.addToMapping(0, trees, tedC);
        repTree.maxEditDistance = MAXEDITDISTANCE;

        repTrees.add(repTree);

        return repTrees;
    }

}
