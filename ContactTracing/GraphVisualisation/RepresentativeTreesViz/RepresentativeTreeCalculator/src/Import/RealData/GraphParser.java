/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Import.RealData;

import InfectionTreeGenerator.Graph.ContactData.ContactEdge;
import InfectionTreeGenerator.Graph.ContactData.ContactNode;
import InfectionTreeGenerator.Graph.Graph;
import InfectionTreeGenerator.Graph.Node;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author MaxSondag
 */
public class GraphParser {
    
    List<String> edgeFileContent;
    List<String> nodeFileContent;
    Graph g = new Graph<ContactNode, ContactEdge>();
    
    String[] edgeHeaders;
    String[] edgeDataType;
    
    String[] nodeHeaders;
    String[] nodeDataType;
    
    public GraphParser(String edgeFileLocation, String nodeFileLocation) throws IOException {
        edgeFileContent = Files.readAllLines(Paths.get(edgeFileLocation));
        nodeFileContent = Files.readAllLines(Paths.get(nodeFileLocation));
        
        edgeHeaders = edgeFileContent.get(0).split(",");
        edgeDataType = edgeFileContent.get(1).split(",");
        
        nodeHeaders = nodeFileContent.get(0).split(",");
        nodeDataType = nodeFileContent.get(1).split(",");
    }
    
    public Graph constructGraph() {
        
        for (int i = 2; i < edgeFileContent.size(); i++)//skip headers
        {
            String line = edgeFileContent.get(i);
            String[] split = line.split(",");
            int id1 = Integer.parseInt(split[0]);
            int id2 = Integer.parseInt(split[1]);
            
            createNodeIfNotExists(id1);
            createNodeIfNotExists(id2);
            //create the edge with metadata
            createEdge(split);
        }
        
        //set positive test times
        for (int i = 2; i < nodeFileContent.size(); i++)//skip headers
        {
            String line = nodeFileContent.get(i);
            String[] split = line.split(",");
            int id = Integer.parseInt(split[0]);
            long time = Long.parseLong(split[1]);
            ((ContactNode) g.getNode(id)).setTestTime(time);
        }
        return g;
    }
    
    private void createNodeIfNotExists(int id) {
        Node n = new Node(id);
        g.addNode(n);
    }
    
    private void createEdge(String[] split) {
        int id1 = Integer.parseInt(split[0]);
        int id2 = Integer.parseInt(split[1]);
        long time = Long.parseLong(split[2]);
        int weight = Integer.parseInt(split[3]);
        
        for (int i = 4; i < split.length; i++) {
            Node n1 = g.getNode(id1);
            Node n2 = g.getNode(id2);
            ContactEdge ce = new ContactEdge(n1, n2, time, weight);
            g.addEdge(ce);
        }
    }
    
}
