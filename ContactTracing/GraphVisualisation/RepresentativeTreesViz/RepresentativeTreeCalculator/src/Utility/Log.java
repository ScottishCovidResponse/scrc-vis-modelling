/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utility;

import java.util.HashSet;

/**
 *
 * @author MaxSondag
 */
public class Log {

    private static boolean PRINTPROGRESS = true;

    private static HashSet<String> messagesSend = new HashSet();

    public static void printOnce(String message) {
        if (!messagesSend.contains(message)) {
            messagesSend.add(message);
            System.out.println(message);
        }
    }

    public static void printProgress(String message) {
        if (PRINTPROGRESS) {
            System.out.println(message);
        }

    }

    private static long lastTime = System.currentTimeMillis();

    /**
     * Prints the message only if there has not been another message with a
     * delay in the last {@code delay} milliseconds
     *
     * @param message
     * @param delay
     */
    public static void printProgress(String message, int delay) {
        long currentTime = System.currentTimeMillis();
        double difference = currentTime - lastTime;
        if ((currentTime - lastTime) > delay) {
            printProgress(message + ". Time since last message:" + difference);
            lastTime = currentTime;
        }

    }
}
