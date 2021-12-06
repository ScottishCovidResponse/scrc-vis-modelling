/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Import.RealData;

import Export.Json.TreeNodeJson;
import InfectionTreeGenerator.Graph.ContactData.ContactNode;
import InfectionTreeGenerator.Graph.Tree;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;

/**
 *
 * @author MaxSondag
 */
class ForestReader {

    Set<Tree> readForest(String forestLocation, String nodeMetaDataLocation) throws FileNotFoundException {

        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new FileReader(nodeMetaDataLocation));
        ContactNode[] nodes = gson.fromJson(reader, ContactNode[].class);

        System.out.println("read " + nodes.length + " nodes");

        reader = new JsonReader(new FileReader(forestLocation));
        TreeNodeJson[] trees = gson.fromJson(reader, TreeNodeJson[].class);

        System.out.println("read " + trees.length + " trees");
        for (TreeNodeJson t : trees) {
            if (t.children != null) {
                System.out.println("correct");
            }
        }
        return null;
    }

}
