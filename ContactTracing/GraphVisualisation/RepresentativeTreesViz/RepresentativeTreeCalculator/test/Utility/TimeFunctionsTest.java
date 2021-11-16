/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utility;

import Import.RealData.ContactGraphParser;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author MaxSondag
 */
public class TimeFunctionsTest {

    public TimeFunctionsTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of dateToUnixTimestamp method, of class TimeFunctions.
     */
    @Test
    public void testDateToUnixTimestamp() {
        System.out.println("dateToUnixTimestamp");
        String date = "2021-11-11";
        long expResult = 1636588800;
        long result = TimeFunctions.dateToUnixTimestamp(date);
        
        assertEquals(result,expResult);
    }

}
