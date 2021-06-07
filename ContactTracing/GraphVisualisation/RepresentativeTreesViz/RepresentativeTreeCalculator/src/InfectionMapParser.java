
import InfectionTreeGenerator.Graph.Infection.InfectionEdge;
import InfectionTreeGenerator.Graph.Infection.InfectionGraph;
import InfectionTreeGenerator.Graph.Infection.InfectionNode;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author MaxSondag
 */
class InfectionMapParser {

    InfectionGraph ig = new InfectionGraph();
    List<String> content;

    public InfectionMapParser(List<String> lines) {
        this.content = lines;
    }

    public InfectionGraph constructGraph() {
        parseData(content);
        return ig;
    }

    private void parseData(List<String> lines) {
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            line = cleanLine(line);//remove weird formatting

            if (line.isBlank()) {
                continue;//ignore blank lines
            }
            String[] structure = line.split("->");
            //either 1 or 2 arrow. If two arrows, need to skip the first arrow as this is "visual" only
            int offset = structure.length == 2 ? 0 : 1;

            String rootNode = structure[offset];
            int rootId = createNode(rootNode);

            for (String node : line.split("->")[offset + 1].split(",")) {
                int nodeId = createNode(node);
                createEdge(rootId, nodeId);
            }
        }
    }

    private int createNode(String rootNode) {
        int id = getId(rootNode);
        double time = getTime(rootNode);
        if (!ig.hasNodeWithId(id)) {
            InfectionNode n = new InfectionNode(id, time);
            ig.addNode(n);
        }
        return id;
    }

    private void createEdge(int rootId, int targetId) {
        InfectionNode root =  ig.getNode(rootId);
        InfectionNode target =  ig.getNode(targetId);
        InfectionEdge e = new InfectionEdge(root, target, target.exposedTime);
        ig.addEdge(e);
    }

    /**
     * Expects a string of format X*(Y*) where X* is the id and Y* is the
     * exposedTime. Returns X*
     *
     * @param node
     * @return
     */
    private int getId(String node) {
        return Integer.parseInt(node.split("\\(")[0]);
    }

    /**
     * Expects a string of format X*(Y*) where X* is the id and Y* is the
     * exposedTime. Returns Y*
     *
     * @param node
     * @return
     */
    private double getTime(String node) {
        return Double.parseDouble(node.split("\\(")[1].split("\\)")[0]);
    }

    private String cleanLine(String line) {
        line = removeWhitespace(line);
        line = removeSquareBrackets(line);
        return line;
    }

    private String removeWhitespace(String line) {
        return line.replaceAll("\\s+", "");
    }

    private String removeSquareBrackets(String line) {
        return line.replaceAll("\\[|\\]", "");
    }

}
