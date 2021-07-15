/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfectionTreeGenerator.Graph.ContactData;

import Import.RealData.MetaData;
import InfectionTreeGenerator.Graph.Edge;
import InfectionTreeGenerator.Graph.Node;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author MaxSondag
 */
public class ContactEdge extends Edge<ContactNode> {

    public double contactTime;
    public ArrayList<MetaData> metaDataList;

    public ContactEdge(ContactNode start, ContactNode target, double contactTime, int infectionChanceWeight) {
        super(start, target);
        this.contactTime = contactTime;
        this.weight = infectionChanceWeight;
    }

    public void setMetaData(ArrayList<MetaData> metaDataList) {
        this.metaDataList = metaDataList;
    }

    @Override
    public String toString() {
        return "Edge{" + "start=" + source.id + ", end=" + target.id + ", time=" + contactTime + ", weight=" + weight + "}";
    }

    @Override
    public String toJson() {
        return "{\"sourceId\":" + source.id + ","
                + "\"targetId\":" + target.id + ","
                + "\"contactTime\":" + contactTime + ","
                + "\"weight\":" + weight
                + "}";
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 43 * hash + Objects.hashCode(this.source);
        hash = 43 * hash + Objects.hashCode(this.target);
        hash = 43 * hash + Objects.hashCode(this.contactTime);
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
        final ContactEdge other = (ContactEdge) obj;
        if (!Objects.equals(this.source, other.source)) {
            return false;
        }
        if (!Objects.equals(this.target, other.target)) {
            return false;
        }
        if (!Objects.equals(this.contactTime, other.contactTime)) {
            return false;
        }
        return true;
    }

    @Override
    public Edge deepCopy(HashMap<Integer, ContactNode> newNodes) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
