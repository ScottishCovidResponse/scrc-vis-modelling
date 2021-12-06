/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Import.RealData;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.json.JSONException;

/**
 *
 * @author MaxSondag
 */
public class RealDataParserTest {

    String inputFolderLocation = "./Data/testSet/TestRealData/";
    String outputFileLocation = "./Data/testSet/output/";

    public RealDataParserTest() {
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
        //delete the files once we are done so it doesn't clotter new tests
        new File(outputFileLocation + "RepTrees.json").deleteOnExit();
        new File(outputFileLocation + "NodesAndMeta.json").deleteOnExit();
        new File(outputFileLocation + "AllTrees.json").deleteOnExit();
    }

    /**
     * Test of parseData method, of class RealDataParser.
     */
    @Test
    public void testParseData() throws Exception {
        System.out.println("parseData: Checking that simple input case generates expected output");

        int startTreeSize = 1;//calculate starting from trees of size 1
        int endTreeSize = 2000; //stop calculating for trees of size 2000

        RealDataParser instance = new RealDataParser(inputFolderLocation, outputFileLocation, startTreeSize, endTreeSize);
        instance.parseData(false);

        //verify that the json is correct. Does not care about order of objects
        verifyJson(inputFolderLocation + "RepTrees.json", outputFileLocation + "RepTrees.json");
        verifyJson(inputFolderLocation + "NodesAndMeta.json", outputFileLocation + "NodesAndMeta.json");
        verifyJson(inputFolderLocation + "AllTrees.json", outputFileLocation + "AllTrees.json");

    }

    /**
     * Verifies that the json of the two folders is identical. Does not care
     * about the order in the array
     *
     * @param testFileLocation
     * @param outputFileLocation
     * @throws IOException
     * @throws JSONException
     */
    private void verifyJson(String testFileLocation, String outputFileLocation) throws IOException, JSONException {
        String testJson = Files.readString(Paths.get(testFileLocation));
        String outputJson = Files.readString(Paths.get(outputFileLocation));
        JSONAssert.assertEquals(testJson, outputJson, false);//allows swapping of order
    }

}
