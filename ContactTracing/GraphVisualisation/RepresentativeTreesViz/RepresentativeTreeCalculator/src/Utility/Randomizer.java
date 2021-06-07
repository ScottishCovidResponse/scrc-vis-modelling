/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utility;

import java.util.Random;

/**
 *
 * @author MaxSondag
 */
public class Randomizer {

    private static Random random = new Random(42);

    public static int getRandomInt(int min, int max) {
        return min + random.nextInt(max - min);
    }

    public static double getRandomDouble() {
        return random.nextDouble();
    }
}
