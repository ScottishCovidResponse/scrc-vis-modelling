/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfectionTreeGenerator.Graph.GraphAlgorithms.DistanceMeasures.TreeEditDistance;

import Utility.Pair;

import java.util.Collection;
import java.util.HashMap;

import InfectionTreeGenerator.Graph.Edge;
import InfectionTreeGenerator.Graph.GraphAlgorithms.DistanceMeasures.TreeDistanceMeasure;
import InfectionTreeGenerator.Graph.Node;
import InfectionTreeGenerator.Graph.Tree;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.cplex.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Method from paper by Kondo, Seiichi, et al. 
 * "Fast computation of the tree edit distance between unordered trees using IP solvers." International Conference on Discovery Science. 2014. 
 * @author MaxSondag
 */
public class TreeEditDistanceLPCplex<N extends Node, E extends Edge>  {

    protected int TIMEOUT = 3600;//one hour timeout

    Tree tTarget;
    Tree tSource;

    TEDMapping mapping;

    //Note: cplex require a jvm argument: -Djava.library.path=/path 
    IloCplex cplex;
    IloLinearNumExpr objective;

    /**
     * For each pair of nodes, holds the variable assigned to it
     */
    protected HashMap<Pair<N, N>, IloIntVar> variableMapping = new HashMap();

