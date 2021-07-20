/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Export;

import InfectionTreeGenerator.Graph.Edge;
import InfectionTreeGenerator.Graph.Node;
import InfectionTreeGenerator.Graph.Tree;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author MaxSondag
 */
public class TreeNodeJson {
    /**
     * Id of the node
     */
    public int id;

    /**
     * Children of this node
     */
    public List<TreeNodeJson> children = new ArrayList();

    public TreeNodeJson(Tree t) {
        Node root = t.calculateRoot();
        initialize(root);
    }

    protected TreeNodeJson(Node n) {
        initialize(n);
    }

    protected void initialize(Node root) {
        this.id = root.id;

        //recurse into the children
        List<Edge> outEdges = root.getOutgoingEdges();
        for (Edge e : outEdges) {
            Node child = e.target;
            TreeNodeJson rnChild = new TreeNodeJson(child);
            children.add(rnChild);
        }
    }

    public TreeNodeJson getChild(int id) {
        for (TreeNodeJson rnj : children) {
            if (rnj.id == id) {
                return rnj;
            }
        }
        return null;
    }
}
