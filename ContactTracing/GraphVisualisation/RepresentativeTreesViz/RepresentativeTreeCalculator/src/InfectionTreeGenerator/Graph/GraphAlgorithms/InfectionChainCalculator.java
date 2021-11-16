/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfectionTreeGenerator.Graph.GraphAlgorithms;

import InfectionTreeGenerator.Graph.ContactData.ContactEdge;
import InfectionTreeGenerator.Graph.ContactData.ContactGraph;
import InfectionTreeGenerator.Graph.ContactData.ContactNode;
import InfectionTreeGenerator.Graph.Edge;
import InfectionTreeGenerator.Graph.Graph;
import InfectionTreeGenerator.Graph.Infection.InfectionEdge;
import InfectionTreeGenerator.Graph.Infection.InfectionGraph;
import InfectionTreeGenerator.Graph.Infection.InfectionNode;
import InfectionTreeGenerator.Graph.Node;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Calculates the edge directions for a connected graph to hold the most likely
 * way an infection spread
 *
 * @author MaxSondag
 */
public class InfectionChainCalculator {

    private ContactGraph g;
    /**
     * How long a person is infectious after being exposed to the diseasee. Used
     * to configure weights. Around 10 for covid, but can be longer. Default to
     * 16 to be save and include the incubation time.
     */
    private double infectiousPeriod;

    //make the folder in the current working directory.
    private String tempFolder = System.getProperty("user.dir") + "/temporary";
    private String javaOutputEdgeFilePrefix = tempFolder + "/edge";
    private String javaOutputNodeFilePrefix = tempFolder + "/node";

    private String pythonOutputFolderPrefix = tempFolder + "/chain";//own folder
    private String pythonOutputFilePrefix = pythonOutputFolderPrefix + "/chain";//own folder

    /**
     * Holds where the python program to compute the infection chains is stored.
     */
    private String pythonProgramLocation = "F:/Development/Swansea/scrc-vis-modelling/ContactTracing/GraphVisualisation/RepresentativeTreesViz/RepresentativeTreeCalculator/reconstructingEpidemic/experiments/main.py";

    /**
     *
     * @param g
     */
    public InfectionChainCalculator(ContactGraph g) {
        this.g = g;
        this.infectiousPeriod = 16;
    }

    /**
     *
     * @param g
     * @param infectiousPeriod
     */
    public InfectionChainCalculator(ContactGraph g, double infectiousPeriod) {
        this.g = g;
        this.infectiousPeriod = infectiousPeriod;
    }

    public InfectionGraph calculateInfectionGraph() {
        File f = new File(tempFolder);
        f.mkdir();
        f.deleteOnExit();

        f = new File(pythonOutputFolderPrefix);
        f.mkdir();
        f.deleteOnExit();

        int componentCount = writeComponentFiles();
        executeProgram(componentCount);
        InfectionGraph g = parseOutputFiles();

        return g;
    }

    /**
     * Writes an outputfile for each seperate component of the graph and returns
     * how many components there are
     */
    private int writeComponentFiles() {
        Set<ContactNode> nodesHandled = new HashSet();
        int componentNumber = 0;//how many components we have already processed
        for (ContactNode n : g.getNodes()) {
            if (nodesHandled.contains(n)) {
                continue;//already have the component containing n
            }
            Collection<ContactNode> componentNodes = g.getReachableNodes(n);
            Set<ContactEdge> componentEdges = new HashSet();
            for (ContactNode node : componentNodes) {
                componentEdges.addAll(node.edges);
            }

            writeComponentFiles(g, componentNodes, componentEdges, componentNumber);
            nodesHandled.addAll(componentNodes);

            componentNumber++;
        }

        return componentNumber;
    }

