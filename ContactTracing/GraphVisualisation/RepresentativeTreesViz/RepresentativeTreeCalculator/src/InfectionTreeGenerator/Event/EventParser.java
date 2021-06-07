/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfectionTreeGenerator.Event;

import InfectionTreeGenerator.Graph.Infection.InfectionGraph;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author MaxSondag
 */
public class EventParser {

    final String fileLocation;

    public EventParser(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    public List<Event> getEvents() throws IOException {

        List<Event> events = new ArrayList();
        List<String> lines = Files.readAllLines(Paths.get(fileLocation));
        boolean header = true;
        for (String line : lines) {
            if (header) {//skip the header
                header = false;
                continue;
            }
            Event e = parseEvent(line);
            events.add(e);
        }

        return events;
    }

    private Event parseEvent(String line) {
        Event e = null;
        try {
            line = line.replaceAll("\"", "");//replace extra "";
            String[] split = line.split(",");
            double time = Double.parseDouble(split[0]);

            String eventType = split[1];
            int nodeId = Integer.parseInt(split[2]);
            String newStatus = split[3];
            String additionalInfo;
            if (split.length >= 5) {//in case of an alertEvent, there is no additional infor
                additionalInfo = split[4];
            } else {
                additionalInfo = "";
            }

            switch (eventType) {
                case "InfectionEvent":
                    Integer sourceId = getSourceId(line);
                    e = new InfectionEvent(nodeId, sourceId, time, newStatus, additionalInfo);
                    break;
                case "AlertEvent":
                    e = new AlertEvent(nodeId, time, newStatus, additionalInfo);
                    break;
                case "VirusEvent":
                    e = new VirusEvent(nodeId, time, newStatus, additionalInfo);
                    break;
            }

        } catch (Exception ex) {
            System.err.println("Input line is malformed and cannot be parsed: [" + line + "]");
            Logger.getLogger(EventParser.class
                    .getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }

        return e;
    }

    private Integer getSourceId(String line) {
        String targetString = "This case was due to contact with ";

        if (!line.contains(targetString)) {//root node instead
            return null;
        }
        //throw away preface of id

        line = line.substring(line.indexOf(targetString) + targetString.length());
        //parse targetId
        Integer sourceId = Integer.parseInt(line.substring(0, line.indexOf(" ")));
        return sourceId;
    }

    public void addEventData(InfectionGraph g, List<Event> events) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
