/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Export;

import Import.RealData.MetaData;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author MaxSondag
 */
public class CsvToTypeFormatter {

    public static void main(String args[]) {
    }

    List<String> nodeMetaDataFileContent;
    List<String> edgeMetaDataFileContent;

    HashMap<String, List<MetaData>> metaDataByMd5Hash = new HashMap();

    public CsvToTypeFormatter() throws IOException {
        nodeMetaDataFileContent = Files.readAllLines(Paths.get("E:\\TTP\\TTPDataPlus\\Wales_TTP_data_cases_contacts.csv"));
        edgeMetaDataFileContent = Files.readAllLines(Paths.get("E:\\TTP\\TTPDataPlus\\Wales_TTP_data_exposures.csv"));

        parseNodes(nodeMetaDataFileContent);
    }

    private void parseNodes(List<String> nodeMetaDataFileContent) {
        for (int i = 1; i < nodeMetaDataFileContent.size(); i++) {
            String line = nodeMetaDataFileContent.get(i);
            String[] fields = line.split(",");

            MetaData md5Hash = new MetaData("CaseID", "String", fields[0]);
            MetaData testDate = new MetaData("testDate", "String", fields[3]);
            MetaData testResult = new MetaData("CaseID", "String", fields[4]);
            MetaData location = new MetaData("CaseID", "String", fields[5]);

        }
    }

}
