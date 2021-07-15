/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfectionTreeGenerator.Graph.ContactData;

import Import.RealData.MetaData;
import InfectionTreeGenerator.Graph.Node;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author MaxSondag
 */
public class ContactNode extends Node<ContactEdge> {

    /**
     * Holds when this node was exposed. Time is unix timestamp
     */
    public Integer positiveTestTime;

    public ArrayList<MetaData> metaDataList;
    
    public ContactNode(int id) {
        super(id);
    }

    public void setTestTime(int positiveTestTime) {
        this.positiveTestTime = positiveTestTime;
    }

    public void setMetaData(ArrayList<MetaData> metaDataList) {
        this.metaDataList = metaDataList;
    }

}
