/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfectionTreeGenerator.Graph.GraphAlgorithms.DistanceMeasures;

import InfectionTreeGenerator.Graph.Infection.InfectionEdge;
import InfectionTreeGenerator.Graph.Infection.InfectionNode;
import InfectionTreeGenerator.Graph.Tree;

/**
 *
 * @author MaxSondag
 */
public interface TreeDistanceMeasure {

    /**
     * Distance between two infectionTrees.
     *
     * @param t1
     * @param t2
     * @return
     */
    public abstract double getDistance(Tree<InfectionNode, InfectionEdge> t1, Tree<InfectionNode, InfectionEdge> t2);

}
