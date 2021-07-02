/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfectionTreeGenerator.Graph.ContactData;

import InfectionTreeGenerator.Graph.Node;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author MaxSondag
 */
public class ContactNode extends Node<ContactEdge> {

    /**
     * Holds when this node was exposed
     */
    public Double positiveTestTime;

    public ContactNode(int id) {
        super(id);
    }

    public void setTestTime(double positiveTestTime) {
        this.positiveTestTime = positiveTestTime;
    }

}
