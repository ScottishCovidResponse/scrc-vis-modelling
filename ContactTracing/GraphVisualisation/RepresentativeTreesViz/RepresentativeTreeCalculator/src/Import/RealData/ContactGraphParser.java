/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Import.RealData;

import InfectionTreeGenerator.Graph.ContactData.ContactEdge;
import InfectionTreeGenerator.Graph.ContactData.ContactGraph;
import InfectionTreeGenerator.Graph.ContactData.ContactNode;
import InfectionTreeGenerator.Graph.Graph;
import InfectionTreeGenerator.Graph.Node;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author MaxSondag
 */
public class ContactGraphParser {

    List<String> edgeFileContent;
    List<String> nodeFileContent;
    ContactGraph g = new ContactGraph();

    String[] edgeHeaders;
    String[] edgeDataType;

    String[] nodeHeaders;
    String[] nodeDataType;

    public ContactGraphParser(String nodeFileLocation, String edgeFileLocation) throws IOException {
        edgeFileContent = Files.readAllLines(Paths.get(edgeFileLocation));
        nodeFileContent = Files.readAllLines(Paths.get(nodeFileLocation));

        edgeHeaders = edgeFileContent.get(0).split(",");
        edgeDataType = edgeFileContent.get(1).split(",");

        nodeHeaders = nodeFileContent.get(0).split(",");
        nodeDataType = nodeFileContent.get(1).split(",");
    }

    public ContactGraph constructGraph() {

        //create the nodes of the graph firsts
        for (int i = 2; i < nodeFileContent.size(); i++)//skip headers
        {
            String line = nodeFileContent.get(i);
            
            if (line.length() == 0) {
                System.err.println("Node file has and empty line at line number "+i+". skipping line");
                continue;
            }
            String[] split = line.split(",");
            createNode(split);
        }

        //create the edges of the graph
        for (int i = 2; i < edgeFileContent.size(); i++)//skip headers
        {
            String line = edgeFileContent.get(i);
            if (line.length() == 0) {
                System.err.println("Edge file has and empty line at line number "+i+". skipping line");
                continue;
            }

            String[] split = line.split(",");
            //create the edge with metadata
            createEdge(split);
        }

        return g;
    }

    private void createNode(String[] split) {
        int id = Integer.parseInt(split[0]);
        assert (g.getNode(id) == null);

        ContactNode n = new ContactNode(id);
        g.addNode(n);

        if (!split[1].isBlank()) {
            //if this node has a time where it is tested positive
            int testTime = Integer.parseInt(split[1]);
            n.setTestTime(testTime);
        }

        //add metadata
        ArrayList<MetaData> metaDataList = new ArrayList();

        for (int i = 2; i < split.length; i++) {
            String header = nodeHeaders[i];
            String dataType = nodeDataType[i];
            String valueString = split[i];
            MetaData md = new MetaData(header, dataType, valueString);
            metaDataList.add(md);
        }

        n.setMetaData(metaDataList);

    }

    private void createEdge(String[] split) {
        int id1 = Integer.parseInt(split[0]);
        int id2 = Integer.parseInt(split[1]);
        long time = Long.parseLong(split[2]);
        int weight = Integer.parseInt(split[3]);

        ContactNode n1 = g.getNode(id1);
        ContactNode n2 = g.getNode(id2);
        ContactEdge e = new ContactEdge(n1, n2, time, weight);
        g.addEdge(e);

        //add metadata
        ArrayList<MetaData> metaDataList = new ArrayList();

        for (int i = 4; i < split.length; i++) {
            String header = edgeHeaders[i];
            String dataType = edgeDataType[i];
            String valueString = split[i];
            MetaData md = new MetaData(header, dataType, valueString);
            metaDataList.add(md);
        }

        e.setMetaData(metaDataList);

    }

}
