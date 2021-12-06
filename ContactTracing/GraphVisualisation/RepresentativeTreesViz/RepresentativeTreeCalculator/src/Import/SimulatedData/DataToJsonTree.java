package Import.SimulatedData;

import Contact.Contact;
import Contact.ContactParser;
import Policy.PolicySimulator;
import Export.GraphWriter;
import Export.SimMetaDataWriter;
import Policy.Policy;
import InfectionTreeGenerator.Event.Event;
import InfectionTreeGenerator.Event.EventParser;
import InfectionTreeGenerator.Graph.GraphAlgorithms.DistanceMeasures.RtDistanceMeasure;
import InfectionTreeGenerator.Graph.GraphAlgorithms.DistanceMeasures.TreeDistanceMeasure;
import InfectionTreeGenerator.Graph.GraphAlgorithms.ForestFinder;
import InfectionTreeGenerator.Graph.GraphAlgorithms.RepresentativeTree.RepresentativeTreesFinder;
import InfectionTreeGenerator.Graph.Infection.InfectionGraph;
import InfectionTreeGenerator.Graph.Infection.InfectionNode;
import InfectionTreeGenerator.Graph.Node;
import InfectionTreeGenerator.Graph.Tree;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
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
public class DataToJsonTree {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            String baseFolderLocation = "./Data/demoMultiScenario0";
            //String baseFolderLocation = "./Data/2021-01-06/DataForTreeReduction";

            String inputFolderLocation = baseFolderLocation + "/input";
            String outputFileLocation = baseFolderLocation + "/output";

            int startTreeSize = 1;//calculate starting from trees of size 1
            int endTreeSize = 2000; //stop calculating for trees of size 200

            new DataToJsonTree(inputFolderLocation, outputFileLocation, startTreeSize, endTreeSize);
        } catch (IOException ex) {
            System.out.println("Invalid input or outputFileLocation");
            Logger.getLogger(DataToJsonTree.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    final private String inputFolderLocation;
    final private String outputFileLocation;

    private DataToJsonTree(String inputFolderLocation, String outputFileLocation, int startTreeSize, int endTreeSize) throws IOException {
        this.inputFolderLocation = inputFolderLocation;
        this.outputFileLocation = outputFileLocation;
        System.out.println("Working on data from: " + inputFolderLocation);
        //read data
        List<String> infectionMap = Files.readAllLines(Paths.get(inputFolderLocation + "/infectionMap.txt"));
        InfectionMapParser imp = new InfectionMapParser(infectionMap);
        imp.constructGraph();

        InfectionGraph ig = imp.ig;
        //parse the events
        EventParser ep = new EventParser(inputFolderLocation + "/events.csv");
        List<Event> events = ep.getEvents();
        ig.addEventData(events);//add event to the data

        ContactParser cp = new ContactParser(inputFolderLocation + "/contacts_covid_model.csv");
//        ContactParser cp = new ContactParser(inputFolderLocation + "/contacts_pop_20000.csv");
        HashMap<Integer, Set<Contact>> contacts = cp.getContacts();

        addLocationData(ig, contacts);

        PolicySimulator ps = new PolicySimulator(ig, events, contacts);
        ps.applyAllPolicies();//apply all policies to ig
        List<Policy> policies = ps.getAppliedPolicies();
        System.out.println("g.getNodes().size() = " + ig.getNodes().size());
        System.out.println("g.getEdges().size() = " + ig.getEdges().size());

        AgeParser ap = new AgeParser(inputFolderLocation + "/ids_paul.csv");
//        AgeParser ap = new AgeParser(inputFolderLocation + "/people_pop_20000.csv");
        addAgeData(ig, ap);

        //output data
        printStatistics(contacts, ig);

        GraphWriter tw = new GraphWriter();
        tw.writeInfectionGraph(outputFileLocation + "/NodesAndMeta.json", ig);
//
//        SimMetaDataWriter gmdw = new SimMetaDataWriter(ig, events, contacts, policies);
//        gmdw.writeMetaDataFile(outputFileLocation + "/SimMeta.json");

        System.out.println("Finding the forest");
        ForestFinder ff = new ForestFinder(ig, Tree.class);
        Set<Tree> forest = ff.getForest();

        tw.writeForest(outputFileLocation + "/AllTrees.json", forest);
        TreeDistanceMeasure tdm = new RtDistanceMeasure(1);
        RepresentativeTreesFinder rgf = new RepresentativeTreesFinder();
        rgf.getAndWriteRepresentativeTreeData(forest, startTreeSize, endTreeSize, tdm, outputFileLocation + "/ReptreesRTDistance/");
    }

    private void printStatistics(HashMap<Integer, Set<Contact>> contacts, InfectionGraph ig) {
        Set<Contact> allContacts = new HashSet();
        for (Set<Contact> c : contacts.values()) {
            allContacts.addAll(c);
        }

        System.out.println("Amount of contacts: " + allContacts.size());
        System.out.println("Amount of nodes: " + contacts.keySet().size());
        System.out.println("Amount of infected nodes: " + ig.getNodes().size());
    }

    private void addLocationData(InfectionGraph ig, HashMap<Integer, Set<Contact>> contacts) {

        for (InfectionNode in : ig.getNodes()) {
            double exposedTime = in.exposedTime;
            int infectorId = in.sourceInfectionId;

            if (in.id == 10951) {
                System.out.println("");
            }

            if (in.sourceInfectionId == in.id) {//initial infection, no location
                in.infectionLocation = "initial";
                continue;
            }
            Set<Contact> inContacts = contacts.get(in.id);

            //get contact that was the source of the exposure
            Contact c = getExposureContact(infectorId, exposedTime, inContacts);
            in.infectionLocation = c.location;
        }
    }

    private Contact getExposureContact(int infectorId, double exposedTime, Set<Contact> contactsFromNode) {
        for (Contact c : contactsFromNode) {
            //contact time is an integer, exposed time is split into half day times.
            if (c.endNodeId == infectorId && (c.time == exposedTime || c.time == (exposedTime - 0.5))) {
                return c;
            }
        }

        throw new IllegalStateException("No valid contact present");
    }

    private void addAgeData(InfectionGraph ig, AgeParser ap) {
        for (InfectionNode in : ig.getNodes()) {
            in.age = ap.agePerId.get(in.id);
        }
    }
}
