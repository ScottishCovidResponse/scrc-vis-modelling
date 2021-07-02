package Import.RealData;

import Import.SimulatedData.AgeParser;
import Import.SimulatedData.InfectionMapParser;
import Import.SimulatedData.DataToJsonTree;
import Contact.Contact;
import Contact.ContactParser;
import Export.GraphWriter;
import Export.SimMetaDataWriter;
import InfectionTreeGenerator.Event.Event;
import InfectionTreeGenerator.Event.EventParser;
import InfectionTreeGenerator.Graph.ContactData.ContactEdge;
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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

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
            String inputFolderLocation = "./Data/RealData/";
            String outputFileLocation = inputFolderLocation;

            int startTreeSize = 1;//calculate starting from trees of size 1
            int endTreeSize = 2000; //stop calculating for trees of size 2000

            new RealDataParser(inputFolderLocation, outputFileLocation, startTreeSize, endTreeSize);
        } catch (IOException ex) {
            System.out.println("Invalid input or outputFileLocation");
            Logger.getLogger(DataToJsonTree.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    final private String inputFolderLocation;
    final private String outputFileLocation;

    private RealDataParser(String inputFolderLocation, String outputFileLocation, int startTreeSize, int endTreeSize) throws IOException {
        this.inputFolderLocation = inputFolderLocation;
        this.outputFileLocation = outputFileLocation;
        System.out.println("Working on data from: " + inputFolderLocation);
        //read data

        //gets the structure of the graph. Ignore metadata
        GraphParser gp = new GraphParser(inputFolderLocation + "/ContactEdgeData.csv",inputFolderLocation + "/NodeData.csv",);
        Graph<ContactNode, ContactEdge> g = gp.constructGraph();
        
        
        System.out.println("Calculate most likely infection chain");
        InfectionChainCalculator icc = new InfectionChainCalculator(g);
        InfectionGraph ig = icc.calculateInfectionGraph(positiveTestData);

        System.out.println("Finding the forest");
        ForestFinder ff = new ForestFinder(ig, Tree.class);
        Set<Tree> forest = ff.getForest();

        //write the forest to a file
        GraphWriter tw = new GraphWriter();
        tw.writeForest(outputFileLocation + "/AllTrees.json", forest);

        System.out.println("TODO: Set time windows automatically");
        
        TreeDistanceMeasure tdm = new RtDistanceMeasure(100, 1);
        RepresentativeTreesFinder rgf = new RepresentativeTreesFinder();
        rgf.getAndWriteRepresentativeTreeData(forest, startTreeSize, endTreeSize, tdm, outputFileLocation + "/ReptreesRTDistance/");
        
        System.out.println("TODO Merge automatically");
    }

}
