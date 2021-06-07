
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
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
public class AgeParser {

    public HashMap<Integer, Integer> agePerId = new HashMap();

    public AgeParser(String fileLocation) throws IOException {
        parseAges(Files.readAllLines(Paths.get(fileLocation)));
    }

    private void parseAges(List<String> lines) {
        for (int i = 1; i < lines.size(); i++)//skip header
        {
            String line = lines.get(i);
            int id = Integer.parseInt(line.split(",")[0]);
            int age = Integer.parseInt(line.split(",")[1]);
            agePerId.put(id, age);
        }
    }

}
