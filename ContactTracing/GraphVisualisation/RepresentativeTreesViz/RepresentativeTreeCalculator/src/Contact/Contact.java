package Contact;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author MaxSondag
 */
public class Contact {

    public double time;
    public int startNodeId;
    public int endNodeId;
    public int weight;
    public String location;

    public Contact(String line) {
        String[] split = line.split(",");
        time = Double.parseDouble(split[0]);
        startNodeId = Integer.parseInt(split[1]);
        endNodeId = Integer.parseInt(split[2]);
        weight = Integer.parseInt(split[3]);
        location = split[4];
    }

    public Contact(double time, int startNodeId, int endNodeId, String location) {
        this.time = time;
        this.startNodeId = startNodeId;
        this.endNodeId = endNodeId;
        this.location = location;
    }

    /**
     * returns a new contact with start and end inverted
     *
     * @return
     */
    public Contact invert() {

        return new Contact(time, endNodeId, startNodeId, location);
    }

}
