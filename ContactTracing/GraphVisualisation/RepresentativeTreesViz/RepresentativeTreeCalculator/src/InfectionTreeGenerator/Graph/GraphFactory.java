/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfectionTreeGenerator.Graph;

import InfectionTreeGenerator.Graph.Infection.InfectionGraph;

/**
 *
 * @author MaxSondag
 */
public class GraphFactory {

    public static <G extends Graph> G getNewGraph(String graphType) {
        if (graphType == null) {
            return null;
        }
        if (graphType.equalsIgnoreCase("InfectionGraph")) {
            return (G) new InfectionGraph();
        }
        if (graphType.equalsIgnoreCase("Tree")) {
            return (G) new Tree();
        }
        if (graphType.equalsIgnoreCase("Graph")) {
            return (G) new Graph();
        }
        throw new UnsupportedOperationException("Type " + graphType + " is not supported yet");
    }

}
