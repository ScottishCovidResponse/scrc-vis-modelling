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
import Utility.Log;
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

    /**
     * Holds the most likely infectiongraph
     */
    private InfectionGraph mostLikelyInfectionGraph = new InfectionGraph();

    private ContactGraph contactGraph;
    /**
     * How long a person is infectious after being exposed to the diseasee. Used
     * to configure weights. Around 10 for covid, but can be longer. Default to
     * 16 to be save and include the incubation time.
     */
    private double infectiousPeriod;

    //make the folder in the current working directory.
    private String tempFolder;
    private String javaOutputEdgeFilePrefix;
    private String javaOutputNodeFilePrefix;

    private String pythonOutputFolderPrefix;
    private String pythonOutputFilePrefix;

    /**
     * Holds where the python program to compute the infection chains is stored.
     */
    private String pythonProgramLocation = "F:/Development/Swansea/scrc-vis-modelling/ContactTracing/GraphVisualisation/RepresentativeTreesViz/RepresentativeTreeCalculator/reconstructingEpidemic/experiments/main.py";

    /**
     *
     * @param g
     */
    public InfectionChainCalculator(ContactGraph g, String dataFolderLocation) {
        this.contactGraph = g;
        this.infectiousPeriod = 16;
        this.tempFolder = dataFolderLocation + "/temporary";
        this.javaOutputEdgeFilePrefix = tempFolder + "/edge";
        this.javaOutputNodeFilePrefix = tempFolder + "/node";
        this.pythonOutputFolderPrefix = tempFolder + "/chain";//own folder
        this.pythonOutputFilePrefix = pythonOutputFolderPrefix + "/chain";//own folder
    }

    /**
     *
     * @param g
     * @param infectiousPeriod
     */
    public InfectionChainCalculator(ContactGraph g, double infectiousPeriod) {
        this.contactGraph = g;
        this.infectiousPeriod = infectiousPeriod;
    }

    public InfectionGraph calculateInfectionGraph(boolean programAlreadyExecuted) {
        File f = new File(tempFolder);
        f.mkdir();
//        f.deleteOnExit();

        f = new File(pythonOutputFolderPrefix);
        f.mkdir();
//        f.deleteOnExit();

        Set<ContactNode> nodesHandled = solveTrivialComponents();

        if (programAlreadyExecuted == false) {
            System.out.println("Start writing files");
            //write a temporary file for each non-trivial component. Directly add trivial components to the graph
//            int componentCount = writeComponentFiles(nodesHandled);
//            //calculate most-likely-infection chains for each component. TODO: Allow for multiple index cases per component?
//            executeProgram(componentCount);
        }
        System.out.println("Start parsing output files");
        //Add the most-likely-infection chains to the mostLikelyInfectionGraph
        parseOutputFiles();
        System.out.println("output files parsed");
        return mostLikelyInfectionGraph;
    }

    private Set<ContactNode> solveTrivialComponents() {
        Set<ContactNode> nodesHandled = new HashSet();
        int trivialComponentNumber = 0;//how many components of size 1 we processed

        for (ContactNode n : contactGraph.getNodes()) {
            if (nodesHandled.contains(n)) {
                continue;//already have the component containing n
            }
            Collection<ContactNode> componentNodes = contactGraph.getReachableNodes(n);
            if (componentNodes.size() > 2) {//not a trivial component
                continue;
            }

            if (componentNodes.size() == 1) {
                mostLikelyInfectionGraph.addNode(new InfectionNode(n.id, n.positiveTestTime));
            }
            if (componentNodes.size() == 2) {
                //get the othernode and the componentEdges
                Set<ContactEdge> componentEdges = new HashSet();
                ContactNode otherN = null;
                for (ContactNode cn : componentNodes) {
                    componentEdges.addAll(cn.edges);
                    if (cn != n) {
                        otherN = cn;
                    }
                }

                //add the infectionNodes
                InfectionNode iN = new InfectionNode(n.id, n.positiveTestTime);
                InfectionNode iOtherN = new InfectionNode(otherN.id, otherN.positiveTestTime);
                mostLikelyInfectionGraph.addNode(iN);
                mostLikelyInfectionGraph.addNode(iOtherN);

                //use the earliest edge that is a contact for the graph
                long earliestContact = Long.MAX_VALUE;
                for (ContactEdge ce : componentEdges) {
                    earliestContact = Math.min(earliestContact, ce.contactTime);
                }

                if (n.positiveTestTime < otherN.positiveTestTime) {
                    mostLikelyInfectionGraph.addEdge(new InfectionEdge(iN, iOtherN, earliestContact));
                } else {
                    mostLikelyInfectionGraph.addEdge(new InfectionEdge(iOtherN, iN, earliestContact));
                }
            }

            trivialComponentNumber++;
            nodesHandled.addAll(componentNodes);
            Log.printProgress("trivial component number: " + trivialComponentNumber + " is handled", 1, 1000);
        }

        System.out.println(trivialComponentNumber + " total trivial components handled");
        return nodesHandled;
    }

    /**
     * Writes an outputfile for each seperate component of the graph and returns
     * how many components there are
     *
     * @param nodesHandled Which nodes are already handled when parsing trivial
     * components
     */
    private int writeComponentFiles(Set<ContactNode> nodesHandled) {
        System.out.println("TODO: Need to print all edges between them and take that into account in the program if possible. Likely with weight");

        int componentNumber = 0;//how many components we have written files for processed
        int trivalComponentNumber = 0;//how many components of size 1 we processed

        for (ContactNode n : contactGraph.getNodes()) {
            if (nodesHandled.contains(n)) {
                continue;//already have the component containing n
            }
            Collection<ContactNode> componentNodes = contactGraph.getReachableNodes(n);
            Set<ContactEdge> componentEdges = new HashSet();
            for (ContactNode node : componentNodes) {
                componentEdges.addAll(node.edges);
            }

            writeComponentFiles(contactGraph, componentNodes, componentEdges, componentNumber);
            nodesHandled.addAll(componentNodes);

            componentNumber++;
            Log.printProgress("component number: " + componentNumber + " is written", 1000);
        }
        return componentNumber;
    }

    private void writeComponentFiles(ContactGraph g, Collection<ContactNode> nodes, Collection<ContactEdge> edges, int componentNumber) {

        try {
            String nodeFileName = javaOutputNodeFilePrefix + componentNumber + ".tsv";
            String edgeFileName = javaOutputEdgeFilePrefix + componentNumber + ".tsv";

            writeNodeFile(g, nodes, edges, componentNumber, nodeFileName);
            writeEdgeFile(g, nodes, edges, componentNumber, edgeFileName);

        } catch (IOException ex) {
            Logger.getLogger(InfectionChainCalculator.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void writeEdgeFile(ContactGraph g, Collection<ContactNode> nodes, Collection<ContactEdge> edges, int componentNumber, String fileName) throws IOException {

//        System.out.println("TODO: Need to print all edges between them and take that into account in the program if possible");
        //Need to sort the edges by time for the file.
        List<ContactEdge> sortedEdges = new ArrayList();
        sortedEdges.addAll(edges);
        Collections.sort(sortedEdges, (a, b) -> Double.compare(a.contactTime, b.contactTime));

        StringBuilder edgeFileContent = new StringBuilder();
        //add content line by line
        boolean first = true;
        for (ContactEdge e : sortedEdges) {
            String time = "" + ((int) e.contactTime);
            String nodeId1 = "" + e.source.id;
            String nodeId2 = "" + e.target.id;
            String weight = getWeight(g, e);
            //make sure no extra newline is present
            if (first) {
                first = false;
            } else {
                edgeFileContent.append("\n");
            }
            edgeFileContent.append(time);
            edgeFileContent.append("\t");
            edgeFileContent.append(nodeId1);
            edgeFileContent.append("\t");
            edgeFileContent.append(nodeId2);
            edgeFileContent.append("\t");
            edgeFileContent.append(weight);
        }

        byte[] bytes = edgeFileContent.toString().getBytes();
        Files.write(Paths.get(fileName), bytes);
        File f = new File(fileName);
//        f.deleteOnExit();
    }

    private void writeNodeFile(ContactGraph g, Collection<ContactNode> nodes, Collection<ContactEdge> edges, int componentNumber, String fileName) throws IOException {

        //Need to sort the nodes by reporting timefor the file.
        List<ContactNode> sortedNodes = new ArrayList();
        sortedNodes.addAll(nodes);

        ////first put all nodes with a known testing time
        List<ContactNode> timeStampedNodes = sortedNodes.stream()
                .filter(n -> n.positiveTestTime != null)
                .sorted((a, b) -> Double.compare(a.positiveTestTime, b.positiveTestTime))
                .collect(Collectors.toList());

        //add content line by line
        StringBuilder nodeFileContent = new StringBuilder();
        boolean first = true;
        for (ContactNode n : timeStampedNodes) {
            String nodeId = "" + n.id;
            String testTime = "" + (n.positiveTestTime);
            //make sure no extra newline is present at the start
            if (first) {
                first = false;
            } else {
                nodeFileContent.append("\n");//put this on a newline
            }
            nodeFileContent.append(nodeId);
            nodeFileContent.append("\t");
            nodeFileContent.append(testTime);
        }

        //Add the remainder of the nodes that do not have a test time.
        List<ContactNode> unknownNodes = sortedNodes.stream()
                .filter(n -> n.positiveTestTime == null)
                .collect(Collectors.toList());

        //add content line by line
        for (ContactNode n : unknownNodes) {
            String nodeId = "" + n.id;
            nodeFileContent.append("\n");
            nodeFileContent.append(nodeId); //no test time given, so leave it blank
        }

        byte[] bytes = nodeFileContent.toString().getBytes();

        Files.write(Paths.get(fileName), bytes);
        File f = new File(fileName);
//        f.deleteOnExit();
    }

    private String getWeight(ContactGraph g, ContactEdge e) {
        //TODO: Encode weight by type of edge. Not yet in the data.
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

        //TODO: Speed up python. Likely need to make sure that it is not starting up over and over but batch processing.
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
                Logger.getLogger(InfectionChainCalculator.class
                        .getName()).log(Level.SEVERE, null, ex);
                System.out.println("Error for file: " + nodeFileName);
            }

        }

        //delete when done so we can delete the folder
        for (File f : new File(pythonOutputFolderPrefix).listFiles()) {
//            f.deleteOnExit();
        }

    }

    private void parseOutputFiles() {

        //get all the chain files, they all end with .txt and are the only ones to do so
        File folder = new File(pythonOutputFolderPrefix);
        File[] listFiles = folder.listFiles();

        for (File f : listFiles) {
            try {
                parseOutputFile(f);
            } catch (IOException ex) {
                Logger.getLogger(InfectionChainCalculator.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void parseOutputFile(File f) throws IOException {
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
                mostLikelyInfectionGraph.addNodeIfNotpresent(n1);

                InfectionNode n2 = new InfectionNode(id2, timestamp);
                mostLikelyInfectionGraph.addNodeIfNotpresent(n2);

                if (id1 != id2) {
                    //add the edge if it is not a self-edge
                    //Need to use getNode as nodes may already have existed.
                    InfectionEdge e = new InfectionEdge(mostLikelyInfectionGraph.getNode(id1), mostLikelyInfectionGraph.getNode(id2), timestamp);
                    mostLikelyInfectionGraph.addEdgeIfNotPresent(e);
                }
            }
        }
    }

}