    private void writeComponentFiles(ContactGraph g, Collection<ContactNode> nodes, Collection<ContactEdge> edges, int componentNumber) {

        try {
            writeEdgeFile(g, nodes, edges, componentNumber);
            writeNodeFile(g, nodes, edges, componentNumber);
        } catch (IOException ex) {
            Logger.getLogger(InfectionChainCalculator.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void writeEdgeFile(ContactGraph g, Collection<ContactNode> nodes, Collection<ContactEdge> edges, int componentNumber) throws IOException {

        //Need to sort the edges by time for the file. 
        List<ContactEdge> sortedEdges = new ArrayList();
        sortedEdges.addAll(edges);
        Collections.sort(sortedEdges, (a, b) -> Double.compare(a.contactTime, b.contactTime));

        //add content line by line
        boolean first = true;
        String edgeFileContent = "";
        for (ContactEdge e : sortedEdges) {
            String time = "" + ((int) e.contactTime);
            String nodeId1 = "" + e.source.id;
            String nodeId2 = "" + e.target.id;
            String weight = getWeight(g, e);
            //make sure no extra newline is present
            if (first) {
                first = false;
            } else {
                edgeFileContent += "\n";
            }
            edgeFileContent += time + "\t" + nodeId1 + "\t" + nodeId2 + "\t" + weight;
        }

        String fileName = javaOutputEdgeFilePrefix + componentNumber + ".tsv";
        Files.write(Paths.get(fileName), edgeFileContent.getBytes());
        new File(fileName).deleteOnExit();
    }

    private void writeNodeFile(ContactGraph g, Collection<ContactNode> nodes, Collection<ContactEdge> edges, int componentNumber) throws IOException {

        //Need to sort the nodes by reporting timefor the file. 
        List<ContactNode> sortedNodes = new ArrayList();
        sortedNodes.addAll(nodes);

        ////first put all nodes with a known testing time
        List<ContactNode> timeStampedNodes = sortedNodes.stream()
                .filter(n -> n.positiveTestTime != null)
                .sorted((a, b) -> Double.compare(a.positiveTestTime, b.positiveTestTime))
                .collect(Collectors.toList());

        //add content line by line
        String nodeFileContent = "";
        boolean first = true;
        for (ContactNode n : timeStampedNodes) {
            String nodeId = "" + n.id;
            String testTime = "" + (n.positiveTestTime);
            //make sure no extra newline is present at the start
            if (first) {
                first = false;
            } else {
                nodeFileContent += "\n";//put this on a newline
            }
            nodeFileContent += nodeId + "\t" + testTime;
        }

        //Add the remainder of the nodes that do not have a test time.
        List<ContactNode> unknownNodes = sortedNodes.stream()
                .filter(n -> n.positiveTestTime == null)
                .collect(Collectors.toList());

        //add content line by line
        for (ContactNode n : unknownNodes) {
            String nodeId = "" + n.id;
            nodeFileContent += "\n" + nodeId; //no test time given, so leave it blank
        }

        String fileName = javaOutputNodeFilePrefix + componentNumber + ".tsv";
        Files.write(Paths.get(fileName), nodeFileContent.getBytes());
        new File(fileName).deleteOnExit();
    }

    private String getWeight(ContactGraph g, ContactEdge e) {

        Long sourceTestTime = e.source.positiveTestTime;
        Long targetTestTime = e.target.positiveTestTime;
        double type = e.weight;

        //If test times are unknown, set them to the maximum considered value
        Double edgeWeight = (double) infectiousPeriod;
        if (sourceTestTime != null && targetTestTime != null) {
            //get the difference in days
            double diff = (targetTestTime - sourceTestTime) / (60 * 60 * 24);
            //diff can be negative in which case it is going from a later test to an earlier. Much more unlikely so we penalize it
            if (diff < 0) {
                diff = -diff * 2;//invert and multiply difference by two
            }
            //Constrain value to [1,abs(diff),infectiousperiod]
            edgeWeight = Math.max(1, Math.min(infectiousPeriod, Math.abs(diff)));
        }

        return "" + edgeWeight;
    }

    private void executeProgram(int componentCount) {

        //for every component we execute the python program and wait for it to complete before proceeding to not hog compute resources
        for (int i = 0; i < componentCount; i++) {

            String nodeFileName = javaOutputNodeFilePrefix + i + ".tsv";
            String edgeFileName = javaOutputEdgeFilePrefix + i + ".tsv";
            String outputFileName = pythonOutputFilePrefix + i + ".txt";

            try {

                ProcessBuilder pb = new ProcessBuilder().inheritIO().command("python",
                        pythonProgramLocation,
                        "-n", nodeFileName,
                        "-e", edgeFileName,
                        "-o", outputFileName);

                Process p = pb.start();
                int exitCode = p.waitFor();

                assert (0 == exitCode);//no errors should occur
            } catch (InterruptedException | IOException ex) {
                Logger.getLogger(InfectionChainCalculator.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        //delete when done so we can delete the folder
        for (File f : new File(pythonOutputFolderPrefix).listFiles()) {
            f.deleteOnExit();
        }

    }

    private InfectionGraph parseOutputFiles() {

        //get all the chain files, they all end with .txt and are the only ones to do so
        File folder = new File(pythonOutputFolderPrefix);
        File[] listFiles = folder.listFiles();

        InfectionGraph g = new InfectionGraph();
        for (File f : listFiles) {
            try {
                parseOutputFile(g, f);
            } catch (IOException ex) {
                Logger.getLogger(InfectionChainCalculator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return g;
    }

    private void parseOutputFile(InfectionGraph g, File f) throws IOException {
        List<String> lines = Files.readAllLines(f.toPath());
        for (String line : lines) {
            String[] split = line.split("\\(");
            for (int i = 1; i < split.length; i++)//start at 1 as first is empty
            {
                String edgeString = split[i];
                edgeString = edgeString.replace(" ", ""); //remove white spaces
                edgeString = edgeString.replace(")", "");//remove ) still present

                String[] values = edgeString.split(",");

                double timestamp = Double.parseDouble(values[0]);
                int id1 = Integer.parseInt(values[1]);
                int id2 = Integer.parseInt(values[2]);

                //add the nodes first to ensure they exists. 
                //Node n1 only has this timestamp if it's a root node. Otherwise it's already set elsewhere.
                InfectionNode n1 = new InfectionNode(id1, timestamp);
                g.addNodeIfNotpresent(n1);

                InfectionNode n2 = new InfectionNode(id2, timestamp);
                g.addNodeIfNotpresent(n2);

                if (id1 != id2) {
                    //add the edge if it is not a self-edge
                    InfectionEdge e = new InfectionEdge(g.getNode(id1), g.getNode(id2), timestamp);
                    g.addEdgeIfNotPresent(e);
                }
            }

        }
    }

}