    TreeEditDistanceLPCplex(Tree tSource, Tree tTarget) {

        this.tSource = tSource;
        this.tTarget = tTarget;

        try {
            cplex = new IloCplex();
            setupLP();

        } catch (IloException ex) {
            Logger.getLogger(TreeEditDistanceLPCplex.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void setupLP() throws IloException {
        createVariables();
        addNodeCapConstraints();
        addAncestorConstraints();
        addRootConstraint();
        addObjectiveFunction();

    }

    /**
     * Holds for each node in {@code tSource} to which node it maps in
     * {@code tTarget}.
     *
     * @return
     */
    public TEDMapping<N, E> getMapping() {
        return mapping;
    }

    public int solve() {
        try {
            OutputStream outputStream;
            //shut up cplex and write it to a file
            outputStream = new FileOutputStream("./Data/CplexOutputStream.txt", true);
            cplex.setOut(outputStream);

            //1 hour timeout
            cplex.setParam(IloCplex.DoubleParam.TimeLimit, TIMEOUT);

            if (cplex.solve()) {
//        printResult();
                mapping = constructTED();
                int objectiveValue = (int) cplex.getObjValue();
                cplex.close();
                return objectiveValue;
            }
            System.err.println("Couldn't find a solution");
        } catch (IloException ex) {
            Logger.getLogger(TreeEditDistanceLPCplex.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {

        }
        return -1;
    }

    private void createVariables() throws IloException {
        Collection<N> nodesS = tSource.getNodes();
        Collection<N> nodesT = tTarget.getNodes();

        for (N s : nodesS) {
            for (N t : nodesT) {
                IloIntVar varN1N2 = cplex.boolVar(s.id + " to " + t.id);
                variableMapping.put(new Pair(s, t), varN1N2);
            }
        }
    }

    private void addNodeCapConstraints() throws IloException {
        //adds constraint 2 and 3. Satisfied one-to-one mapping

        Collection<N> nodesS = tSource.getNodes();
        Collection<N> nodesT = tTarget.getNodes();

        for (N s : nodesS) {
            IloLinearNumExpr constraint = cplex.linearNumExpr();
            for (N t : nodesT) {
                IloIntVar var = variableMapping.get(new Pair(s, t));
                constraint.addTerm(var, 1);
            }
            cplex.addLe(constraint, 1);
            cplex.addGe(constraint, 0);
        }

        //
        for (N t : nodesT) {
            IloLinearNumExpr constraint = cplex.linearNumExpr();
            int i = 0;
            for (N s : nodesS) {
                IloIntVar var = variableMapping.get(new Pair(s, t));
                constraint.addTerm(var, 1);
            }
            cplex.addLe(constraint, 1);
            cplex.addGe(constraint, 0);
        }
    }

    private void addAncestorConstraints() throws IloException {
        Collection<N> nodesS = tSource.getNodes();
        Collection<N> nodesT = tTarget.getNodes();

        for (N s1 : nodesS) {
            for (N t1 : nodesT) {
                IloIntVar varS1T1 = variableMapping.get(new Pair(s1, t1));

                for (N s2 : nodesS) {
                    if (s1 == s2) {
                        continue;
                    }
                    for (N t2 : nodesT) {
                        if (t1 == t2) {
                            continue;
                        }
                        if (ancestorPreserved(s1, t1, s2, t2)) {
                            IloIntVar varS2T2 = variableMapping.get(new Pair(s2, t2));
                            //m_{s_1,t_1}+m_{s_2,t_2} <= 1
                            IloLinearNumExpr constraint = cplex.linearNumExpr();
                            constraint.addTerm(1, varS1T1);
                            constraint.addTerm(1, varS2T2);
                            cplex.addLe(constraint, 1);
                        }
                    }
                }
            }
        }
    }

    /**
     * Ensure the roots are always mapped to each other
     */
    private void addRootConstraint() throws IloException {
        Node rootS = tSource.calculateRoot();
        Node rootT = tTarget.calculateRoot();

        IloIntVar varRootSRootT = variableMapping.get(new Pair(rootS, rootT));

        IloLinearNumExpr constraint = cplex.linearNumExpr();
        constraint.addTerm(1, varRootSRootT);
        cplex.addEq(constraint, 1);
    }

    protected void addObjectiveFunction() throws IloException {

        Collection<N> nodesS = tSource.getNodes();
        Collection<N> nodesT = tTarget.getNodes();

        //size of total nodes is an easy upperbound
        objective = cplex.linearNumExpr();
        //+1 for the offset
        for (N s : nodesS) {
            for (N t : nodesT) {
                //sum_{(s,t) \in S X T} {d(s,t)-d(s,e)-d(e,t)}m_{s,t} 
                //d(s,t)(label replacement) does not exist in unlabeled
                //d(s,e)(removal) = 1
                //d(e,t)(insertion) = 1
                //=sum_{(s,t) \in S X T} (-2)m_{s,t}
                //if we need to change something, incur the value
                IloIntVar val = variableMapping.get(new Pair(s, t));
                objective.addTerm(-2, val);
            }
        }

        objective.setConstant(nodesS.size() + nodesT.size());
        //sum_{s \in S} d(s,e) + sum_{t \in T} d(e,t) = |S|+|T| as d(s,e) =1 and d(e,t) = 1

        //set the objective function
        cplex.addMinimize(objective);
    }

    private boolean ancestorPreserved(N s1, N t1, N s2, N t2) {
        boolean s2AncestorOfS1 = tSource.isAncestor(s2, s1); //s1 < s2
        boolean t2AncestorOfT1 = tSource.isAncestor(t2, t1); //t1 < t2

        return s2AncestorOfS1 ^ t2AncestorOfT1; //s1 < s2 XOR t1 < t2
    }

    protected TEDMapping constructTED() throws IloException {

        HashMap<N, N> nodeMapping = new HashMap();
        HashMap<E, E> edgeMapping = new HashMap();

        for (Entry<Pair<N, N>, IloIntVar> entry : variableMapping.entrySet()) {

            if (cplex.getValue(entry.getValue()) == 1) //0 or 1, but a double so using 0.9
            {
                //node is mapped to each other
                Pair<N, N> nodes = entry.getKey();
                N s = nodes.a;
                N t = nodes.b;
                nodeMapping.put(s, t);
            }
        }

        Collection<E> edges = tSource.getEdges();
        for (E sE : edges) {
            if (nodeMapping.containsKey(sE.source) && nodeMapping.containsKey(sE.target)) {
                E tE = (E) tTarget.getEdge(nodeMapping.get(sE.source).id, nodeMapping.get(sE.target).id);
                edgeMapping.put(sE, tE);
            }
        }

        TEDMapping newMap = new TEDMapping(nodeMapping, edgeMapping);
        return newMap;
    }

    private void printResult() throws IloException {
        System.out.println("Printing results of LP. Only showing variables that are non-zero");
        for (IloIntVar var : variableMapping.values()) {
            if (cplex.getValue(var) == 1) {
                System.out.println(var.getName() + " set to 1");
            }
        }

    }

}
