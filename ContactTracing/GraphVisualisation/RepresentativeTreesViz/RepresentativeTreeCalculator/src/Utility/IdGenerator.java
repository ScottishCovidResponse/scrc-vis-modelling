/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utility;

/**
 * Not thread safe
 *
 * @author MaxSondag
 */
public class IdGenerator {

    private IdGenerator() {
    }

    private static int id = 0;

    /**
     * Not thread safe. Returns an unique id that increments
     *
     * @return
     */
    public static int getId() {
        id += 1;
        return id;
    }
}
