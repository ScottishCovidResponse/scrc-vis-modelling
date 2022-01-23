package Import.RealData;

import Import.SimulatedData.AgeParser;
import Import.SimulatedData.InfectionMapParser;
import Import.SimulatedData.DataToJsonTree;
import Contact.Contact;
import Contact.ContactParser;
import Export.GraphWriter;
import Export.Json.JsonMerger;
import Export.SimMetaDataWriter;
import InfectionTreeGenerator.Event.Event;
import InfectionTreeGenerator.Event.EventParser;
import InfectionTreeGenerator.Graph.ContactData.ContactEdge;
import InfectionTreeGenerator.Graph.ContactData.ContactGraph;
import InfectionTreeGenerator.Graph.ContactData.ContactNode;
import InfectionTreeGenerator.Graph.Graph;
import InfectionTreeGenerator.Graph.GraphAlgorithms.DistanceMeasures.RtDistanceMeasure;
import InfectionTreeGenerator.Graph.GraphAlgorithms.DistanceMeasures.TreeDistanceMeasure;
import InfectionTreeGenerator.Graph.GraphAlgorithms.ForestFinder;
import InfectionTreeGenerator.Graph.GraphAlgorithms.InfectionChainCalculator;
import InfectionTreeGenerator.Graph.GraphAlgorithms.RepresentativeTree.RepresentativeTreesFinder;
import InfectionTreeGenerator.Graph.Infection.InfectionGraph;
import InfectionTreeGenerator.Graph.Infection.InfectionNode;
import InfectionTreeGenerator.Graph.Node;
import InfectionTreeGenerator.Graph.Tree;
import Policy.Policy;
import Policy.PolicySimulator;
import Utility.Log;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author MaxSondag
 */
public class RealDataParser {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            System.out.println("TODO: Parse rom arguments");
            String inputFolderLocation = "E:/TTP/RawData";
            String outputFileLocation = "E:/TTP/ProcessedData";

            int startTreeSize = 1;//calculate starting from trees of size 1
            int endTreeSize = 2000; //stop calculating for trees of size 2000

            int timeWindowSize = 60 * 60 * 24;//Time window of 1 day

            /**
             * Whether we have already created all the components files. Note,
             * this does not mean we have calculated all infections trees.
             */
            boolean chainsAlreadyGenerated = true;

            RealDataParser rdp = new RealDataParser(inputFolderLocation, outputFileLocation, startTreeSize, endTreeSize, timeWindowSize);
            rdp.parseData(chainsAlreadyGenerated);
//            rdp.parseTreeData(outputFileLocation + "/NodesAndMeta.json", outputFileLocation + "/AllTrees.json");
        } catch (IOException ex) {
            System.out.println("Invalid input or outputFileLocation");
            Logger.getLogger(DataToJsonTree.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    final private String inputFolderLocation;
    final private String outputFileLocation;

    int startTreeSize, endTreeSize;
    int timeWindowSize;

    protected RealDataParser(String inputFolderLocation, String outputFileLocation, int startTreeSize, int endTreeSize, int timeWindowSize) throws IOException {
        this.inputFolderLocation = inputFolderLocation;
        this.outputFileLocation = outputFileLocation;
        this.startTreeSize = startTreeSize;
        this.endTreeSize = endTreeSize;
        this.timeWindowSize = timeWindowSize;
    }

    public void parseData(boolean chainsAlreadyGenerated) throws IOException {
        System.out.println("Working on data from: " + inputFolderLocation);

        //gets the structure of the graph and the associated metadata
        ContactGraphParser gp = new ContactGraphParser(inputFolderLocation + "/Wales_TTP_data_cases_contacts_v2.csv", inputFolderLocation + "/Wales_TTP_data_exposures_v2.csv");
//        gp.addMetaDataFiles(inputFolderLocation + "/NodeData.csv", inputFolderLocation + "/ContactEdgeData.csv");

        ContactGraph cg = gp.constructGraph();
//        ContactGraph cg = gp.constructGraph(50000);//run it with a limited set of lines to read, should make sure the program actually finished

        cg.printStatistics();

        cg.addContactsAmountToMetadata();//add the amount of contacts to the metadata?
        Log.printProgress("Calculate most likely infection chain");
        InfectionChainCalculator icc = new InfectionChainCalculator(cg, inputFolderLocation);
        InfectionGraph ig = icc.calculateInfectionGraph(chainsAlreadyGenerated);//if chains are already geneerated, no need to execute the program again. Just read files
        System.out.println("Finished calculating most likely infection chain");

        addSourceIdToMetaData(cg, ig);
//
        ig.printStatistics();
//
        //write the metadata of the nodes that are infected to a json file
        GraphWriter gw = new GraphWriter();
        List<ContactNode> exposedContactNodes = cg.getContactNodesInInfectionGraph(cg, ig);
        gw.writeContactData(outputFileLocation + "/NodesAndMeta.json", exposedContactNodes);
        //        tw.writeInfectionGraph(outputFileLocation + "/NodesAndMeta.json", ig);

        Log.printProgress("Finding the forest");
        ForestFinder ff = new ForestFinder(ig, Tree.class);
        Set<Tree> forest = ff.getForest();

        //write the forest to a file. Note that AllTrees only contains infected nodes
        gw.writeForest(outputFileLocation + "/AllTrees.json", forest);

        printForestStatistics(forest);

        calculateRepTrees(forest);
    }

    public void calculateRepTrees(Set<Tree> forest) throws IOException {
        //make output dir for for distances
        File f = new File(outputFileLocation + "/ReptreesRTDistance");
        f.mkdir();

        TreeDistanceMeasure tdm = new RtDistanceMeasure(timeWindowSize);
        RepresentativeTreesFinder rgf = new RepresentativeTreesFinder();
        rgf.getAndWriteRepresentativeTreeData(forest, startTreeSize, endTreeSize, tdm, outputFileLocation + "/ReptreesRTDistance/");

        //merge all the trees together in a single file.
        Log.printProgress("Merging trees");
        JsonMerger merger = new JsonMerger(outputFileLocation + "/ReptreesRTDistance/", outputFileLocation + "/RepTrees.json");
        merger.mergeTrees();
//        merger.cleanup();//delete the temporary output folder.
    }

    private void addSourceIdToMetaData(ContactGraph cg, InfectionGraph ig) {
        for (InfectionNode in : ig.getNodes()) {
            ContactNode cn = cg.getNode(in.id);
            if (cn != null) {//should only happen when debugging
                cn.sourceInfectionId = in.sourceInfectionId;
            }
        }
    }

    private void printForestStatistics(Set<Tree> forest) {
        int nodes = 0;
        int edges = 0;

        for (Tree t : forest) {
            nodes += t.getNodes().size();
            edges += t.getEdges().size();
        }
        System.out.println(forest.size() + " trees.");
        System.out.println(nodes + " nodes in total in the trees.");
        System.out.println(edges + " edges in total in the trees.");
    }

}
