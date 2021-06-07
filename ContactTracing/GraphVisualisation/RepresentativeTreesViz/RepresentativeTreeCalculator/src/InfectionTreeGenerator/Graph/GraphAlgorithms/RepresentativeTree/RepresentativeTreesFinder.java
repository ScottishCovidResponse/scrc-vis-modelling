/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfectionTreeGenerator.Graph.GraphAlgorithms.RepresentativeTree;

import Export.GraphWriter;
import Utility.Log;
import InfectionTreeGenerator.Graph.Edge;
import InfectionTreeGenerator.Graph.Graph;
import InfectionTreeGenerator.Graph.GraphAlgorithms.ForestFinder;
import InfectionTreeGenerator.Graph.GraphAlgorithms.DominatingSetCalculator;
import InfectionTreeGenerator.Graph.GraphAlgorithms.DistanceMeasures.TreeDistanceMeasure;
import InfectionTreeGenerator.Graph.GraphAlgorithms.DistanceMeasures.TreeEditDistance.TreeEditDistanceCalculator;
import InfectionTreeGenerator.Graph.Node;
import InfectionTreeGenerator.Graph.Tree;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author MaxSondag
 */
public class RepresentativeTreesFinder {

    public final int MAXEDITDISTANCE = 100;

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

            Log.printProgress("Calculating for " + trees.size() + " trees with " + size + "nodes.");

            //calculate the representative trees
            Collection<RepresentativeTree> repTrees = calculateRepresentativeTrees(trees, dm);
            allRepTrees.addAll(repTrees);

            //write the representativeTrees
            GraphWriter tw = new GraphWriter();
            tw.writeRepresentativeTrees(outputFilePrefix + size + ".json", repTrees);
        }

        //returns the trees;
        return allRepTrees;

    }

    /**
     * Returns a set of representativeTrees for the collection of {@code trees}.
     *
     * @param trees
     * @return
     */
    private Collection<RepresentativeTree> calculateRepresentativeTrees(List<Tree> trees, TreeDistanceMeasure dm) {

        //Holds the graphs as nodes, and uses the specified distance measure as weights between the nodes
        Graph g = makeWeightedGraph(trees, dm);

        //Start calculating representative trees.
        //We find it by using dominating set on filtered trees. The dominating set are the set of representative nodes
        //where all other nodes can transform into within {ted} graph change moves.
        DominatingSetCalculator dsc = new DominatingSetCalculator();

        //start with an graph on distance 0 and slowly add edges. Used to intialize the dominating set
        Graph fg0 = getFilteredGraph(g, 0);
        List<Integer> currentDsIds = dsc.getDominatingSet(fg0);

        //initialize the representing trees, they only get filtered down, so these are all Representative trees that will exists.
        //maps from id to a representative tree
        HashMap<Integer, RepresentativeTree> repTrees = initRepTrees(currentDsIds, trees);

        //go through the edit distances
        for (int ted = 0; ted <= MAXEDITDISTANCE; ted++) {
            //Get graph with only edgeMapping with weight <= ted
            Graph fgTED = getFilteredGraph(g, ted);

            //trim the dominating set down instead of recalculating so we keep the original trees. Trees thus only disappear
            List<Integer> dsTrimmed = dsc.trimDominatingSet(fgTED, currentDsIds);

            //holds the set of trees that are assigned to a dominating tree at distance {ted}
            HashMap<Integer, List<Tree>> mapping = calculateDominationMapping(fgTED, dsTrimmed, trees);

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

    private Graph makeWeightedGraph(List<Tree> trees, TreeDistanceMeasure tdm) {
        Graph g = new Graph();

        //make nodes for each tree
        HashMap<Integer, Node> nodeMapping = new HashMap();
        HashMap<Integer, Tree> graphMapping = new HashMap();
        for (int i = 0; i < (trees.size()); i++) {
            Tree t = trees.get(i);
            Node n = new Node(t.id, 1);
            nodeMapping.put(n.id, n);
            graphMapping.put(n.id, t);

            g.addNode(n);
        }

        int count = 0;//counter for progesas
        for (int i = 0; i < (trees.size() - 1); i++) {
            Tree t1 = trees.get(i);
            for (int j = i + 1; j < trees.size(); j++) {

                count++;
                if (count % 10 == 0) {
                    Log.printProgress("Calculating distance " + count + " out of " + ((trees.size() * trees.size() - trees.size()) / 2), 1000);
                }

                Tree t2 = trees.get(j);

                int distance = tdm.getDistance(t1, t2);

                Node n1 = nodeMapping.get(t1.id);
                Node n2 = nodeMapping.get(t2.id);
                Edge weOut = new Edge(n1, n2, distance);
                //ted is symmetric
                Edge weIn = new Edge(n2, n1, distance);
                g.addEdge(weOut);
                g.addEdge(weIn);
            }
        }
        return g;
    }

    /**
     * Returns a new graph with the same id's that only has edges with weight
     * below or equal to {@code weight}
     *
     * @param weight
     * @return
     */
    private Graph getFilteredGraph(Graph<Node, Edge> g, double weight) {
        Graph<Node, Edge> deepCopy = g.deepCopy();
        Set<Edge> toRemove = new HashSet();
        for (Edge e : deepCopy.getEdges()) {
            if (e.weight > weight) {
                toRemove.add(e);
            }
        }
        deepCopy.removeEdges(toRemove);
        return deepCopy;
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
     * Returns for a given graphId the set of graphs that it is "assigned" to to
     * dominate. Each graph is only assigned to one other graph even if
     * dominated by more graphs. If a tree is in the dominating set, it always
     * dominates itself
     *
     * @param trimmedGraph The graph the dominating set is based upon
     * @param dsIds Set of graph ids that are in the dominating set.
     * @param trees all trees
     * @return
     */
    private HashMap<Integer, List<Tree>> calculateDominationMapping(Graph trimmedGraph, List<Integer> dsIds, List<Tree> trees) {
        HashMap<Integer, List<Tree>> mapping = new HashMap();

        //make a copy so we can freely delete those that we have mapped
        List<Tree> remainingTrees = new ArrayList();
        remainingTrees.addAll(trees);

        for (Integer id : dsIds) {
            //the current tree we are looking into the dominance relations of
            Tree domTree = getTreeWithId(trees, id);

            Node domTreeNode = trimmedGraph.getNode(id);
            List<Edge> edges = domTreeNode.edges;

            List<Tree> dominated = new ArrayList();

            //it dominated itself
            dominated.add(domTree);
            //and all trees on outgoing edges
            for (Edge e : edges) {
                Tree t = getTreeWithId(remainingTrees, e.target.id);
                if (t == null) {//already processed
                    continue;
                }
                //unless t is in the dominating set
                if (!dsIds.contains(t.id)) {
                    dominated.add(t);
                }
            }

            mapping.put(id, dominated);

            //a tree can only be dominated once
            remainingTrees.removeAll(dominated);
        }
        assert (remainingTrees.isEmpty());
        return mapping;
    }

    private Tree getTreeWithId(List<Tree> trees, Integer id) {
        for (Tree t : trees) {
            if (t.id == id) {
                return t;
            }
        }
        return null;
    }

}
