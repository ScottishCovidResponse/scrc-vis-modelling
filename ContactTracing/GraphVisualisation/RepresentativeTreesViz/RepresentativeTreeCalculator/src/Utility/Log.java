/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utility;

import java.util.HashMap;
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

    /**
     * Prints the message only if there has not been another message with a
     * delay in the last {@code delay} milliseconds. Defaults to channel 0
     *
     * @param message
     * @param delay
     */
    public static void printProgress(String message, int delay) {
        printProgress(message, 0, delay);
    }

    private static HashMap<Integer, Long> lastTimePerChannel = new HashMap();

    /**
     * Prints the message only if there has not been another message with a
     * delay in the last {@code delay} milliseconds on channel with id
     * {@code channelId}
     *
     * @param message
     * @param delay
     */
    public static void printProgress(String message, int channelId, int delay) {
        long currentTime = System.currentTimeMillis();
        long lastTime = lastTimePerChannel.getOrDefault(channelId, 0l);

        double difference = currentTime - lastTime;
        if ((currentTime - lastTime) > delay) {
            printProgress(message + ". Time since last message:" + difference);
            lastTimePerChannel.put(channelId, currentTime);
        }
    }

}
