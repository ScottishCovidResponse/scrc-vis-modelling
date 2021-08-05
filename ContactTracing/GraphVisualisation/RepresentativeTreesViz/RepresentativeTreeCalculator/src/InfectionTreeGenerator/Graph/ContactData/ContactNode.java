/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfectionTreeGenerator.Graph.ContactData;

import Import.RealData.MetaData;
import InfectionTreeGenerator.Graph.Node;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author MaxSondag
 */
public class ContactNode extends Node<ContactEdge> {

    /**
     * Holds when this node was exposed. Time is unix timestamp
     */
    public Integer positiveTestTime;

    /**
     * Null is unknown or it does not have one
     */
    public Integer sourceInfectionId;

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

    public Set<ContactNode> getUniqueContacts() {
        //find the nodes that node n has been in contact with
        Set<ContactNode> nodesInContact = new HashSet();
        for (ContactEdge e : edges) {
            nodesInContact.add(e.source);
            nodesInContact.add(e.target);
        }
        //remove self
        nodesInContact.remove(this);

        return nodesInContact;
    }

    /**
     * Adds to the metadata of this node. Care should be taken that all nodes
     * have metadata in the same order
     *
     * @param md
     */
    protected void addToMetaData(MetaData md) {
        metaDataList.add(md);
    }


}
