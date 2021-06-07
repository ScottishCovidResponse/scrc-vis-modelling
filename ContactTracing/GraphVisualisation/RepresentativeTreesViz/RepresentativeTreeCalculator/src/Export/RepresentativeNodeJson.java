/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Export;

import InfectionTreeGenerator.Graph.GraphAlgorithms.RepresentativeTree.RepresentativeEdge;
import InfectionTreeGenerator.Graph.GraphAlgorithms.RepresentativeTree.RepresentativeNode;
import InfectionTreeGenerator.Graph.GraphAlgorithms.RepresentativeTree.RepresentativeTree;
import InfectionTreeGenerator.Graph.Node;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author MaxSondag
 */
public class RepresentativeNodeJson {

    /**
     * Id of the node
     */
    public int id;
    
    /**
     * maximum edit distance that this tree is still representing nodes
     */
    public int maxEditDistance;
    /**
     * (edit distance,List<id's>) object that holds the first edit distance when
     * the nodes with id: "id's" are represented by this node
     */
    public List<RepresentationJson> representations = new ArrayList();

    /**
     * Children of this node
     */
    public List<RepresentativeNodeJson> children = new ArrayList();

    public RepresentativeNodeJson(RepresentativeTree rt) {
        this.maxEditDistance = rt.maxEditDistance;
        RepresentativeNode root = rt.calculateRoot();
        initialize(root);
    }

    private RepresentativeNodeJson(RepresentativeNode rn) {
        initialize(rn);
    }

    private void initialize(RepresentativeNode root) {
        this.id = root.id;
        //store the nodes that root represents
        HashMap<Integer, List<Node>> representNodesMapping = root.getRepresentNodesMapping();
        for (Entry<Integer, List<Node>> entry : representNodesMapping.entrySet()) {
            RepresentationJson repJson = new RepresentationJson(entry.getKey(), entry.getValue());
            representations.add(repJson);
        }

        //recurse into the children
        List<RepresentativeEdge> outEdges = root.getOutgoingEdges();
        for (RepresentativeEdge re : outEdges) {
            RepresentativeNode child = re.target;
            RepresentativeNodeJson rnChild = new RepresentativeNodeJson(child);
            children.add(rnChild);
        }
    }

    public RepresentativeNodeJson getChild(int id) {
        for (RepresentativeNodeJson rnj : children) {
            if (rnj.id == id) {
                return rnj;
            }
        }
        return null;
    }

    public class RepresentationJson {

        /**
         * First edit distance the nodes are represented by this node
         */
        public int editDistance;
        /**
         * Nodes that are represented at editDistance
         */
        public Set<Integer> representationIds = new HashSet();

        public RepresentationJson(Integer editDistance, List<Node> nodes) {
            this.editDistance = editDistance;
            for (Node n : nodes) {
                representationIds.add(n.id);
            }
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 31 * hash + this.editDistance;
            hash = 31 * hash + Objects.hashCode(this.representationIds);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final RepresentationJson other = (RepresentationJson) obj;
            if (this.editDistance != other.editDistance) {
                return false;
            }
            if (!Objects.equals(this.representationIds, other.representationIds)) {
                return false;
            }
            return true;
        }

    }

}
