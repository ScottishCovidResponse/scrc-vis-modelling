/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsonmerger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 * @author MaxSondag
 */
public class JsonMerger {

    String inputFolderLocation;
    String outputFileLocation;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here

        String inputFolderLocation = "F:\\Development\\Swansea\\scrc-vis-modelling\\ContactTracing\\GraphVisualisation\\RepresentativeTreesViz\\RepresentativeTreeCalculator\\Data\\demoMultiScenario0\\output\\ReptreesRTDistance";
        String outputFileLocation = "F:\\Development\\Swansea\\scrc-vis-modelling\\ContactTracing\\GraphVisualisation\\RepresentativeTreesViz\\RepresentativeTreeCalculator\\Data\\demoMultiScenario0\\output\\RepTreesRTDistanceFull.json";

        JsonMerger jm = new JsonMerger(inputFolderLocation, outputFileLocation);
//        jm.mergeNodesAndEdges();
        jm.mergeTrees();

    }

    public JsonMerger(String inputFolderLocation, String outputFileLocation) {
        this.inputFolderLocation = inputFolderLocation;
        this.outputFileLocation = outputFileLocation;
    }

    public void mergeTrees() throws IOException {
        File inputFolder = new File(inputFolderLocation);

        StringBuilder sb = new StringBuilder();
        sb.append("[");

        File[] listFiles = inputFolder.listFiles();

        Arrays.sort(listFiles, new Comparator<File>() {
            public int compare(File f1, File f2) {
                // Intentional: Reverse order for this demo
                String f1Path = f1.getAbsolutePath();
                String f2Path = f2.getAbsolutePath();

                int treeSizef1 = getTreeSize(f1Path);
                int treeSizef2 = getTreeSize(f2Path);

                return Integer.compare(treeSizef1, treeSizef2);

            }

            private int getTreeSize(String path) {
                String end = path.substring(path.lastIndexOf("\\")+1);
                String number = end.substring(0,end.indexOf(".json"));
                return Integer.parseInt(number);
            }
        });

        for (int i = 0; i < listFiles.length; i++) {
            File f = listFiles[i];
            assert (f.getName().endsWith(".json"));
            String fullContent = Files.readAllLines(f.toPath()).get(0);//only a single line in the file
            String content = fullContent.substring(1, fullContent.length() - 1);//remove first [ and last ]

            sb.append(content);
            if (i != (listFiles.length - 1)) {
                sb.append(",");//no comma after content of last file
            }
        }

        sb.append("]");
        Files.writeString(Paths.get(outputFileLocation), sb.toString());

    }

    public void mergeNodesAndEdges() throws IOException {

        String mergedNodeContent = "";
        String mergedEdgeContent = "";

        File inputFolder = new File(inputFolderLocation);

        File[] listFiles = inputFolder.listFiles();
        for (File f : listFiles) {
            assert (f.getName().endsWith(".json"));
            String content = Files.readAllLines(f.toPath()).get(0);//only a single line in the file
            String nodeContent = getNodeContent(content);
            String edgeContent = getEdgeContent(content);

            if (!nodeContent.isEmpty()) {
                if (!mergedNodeContent.isEmpty()) {
                    mergedNodeContent += ",";
                }
                mergedNodeContent += nodeContent;
            }
            if (!edgeContent.isEmpty()) {
                if (!mergedEdgeContent.isEmpty()) {
                    mergedEdgeContent += ",";
                }
                mergedEdgeContent += edgeContent;
            }
        }

        String outputString = "{\"nodes\":[";
        outputString += mergedNodeContent;
        outputString += "],\"links\":[";
        outputString += mergedEdgeContent;
        outputString += "]}";

        Files.writeString(Paths.get(outputFileLocation), outputString);
    }

    private String getNodeContent(String content) {
        int startOfNodesArray = content.indexOf("[") + 1;
        int endOfNodesArray = content.indexOf("],\"links");
        String nodeContent = content.substring(startOfNodesArray, endOfNodesArray);
        return nodeContent;
    }

    private String getEdgeContent(String content) {
        int startOfNodesArray = content.indexOf("links\":[") + 8;
        int endOfNodesArray = content.length() - 2;
        String edgeContent = content.substring(startOfNodesArray, endOfNodesArray);
        return edgeContent;
    }

}
