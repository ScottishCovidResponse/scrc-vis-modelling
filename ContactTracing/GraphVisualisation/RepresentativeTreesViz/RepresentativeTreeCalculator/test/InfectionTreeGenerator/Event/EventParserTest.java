/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfectionTreeGenerator.Event;

import InfectionTreeGenerator.Event.EventParser;
import InfectionTreeGenerator.Event.VirusEvent;
import InfectionTreeGenerator.Event.AlertEvent;
import InfectionTreeGenerator.Event.InfectionEvent;
import InfectionTreeGenerator.Event.Event;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static testUtility.Utlity.checkCollectionContentEqual;

/**
 *
 * @author MaxSondag
 */
public class EventParserTest {

    public EventParserTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws IOException {

        String content = "\"time\",\"eventType\",\"id\",\"newStatus\",\"additionalInfo\"";                          //header
        content += "\n0.0,\"InfectionEvent\",1,\"EXPOSED\",\"This case was an initial infection\"";                 //root infection
        content += "\n0.0,\"InfectionEvent\",2,\"EXPOSED\",\"This case was an initial infection\"";                 //root infection
        content += "\n0.5,\"VirusEvent\",1,\"PRESYMPTOMATIC\",\"Old Status : EXPOSED\"";                            //Exposed => presymptomatic
        content += "\n0.5,\"VirusEvent\",2,\"ASYMPTOMATIC\",\"Old Status : EXPOSED\"";                              //Exposed => asymptomatic
        content += "\n1.5,\"InfectionEvent\",3,\"EXPOSED\",\"This case was due to contact with 1 at time = 2\"";  //Normal infection
        content += "\n2.0,\"VirusEvent\",1,\"SYMPTOMATIC\",\"Old Status : PRESYMPTOMATIC\"";                        //PRESYMPTOMATIC => Symptomatic
        content += "\n2.0,\"AlertEvent\",1,\"REQUESTED_TEST\",\"\"";                                                //Alert event: Request test
        content += "\n2.0,\"AlertEvent\",2,\"REQUESTED_TEST\",\"\"";                                                //Alert event: Request test
        content += "\n2.5,\"AlertEvent\",1,\"AWAITING_RESULT\",\"\"";                                               //Alert event: Awaiting result
        content += "\n3.0,\"AlertEvent\",2,\"AWAITING_RESULT\",\"\"";                                               //Alert event: Awaiting result
        content += "\n4.0,\"AlertEvent\",1,\"TESTED_POSITIVE\",\"\"";                                               //Alert event: Tested positive
        content += "\n4.5,\"AlertEvent\",2,\"TESTED_NEGATIVE\",\"\"";                                               //Alert event: Tested negative
        content += "\n5.0,\"VirusEvent\",1,\"RECOVERED\",\"Old Status : SYMPTOMATIC\"";                             //symptomatic => recovered
        content += "\n5.5,\"VirusEvent\",2,\"RECOVERED\",\"Old Status : ASYMPTOMATIC\"";                            //asymptomatic => recovered
        content += "\n6.0,\"VirusEvent\",3,\"PRESYMPTOMATIC\",\"Old Status : EXPOSED\"";                            //Exposed => presymptomatic
        content += "\n6.5,\"VirusEvent\",3,\"SYMPTOMATIC\",\"Old Status : PRESYMPTOMATIC\"";                        //presymptomatic => symptomatic
        content += "\n7.0,\"VirusEvent\",3,\"SEVERELY_SYMPTOMATIC\",\"Old Status : SYMPTOMATIC\"";                  //symptomatic => Severly symptomatic
        content += "\n7.5,\"VirusEvent\",3,\"DEAD\",\"Old Status : SEVERELY_SYMPTOMATIC\"";                         //Severly symptomatic => dead

        Files.writeString(Paths.get("testEvents.csv"), content);
    }

    @After
    public void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get("testEvents.csv"));
    }

    /**
     * Test of getEvents method, of class EventParser.
     */
    @Test
    public void testGetEvents() throws Exception {
        System.out.println("getEvents");

        EventParser instance = new EventParser("testEvents.csv");

        Event e1 = new InfectionEvent(1, null, 0.0, "EXPOSED", "This case was an initial infection");
        Event e2 = new InfectionEvent(2, null, 0.0, "EXPOSED", "This case was an initial infection");
        Event e3 = new VirusEvent(1, 0.5, "PRESYMPTOMATIC", "Old Status : EXPOSED");
        Event e4 = new VirusEvent(2, 0.5, "ASYMPTOMATIC", "Old Status : EXPOSED");
        Event e5 = new InfectionEvent(3, 1, 1.5, "EXPOSED", "This case was due to contact with 1 at time = 2");//time mapping is weird in input file. Extra time should be ignored
        Event e6 = new VirusEvent(1, 2.0, "SYMPTOMATIC", "Old Status : PRESYMPTOMATIC");
        Event e7 = new AlertEvent(1, 2.0, "REQUESTED_TEST", "");
        Event e8 = new AlertEvent(2, 2.0, "REQUESTED_TEST", "");
        Event e9 = new AlertEvent(1, 2.5, "AWAITING_RESULT", "");
        Event e10 = new AlertEvent(1, 3.0, "AWAITING_RESULT", "");
        Event e11 = new AlertEvent(1, 4.0, "TESTED_NEGATIVE", "");
        Event e12 = new AlertEvent(2, 4.5, "REQUESTED_TEST", "");

        Event e13 = new VirusEvent(1, 5.0, "RECOVERED", "Old Status : SYMPTOMATIC");
        Event e14 = new VirusEvent(2, 5.5, "RECOVERED", "Old Status : ASYMPTOMATIC");
        Event e15 = new VirusEvent(3, 6.0, "PRESYMPTOMATIC", "Old Status : EXPOSED");
        Event e16 = new VirusEvent(3, 6.5, "SYMPTOMATIC", "Old Status : PRESYMPTOMATIC");
        Event e17 = new VirusEvent(3, 7.0, "SEVERELY_SYMPTOMATIC", "Old Status : SYMPTOMATIC");
        Event e18 = new VirusEvent(3, 7.5, "DEAD", "Old Status : SEVERELY_SYMPTOMATIC");

        List<Event> expResults = Arrays.asList(e1, e2, e3, e4, e5, e6, e7, e8, e9, e10, e11, e12, e13, e14, e15, e16, e17, e18);
        List<Event> results = instance.getEvents();

        for (int i = 0; i < results.size(); i++) {
            Event expResult = expResults.get(i);
            Event result = results.get(i);
            checkEventEqual(expResult, result);
        }

    }

    private void checkEventEqual(Event expectedEvent, Event event) {
        if (event.getClass() == InfectionEvent.class) {
            checkInfectionEventEqual((InfectionEvent) expectedEvent, (InfectionEvent) event);
        }
    }

    private void checkInfectionEventEqual(InfectionEvent ie1, InfectionEvent ie2) {
        assertEquals(ie1.nodeId, ie2.nodeId);
        assertEquals(ie1.sourceId, ie2.sourceId);
        assertEquals(ie1.time, ie2.time, 1E-7);
        assertEquals(ie1.newStatus, ie2.newStatus);
        assertEquals(ie1.additionalInfo, ie2.additionalInfo);
    }

}
