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
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
    HashMap<String, Integer> instanceIdtoId = new HashMap();
    int currentIdNumber = 0; //start numbering them from 0 onwards

    public ContactGraphParser(String nodeFileLocation, String edgeFileLocation) throws IOException {
        edgeFileContent = Files.readAllLines(Paths.get(edgeFileLocation));
        nodeFileContent = Files.readAllLines(Paths.get(nodeFileLocation));
    }

    public ContactGraph constructGraph() {
        ContactGraph cg = constructGraph(Integer.MAX_VALUE);
        return cg;
    }

    public ContactGraph constructGraph(int maxLinesToProcess) {
        //we can process less lines for debugging purposes;
        int linesToProcess = Math.min(nodeFileContent.size(), maxLinesToProcess);

        //create the nodes of the graph firsts
        for (int i = 1; i < linesToProcess; i++)//skip headers
        {
            String line = nodeFileContent.get(i);
            line = line.replace("\"", "");//remove extra " in the data.
            if (line.length() == 0) {
                System.err.println("Node file has and empty line at line number " + i + ". skipping line");
                continue;
            }
            String[] split = line.split(",", -1);//-1 to catch trailing ","
            try {
                createNode(split);
            } catch (Exception e) {
                System.err.println("line " + line + "could not be parsed");
                e.printStackTrace();
                System.exit(-1);
            }
        }

        linesToProcess = Math.min(edgeFileContent.size(), maxLinesToProcess);
        //create the edges of the graph
        for (int i = 1; i < linesToProcess; i++)//skip headers
        {
            String line = edgeFileContent.get(i);
            line = line.replace("\"", "");//remove extra " in the data.
            if (line.length() == 0) {
                System.err.println("Edge file has and empty line at line number " + i + ". skipping line");
                continue;
            }

            String[] split = line.split(",", -1);//-1 to catch trailing ","
            //create the edge with metadata
            try {
                createEdge(split);
            } catch (Exception e) {
                System.err.println("line " + line + "could not be parsed");
                e.printStackTrace();
                System.exit(-1);
            }
        }

        return g;
    }

    private void createNode(String[] split) {
        String instanceId = split[2];
        String testTime = split[5];
        String testResult = split[6];
        String location = split[7];

        int id = getIdFromInstanceId(instanceId);
        assert (g.getNode(id) == null);

        if (testTime.isBlank()) {//testing time is empty, won't create node
            return;
        }

        ContactNode n = new ContactNode(id);
        n.md5Hash = instanceId;
        n.location = location;

        //add the location
        if ("Positive".equals(testResult)) {
            //only add a node if it was involved in a positive test

            long unixTestTime = TimeFunctions.dateToUnixTimestamp(testTime);
            //Might be multiple tests, set it to the first one
            n.setTestTime(unixTestTime);

            g.addNode(n);
        }
    }

    private void createEdge(String[] split) {

        String exposureId = split[0];
        
        String indexInstanceId = split[3];
        String contactInstanceId = split[6];
        String contactTime = split[8];
        
        
        //We ignore edges that do not have id's for both endpoints
        if (indexInstanceId.isBlank() || contactInstanceId.isBlank()) {
            return;
        }

        int id1 = getIdFromInstanceId(indexInstanceId);
        int id2 = getIdFromInstanceId(contactInstanceId);
        int weight = 1; //TODO: weight is not yet in data

        ContactNode n1 = g.getNode(id1);
        ContactNode n2 = g.getNode(id2);

        //Add 1 to it's contact count of each existing node
        if (n1 != null) {
            n1.addContactCount();
        }
        if (n2 != null) {
            n2.addContactCount();
        }

        if (contactTime.isBlank()) {//no time of contact
            return;
        }
        if (n1 == null || n2 == null) {
            //One of the nodes never tested positive, don't add an edge
            return;
        }

        if (id1 == id2) {
            //we do not do self edges
            System.out.println("Self edge in the data with hash" + exposureId);
            return;
        }

        long unixContactTime = TimeFunctions.dateToUnixTimestamp(contactTime);

        ContactEdge e = new ContactEdge(n1, n2, unixContactTime, weight);
        g.addEdge(e);

        //need bidirectional edges
        ContactEdge eI = new ContactEdge(n2, n1, unixContactTime, weight);
        g.addEdge(eI);
    }

    public void addMetaDataFiles(String nodeMetaDataFileLocation, String edgeMetaDataFileLocation) throws IOException {
        edgeMetaDataFileContent = Files.readAllLines(Paths.get(edgeMetaDataFileLocation));
        nodeMetaDataFileContent = Files.readAllLines(Paths.get(nodeMetaDataFileLocation));

        edgeHeaders = edgeFileContent.get(0).split(",");
        edgeDataType = edgeFileContent.get(1).split(",");

        nodeHeaders = nodeFileContent.get(0).split(",");
        nodeDataType = nodeFileContent.get(1).split(",");
    }

    private int getIdFromInstanceId(String caseId) {
        if (instanceIdtoId.containsKey(caseId)) {
            return instanceIdtoId.get(caseId);
        } else {
            instanceIdtoId.put(caseId, currentIdNumber);
        }
        currentIdNumber++;
        return currentIdNumber - 1;//just increased by 1
    }

}
