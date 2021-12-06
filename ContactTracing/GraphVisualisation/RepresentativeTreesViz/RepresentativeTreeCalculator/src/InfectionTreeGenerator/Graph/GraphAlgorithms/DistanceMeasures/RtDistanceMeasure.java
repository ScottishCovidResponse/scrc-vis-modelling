/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfectionTreeGenerator.Graph.GraphAlgorithms.DistanceMeasures;

import InfectionTreeGenerator.Graph.Infection.InfectionEdge;
import InfectionTreeGenerator.Graph.Infection.InfectionNode;
import InfectionTreeGenerator.Graph.Tree;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author MaxSondag
 */
public class RtDistanceMeasure implements TreeDistanceMeasure {

    /**
     * How many timesteps we take in a single window. Note that all trees are
     * anchored at 0.
     */
    int timeWindowSize;

    public RtDistanceMeasure(int timeWindowSize) {
        this.timeWindowSize = timeWindowSize;
    }

    @Override
    public double getDistance(Tree<InfectionNode, InfectionEdge> t1, Tree<InfectionNode, InfectionEdge> t2) {
        Double[] rtT1 = getRtValuesPerStep(t1);
        Double[] rtT2 = getRtValuesPerStep(t2);

        int totalTimeWindows = Math.max(rtT1.length, rtT2.length);

        double absoluteDiff = 0;
        for (int i = 0; i < totalTimeWindows; i++) {
            double rtT1Val = 0;
            double rtT2Val = 0;
            if (i < rtT1.length) {//still has a value
                rtT1Val = rtT1[i];
            }
            if (i < rtT2.length) {//still has a value
                rtT1Val = rtT2[i];
            }
            absoluteDiff += Math.abs(rtT1Val - rtT2Val);
        }
        return absoluteDiff;
    }

    /**
     * Can be overriden to use a different method to calculate the difference
     *
     * @param t
     * @return
     */
    protected Double[] getCalculatedRtValues(Tree<InfectionNode, InfectionEdge> t) {
        return getRtValuesPerStep(t);
    }

    protected Double[] getRtValuesPerStep(Tree<InfectionNode, InfectionEdge> t) {

        int totalTimeWindows = getTotalTimeWindows(t);

        Double[] rtValues = new Double[totalTimeWindows];

        InfectionNode root = t.calculateRoot();
        double startTime = root.exposedTime;

        for (int timeWindowI = 0; timeWindowI < totalTimeWindows; timeWindowI++) {
            double timeStart = timeWindowSize * timeWindowI + startTime;
            double timeEnd = timeWindowSize * (timeWindowI + 1) + startTime;

            double futureIN = countFutureInfectedNodes(t, timeStart, timeEnd);
            double newIN = countNewInfectedNodes(t, timeStart, timeEnd);
            double rt = futureIN / newIN;
            if (newIN == 0) {//ignore divide by 0.
                assert (futureIN == 0);
                rt = 0;
            }
            rtValues[timeWindowI] = rt;
        }
        return rtValues;
    }

    protected double countFutureInfectedNodes(Tree<InfectionNode, InfectionEdge> t, double timeStart, double timeEnd) {
        Collection<InfectionNode> nodes = t.getNodes();
        int futureINCount = 0;
        for (InfectionNode n : nodes) {
            if (n.exposedTime >= timeStart && n.exposedTime < timeEnd) {
                futureINCount += t.getChildren(n).size();
            }
        }
        return futureINCount;
    }

    protected double countNewInfectedNodes(Tree<InfectionNode, InfectionEdge> t, double timeStart, double timeEnd) {
        Collection<InfectionNode> nodes = t.getNodes();
        int newINCount = 0;
        for (InfectionNode n : nodes) {
            if (n.exposedTime >= timeStart && n.exposedTime < timeEnd) {
                newINCount++;
            }
        }
        return newINCount;
    }

    protected int getTotalTimeWindows(Tree<InfectionNode, InfectionEdge> t) {

        double minExposedTime = Double.MAX_VALUE;
        double maxExposedTime = Double.MIN_VALUE;

        Collection<InfectionNode> nodes = (Collection<InfectionNode>) t.getNodes();

        for (InfectionNode iN : nodes) {
            minExposedTime = Math.min(minExposedTime, iN.exposedTime);
            maxExposedTime = Math.max(maxExposedTime, iN.exposedTime);
        }

        double range = maxExposedTime - minExposedTime;
        if (maxExposedTime == minExposedTime) {
            range = 0.01;//avoid divide by 0
        }

        int timeWindowsRequired = (int) Math.ceil(range / timeWindowSize);
        if (timeWindowsRequired == (range / timeWindowSize)) {
            timeWindowsRequired++;//Need to include the last number as well
        }
        return timeWindowsRequired;
    }

}
