package Contact;

import Utility.Log;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author MaxSondag
 */
public class ContactParser {

    HashMap<Integer, Set<Contact>> contactsFromUser = new HashMap();

    public ContactParser(String fileLocation) throws IOException {
        parseContacts(Files.readAllLines(Paths.get(fileLocation)));
    }

    private void parseContacts(List<String> lines) {
        for (int i = 1; i < lines.size(); i++)//skip header
        {
            Contact c = new Contact(lines.get(i));
            addToContactList(c);
            //inverted contact occurs as well
            addToContactList(c.invert());
        }
    }

    public HashMap<Integer, Set<Contact>> getContacts() {
        return contactsFromUser;
    }

    private void addToContactList(Contact c) {
        //using list to ensure there is a fixed order,
        Set contactList = contactsFromUser.getOrDefault(c.startNodeId, new HashSet());
        contactList.add(c);
        contactsFromUser.put(c.startNodeId, contactList);
    }
}
