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
public class RtDistanceMeasure7StepsAverage extends RtDistanceMeasure implements TreeDistanceMeasure {

    public RtDistanceMeasure7StepsAverage(int totalTimeWindows, int timeWindowSize) {
        super(totalTimeWindows, timeWindowSize);
    }

    @Override
    protected Double[] getCalculatedRtValues(Tree<InfectionNode, InfectionEdge> t) {
        Double[] rtValuesPerStep = getRtValuesPerStep(t);
        Double[] rt7StepAverage = new Double[rtValuesPerStep.length - 7];

        for (int i = 0; i < rt7StepAverage.length; i++) {
            double sum = 0;
            for (int j = 0; j < 7; j++) {
                sum += rtValuesPerStep[i + j];
            }
            rt7StepAverage[i] = sum / 7.0;
        }
        return rt7StepAverage;
    }

}
