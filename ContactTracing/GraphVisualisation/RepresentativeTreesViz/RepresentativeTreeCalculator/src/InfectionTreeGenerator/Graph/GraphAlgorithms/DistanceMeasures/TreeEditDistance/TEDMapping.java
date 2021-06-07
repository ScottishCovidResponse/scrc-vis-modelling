/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfectionTreeGenerator.Graph.GraphAlgorithms.DistanceMeasures.TreeEditDistance;

import InfectionTreeGenerator.Graph.Edge;
import InfectionTreeGenerator.Graph.Node;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 *
 * @author MaxSondag
 */
public class TEDMapping<N extends Node, E extends Edge> {

    //holds the mapping from t1 to t2
    private HashMap<N, N> nodeMapping = new HashMap();
    private HashMap<E, E> edgeMapping = new HashMap();

    public TEDMapping(HashMap<N, N> nodeMapping, HashMap<E, E> edgeMapping) {
        //make a copy
        this.nodeMapping.putAll(nodeMapping);
        this.edgeMapping.putAll(edgeMapping);
    }

    private TEDMapping() {
    }

    public N getMappedNode(N n) {
        return nodeMapping.get(n);
    }

    public E getMappedEdge(E e) {
        return edgeMapping.get(e);
    }

    public TEDMapping inverse() {

        HashMap<N, N> invertedNodes = new HashMap();
        for (Entry<N, N> nMap : nodeMapping.entrySet()) {
            invertedNodes.put(nMap.getValue(), nMap.getKey());
        }

        HashMap<E, E> invertedEdges = new HashMap();
        for (Entry<E, E> eMap : edgeMapping.entrySet()) {
            invertedEdges.put(eMap.getValue(), eMap.getKey());
        }

        TEDMapping invertedTED = new TEDMapping();
        invertedTED.nodeMapping = invertedNodes;
        invertedTED.edgeMapping = invertedEdges;
        return invertedTED;
    }

}
