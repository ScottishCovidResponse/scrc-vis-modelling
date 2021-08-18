/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfectionTreeGenerator.Graph.GraphAlgorithms.DistanceMeasures.TreeEditDistance;

import Utility.Pair;

import java.util.Collection;

import InfectionTreeGenerator.Graph.Edge;
import InfectionTreeGenerator.Graph.Node;
import InfectionTreeGenerator.Graph.Tree;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.cplex.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Method from paper by Kondo, Seiichi, et al. 
 * "Fast computation of the tree edit distance between unordered trees using IP solvers." International Conference on Discovery Science. 2014. 
 * @author MaxSondag
 */
public class TreeEditDistanceLPCplexPrioritizeDepth<N extends Node, E extends Edge> extends TreeEditDistanceLPCplex {

    public TreeEditDistanceLPCplexPrioritizeDepth(Tree tSource, Tree tTarget) {
        super(tSource, tTarget);
    }

    @Override
    public int solve() {
        try {
            OutputStream outputStream;
            //shut up cplex and write it to a file
            outputStream = new FileOutputStream("./Data/CplexOutputStream.txt", true);
            cplex.setOut(outputStream);

            //1 hour timeout
            cplex.setParam(IloCplex.DoubleParam.TimeLimit, TIMEOUT);

            if (cplex.solve()) {
                mapping = constructTED();
                double objectiveValue = cplex.getObjValue();
                cplex.close();

                return (int) Math.floor(objectiveValue); //round the result
            }
            System.err.println("Couldn't find a solution");
        } catch (IloException ex) {
            Logger.getLogger(TreeEditDistanceLPCplexPrioritizeDepth.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {

        }
        return -1;
    }

    /**
     * Updated objective function with priority to node at same depth
     *
     * @throws IloException
     */
    @Override
    protected void addObjectiveFunction() throws IloException {

        Collection<N> nodesS = tSource.getNodes();
        Collection<N> nodesT = tTarget.getNodes();

        //getDepth() can be 0 in case of a singleton node
        double maxDepth = Math.max(1, Math.max(tSource.getDepth(), tTarget.getDepth()));
        //we prefer to match nodes on similair depths. This is the factor we need to ensure we keep within the optimum solution
        //|T1.nodes|*|T2.nodes|*maxDepth*2 should be smaller than 1
        double epsilonScale = 0.99 / (nodesS.size() * nodesT.size() * maxDepth * 2);

        objective = cplex.linearNumExpr();
        for (N s : nodesS) {
            for (N t : nodesT) {
                //sum_{(s,t) \in S X T} {d(s,t)-d(s,e)-d(e,t)}m_{s,t} 
                //d(s,t)(label replacement) = \epsilon*depth difference of s and t
                //d(s,e)(removal) = 1
                //d(e,t)(insertion) = 1
                //=sum_{(s,t) \in S X T} (-2)m_{s,t}
                //if we need to change something, incur the value

                //d(s,t) is modified to 
                double sDepth = tSource.getDepth(s);
                double tDepth = tTarget.getDepth(t);

                double depthDiff = Math.abs(sDepth - tDepth);

                //need to cast for some arcane reaseon
                IloIntVar val = (IloIntVar) variableMapping.get(new Pair(s, t));
                objective.addTerm(-2 + depthDiff * epsilonScale, val);
            }
        }

        objective.setConstant(nodesS.size() + nodesT.size());
        //sum_{s \in S} d(s,e) + sum_{t \in T} d(e,t) = |S|+|T| as d(s,e) =1 and d(e,t) = 1

        //set the objective function
        cplex.addMinimize(objective);
    }
}
