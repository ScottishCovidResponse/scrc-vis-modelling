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
     * How many timewindows we have. Note that all trees are anchored at 0.
     */
    int totalTimeWindows;
    /**
     * How many timesteps we take in a single window. Note that all trees are anchored at 0.
     */
    int timeWindowSize;
    
    public RtDistanceMeasure(int totalTimeWindows, int timeWindowSize) {
        this.totalTimeWindows = totalTimeWindows;
        this.timeWindowSize = timeWindowSize;
    }

    @Override
    public double getDistance(Tree<InfectionNode, InfectionEdge> t1, Tree<InfectionNode, InfectionEdge> t2) {
        Double[] rtT1 = getRtValuesPerStep(t1);
        Double[] rtT2 = getRtValuesPerStep(t2);

        assert (rtT1.length == rtT2.length);
        assert (rtT1.length == totalTimeWindows);

        double absoluteDiff = 0;
        for (int i = 0; i < totalTimeWindows; i++) {
            absoluteDiff += Math.abs(rtT1[i] - rtT2[i]);
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

}
