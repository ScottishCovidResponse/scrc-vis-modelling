package Export;

import com.google.gson.Gson;
import InfectionTreeGenerator.Graph.Graph;
import InfectionTreeGenerator.Graph.GraphAlgorithms.RepresentativeTree.RepresentativeEdge;
import InfectionTreeGenerator.Graph.GraphAlgorithms.RepresentativeTree.RepresentativeNode;
import InfectionTreeGenerator.Graph.GraphAlgorithms.RepresentativeTree.RepresentativeTree;
import InfectionTreeGenerator.Graph.Infection.InfectionEdge;
import InfectionTreeGenerator.Graph.Infection.InfectionGraph;
import InfectionTreeGenerator.Graph.Infection.InfectionNode;
import InfectionTreeGenerator.Graph.Tree;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author MaxSondag
 */
public class GraphWriter {

    public void writeRepresentativeGraph(String outputFileLocation, Collection<RepresentativeTree> trees) throws IOException {
        Graph<RepresentativeNode, RepresentativeEdge> g = new Graph();
        for (RepresentativeTree rt : trees) {
            Collection<RepresentativeNode> nodes = rt.getNodes();
            for (RepresentativeNode rn : nodes) {
                g.addNode(rn);
                rn.addMaximum(rt.maxEditDistance);
            }

            Collection<RepresentativeEdge> edges = rt.getEdges();
            for (RepresentativeEdge re : edges) {
                g.addEdgeWithoutNodeUpdate(re);
                re.addMaximum(rt.maxEditDistance);
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("{\"nodes\":[");
        for (RepresentativeNode n : g.getNodes()) {
            sb.append(n.toJson());
            sb.append(",");
        }
        sb.delete(sb.length() - 1, sb.length());//delete last comma
        sb.append("],");

        sb.append("\"links\":[");
        for (RepresentativeEdge e : g.getEdges()) {
            sb.append(e.toJson());
            sb.append(",");
        }
        sb.delete(sb.length() - 1, sb.length());//delete last comma
        sb.append("]}");
        Files.writeString(Paths.get(outputFileLocation), sb.toString());
    }

    private int getMaxEditDistance(Collection<RepresentativeTree> trees) {

        int maxEditDistance = 0;
        for (RepresentativeTree rp : trees) {
            maxEditDistance = Math.max(maxEditDistance, rp.maxEditDistance);
        }
        return maxEditDistance;
    }

    public void writeInfectionGraph(String outputFileLocation, InfectionGraph ig) throws IOException {
        Gson gson = new Gson();
        FileWriter fw = new FileWriter(outputFileLocation);

        Collection<InfectionNode> infectionNodes = ig.getNodes();
        gson.toJson(infectionNodes, fw);

        fw.flush();
        fw.close();
    }

    public void writeForest(String outputFileLocation, Set<Tree> forest) throws IOException {
        Gson gson = new Gson();

        ArrayList<Tree> sortedForest = new ArrayList(forest);

        //sort first by amount of nodes, then by depth
        Collections.sort(sortedForest, new Comparator<Tree>() {
            public int compare(Tree t1, Tree t2) {
                int compareResult = Integer.compare(t1.getNodes().size(), t2.getNodes().size());
                if (compareResult == 0) {
                    return Integer.compare(t1.getDepth(), t2.getDepth());
                }
                return compareResult;
            }
        });

        ArrayList<TreeNodeJson> trees = new ArrayList();
        for (Tree t : sortedForest) {
            TreeNodeJson tnj = new TreeNodeJson(t);
            trees.add(tnj);
        }
        FileWriter fw = new FileWriter(outputFileLocation);
        gson.toJson(trees, fw);
        fw.flush();
        fw.close();
    }

    public void writeRepresentativeTrees(String outputFileLocation, Collection<RepresentativeTree> repTrees) throws IOException {
        Gson gson = new Gson();

        ArrayList<RepresentativeNodeJson> trees = new ArrayList();
        for (RepresentativeTree rt : repTrees) {
            RepresentativeNodeJson rnj = new RepresentativeNodeJson(rt);
            trees.add(rnj);
        }
        FileWriter fw = new FileWriter(outputFileLocation);
        gson.toJson(trees, fw);
        fw.flush();
        fw.close();
    }
}
