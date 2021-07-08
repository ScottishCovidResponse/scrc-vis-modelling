/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testUtility;

import InfectionTreeGenerator.Graph.Edge;
import InfectionTreeGenerator.Graph.GraphAlgorithms.RepresentativeTree.RepresentativeEdge;
import InfectionTreeGenerator.Graph.GraphAlgorithms.RepresentativeTree.RepresentativeNode;
import InfectionTreeGenerator.Graph.Node;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author MaxSondag
 */
public class Utlity {

    public static <X> boolean checkCollectionContentEqual(Collection<X> l1, Collection<X> l2) {
        if (l1.size() != l2.size()) {
            return false;
        }
        for (X o : l1) {
            if (!l2.contains(o)) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkRepresentation(RepresentativeNode rn, int distance, List<Node> nodes) {
        List<Node> representList = rn.getRepresentNodes(distance);

        return checkCollectionContentEqual(representList, nodes);
    }

    public static boolean checkRepresentation(RepresentativeEdge re, int distance, List<Edge> edges) {
        List<Edge> representList = re.getRepresentEdges(distance);
        return checkCollectionContentEqual(representList, edges);
    }

    public static boolean checkRepresentationEmpty(RepresentativeEdge re, Integer... distances) {
        for (int distance : distances) {
            List<Edge> representList = re.getRepresentEdges(distance);
            if (checkCollectionContentEqual(representList, new ArrayList()) == false) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkRepresentationEmpty(RepresentativeNode rn, Integer... distances) {
        for (int distance : distances) {
            List<Node> representList = rn.getRepresentNodes(distance);
            if (checkCollectionContentEqual(representList, new ArrayList()) == false) {
                return false;
            }
        }
        return true;
    }
}
