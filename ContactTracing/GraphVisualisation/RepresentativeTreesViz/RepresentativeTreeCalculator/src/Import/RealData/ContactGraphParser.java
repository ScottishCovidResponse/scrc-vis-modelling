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
import Utility.TimeFunctions;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author MaxSondag
 */
public class ContactGraphParser {

    List<String> edgeFileContent;
    List<String> nodeFileContent;

    List<String> edgeMetaDataFileContent;
    List<String> nodeMetaDataFileContent;
    ContactGraph g = new ContactGraph();

    String[] edgeHeaders;
    String[] edgeDataType;

    String[] nodeHeaders;
    String[] nodeDataType;

    /**
     * For each caseid, holds the converted md5 hash to a number
     */
    HashMap<String, Integer> caseIdtoId = new HashMap();
    int currentIdNumber = 0; //start numbering them from 0 onwards

    public ContactGraphParser(String nodeFileLocation, String edgeFileLocation) throws IOException {
        edgeFileContent = Files.readAllLines(Paths.get(edgeFileLocation));
        nodeFileContent = Files.readAllLines(Paths.get(nodeFileLocation));
    }

    public ContactGraph constructGraph() {
        System.out.println("TODO: There is more data to encode from the files.");
        System.out.println("TODO: Currently only taking positive nodes and edges into account. ");
        //create the nodes of the graph firsts
        for (int i = 1; i < nodeFileContent.size(); i++)//skip headers
        {
            String line = nodeFileContent.get(i);

            if (line.length() == 0) {
                System.err.println("Node file has and empty line at line number " + i + ". skipping line");
                continue;
            }
            String[] split = line.split(",");
            createNode(split);
        }

        //create the edges of the graph
        for (int i = 1; i < edgeFileContent.size(); i++)//skip headers
        {
            String line = edgeFileContent.get(i);
            if (line.length() == 0) {
                System.err.println("Edge file has and empty line at line number " + i + ". skipping line");
                continue;
            }

            String[] split = line.split(",");
            //create the edge with metadata
            createEdge(split);
        }

        return g;
    }

    private void createNode(String[] split) {
        int id = getIdFromCaseId(split[0]);
        assert (g.getNode(id) == null);

        ContactNode n = new ContactNode(id);
        g.addNode(n);

        if (!split[3].isBlank()) {//testing time is not empty
            if ("\"Positive\"".equals(split[4])) {
                //if this node has a time where it is tested positive
                long testTime = TimeFunctions.dateToUnixTimestamp(split[3]);
                n.setTestTime(testTime);
            }
        }

        //add metadata
//        ArrayList<MetaData> metaDataList = new ArrayList();        
//        
//        for (int i = 2; i < split.length; i++) {
//            String header = nodeHeaders[i];
//            String dataType = nodeDataType[i];
//            String valueString = split[i];
//            MetaData md = new MetaData(header, dataType, valueString);
//            metaDataList.add(md);
//        }
//
//        n.setMetaData(metaDataList);
    }

    private void createEdge(String[] split) {

        int id1 = getIdFromCaseId(split[2]);
        int id2 = getIdFromCaseId(split[3]);
        long time = TimeFunctions.dateToUnixTimestamp(split[4]);
        int weight = 1; //TODO: weight is not yet in data

        ContactNode n1 = g.getNode(id1);
        ContactNode n2 = g.getNode(id2);

        if (n1 == null || n2 == null) {
            //not both tested positive
            return;
        }

        ContactEdge e = new ContactEdge(n1, n2, time, weight);
        g.addEdge(e);

//
//        //add metadata
//        ArrayList<MetaData> metaDataList = new ArrayList();
//
//        for (int i = 4; i < split.length; i++) {
//            String header = edgeHeaders[i];
//            String dataType = edgeDataType[i];
//            String valueString = split[i];
//            MetaData md = new MetaData(header, dataType, valueString);
//            metaDataList.add(md);
//        }
//
//        e.setMetaData(metaDataList);
    }

    public void addMetaDataFiles(String nodeMetaDataFileLocation, String edgeMetaDataFileLocation) throws IOException {
        edgeMetaDataFileContent = Files.readAllLines(Paths.get(edgeMetaDataFileLocation));
        nodeMetaDataFileContent = Files.readAllLines(Paths.get(nodeMetaDataFileLocation));

        edgeHeaders = edgeFileContent.get(0).split(",");
        edgeDataType = edgeFileContent.get(1).split(",");

        nodeHeaders = nodeFileContent.get(0).split(",");
        nodeDataType = nodeFileContent.get(1).split(",");
    }

    private int getIdFromCaseId(String caseId) {
        if (caseIdtoId.containsKey(caseId)) {
            return caseIdtoId.get(caseId);
        } else {
            caseIdtoId.put(caseId, currentIdNumber);
        }
        currentIdNumber++;
        return currentIdNumber - 1;//just increased by 1
    }

}
