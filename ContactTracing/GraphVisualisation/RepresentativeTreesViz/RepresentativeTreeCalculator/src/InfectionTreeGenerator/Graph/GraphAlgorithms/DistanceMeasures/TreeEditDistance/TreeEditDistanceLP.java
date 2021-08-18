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
import InfectionTreeGenerator.Graph.Node;
import InfectionTreeGenerator.Graph.Tree;
import java.util.Map.Entry;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;

/**
 * Method from paper by Kondo, Seiichi, et al. 
 * "Fast computation of the tree edit distance between unordered trees using IP solvers." International Conference on Discovery Science. 2014. 
 * @author MaxSondag
 */
public class TreeEditDistanceLP<N extends Node, E extends Edge> {

    Tree tTarget;
    Tree tSource;

    TEDMapping mapping;

    Model model;
    IntVar objective;
    Solution solution;

    /**
     * For each pair of nodes, holds the variable assigned to it
     */
    HashMap<Pair<N, N>, BoolVar> variableMapping = new HashMap();

    TreeEditDistanceLP(Tree tSource, Tree tTarget) {
        this.tSource = tSource;
        this.tTarget = tTarget;
        model = new Model();

        setupLP();
    }

    private void setupLP() {
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

        //debug
//        System.out.println(model.toString());

        Solver solver = model.getSolver();

        solution = new Solution(model);
        while (solver.solve()) {
            solution = solution.record();
        }
        
        //debug
//        printResult();

        mapping = constuctTED();

        return solution.getIntVal(objective);
    }

    private void createVariables() {
        Collection<N> nodesS = tSource.getNodes();
        Collection<N> nodesT = tTarget.getNodes();

        for (N s : nodesS) {
            for (N t : nodesT) {
                BoolVar varN1N2 = model.boolVar(s.id + " to " + t.id);
                variableMapping.put(new Pair(s, t), varN1N2);
            }
        }
    }

    private void addNodeCapConstraints() {
        //adds constraint 2 and 3. Satisfied one-to-one mapping

        Collection<N> nodesS = tSource.getNodes();
        Collection<N> nodesT = tTarget.getNodes();

        for (N s : nodesS) {

            BoolVar[] vars = new BoolVar[nodesT.size()];
            int i = 0;
            for (N t : nodesT) {
                BoolVar var = variableMapping.get(new Pair(s, t));
                vars[i] = var;
                i++;
            }
            model.sum(vars, ">=", 0).post();
            model.sum(vars, "<=", 1).post();
        }

        //
        for (N t : nodesT) {
            BoolVar[] vars = new BoolVar[nodesS.size()];
            int i = 0;
            for (N s : nodesS) {
                BoolVar var = variableMapping.get(new Pair(s, t));
                vars[i] = var;
                i++;
            }
            model.sum(vars, ">=", 0).post();
            model.sum(vars, "<=", 1).post();
        }
    }

    private void addAncestorConstraints() {
        Collection<N> nodesS = tSource.getNodes();
        Collection<N> nodesT = tTarget.getNodes();

        for (N s1 : nodesS) {
            for (N t1 : nodesT) {
                BoolVar varS1T1 = variableMapping.get(new Pair(s1, t1));

                for (N s2 : nodesS) {
                    if (s1 == s2) {
                        continue;
                    }
                    for (N t2 : nodesT) {
                        if (t1 == t2) {
                            continue;
                        }
                        if (ancestorPreserved(s1, t1, s2, t2)) {
                            BoolVar varS2T2 = variableMapping.get(new Pair(s2, t2));
                            //m_{s_1,t_1}+m_{s_2,t_2} <= 1
                            model.arithm(varS1T1, "+", varS2T2, "<=", 1).post();
                        }
                    }
                }
            }
        }
    }

    /**
     * Ensure the roots are always mapped to each other
     */
    private void addRootConstraint() {
        Node rootS = tSource.calculateRoot();
        Node rootT = tTarget.calculateRoot();

        BoolVar varRootSRootT = variableMapping.get(new Pair(rootS, rootT));
        //probably a better way to do this, but it works
        //can't do a single one
        BoolVar[] rootSingletonArray = new BoolVar[]{varRootSRootT};
        model.sum(rootSingletonArray, "=", 1).post();

    }

    private void addObjectiveFunction() {

        Collection<N> nodesS = tSource.getNodes();
        Collection<N> nodesT = tTarget.getNodes();

        //size of total nodes is an easy upperbound
        objective = model.intVar("objective", 0, nodesS.size() + nodesT.size());

        //+1 for the offset
        IntVar[] summations = new IntVar[nodesS.size() * nodesT.size() + 1];
        int[] coefficients = new int[nodesS.size() * nodesT.size() + 1];
        int i = 0;
        for (N s : nodesS) {
            for (N t : nodesT) {
                //sum_{(s,t) \in S X T} {d(s,t)-d(s,e)-d(e,t)}m_{s,t} 
                //d(s,t)(label replacement) does not exist in unlabeled
                //d(s,e)(removal) = 1
                //d(e,t)(insertion) = 1
                //=sum_{(s,t) \in S X T} (-2)m_{s,t}
                //if we need to change something, incur the value
                BoolVar val = variableMapping.get(new Pair(s, t));
                summations[i] = val;
                coefficients[i] = -2;
                i++;
            }
        }

        //sum_{s \in S} d(s,e) + sum_{t \in T} d(e,t) = |S|+|T| as d(s,e) =1 and d(e,t) = 1
        IntVar offSet = model.intVar(nodesS.size() + nodesT.size());
        summations[nodesS.size() * nodesT.size()] = offSet;
        coefficients[nodesS.size() * nodesT.size()] = 1;

        //set the objective function
        model.scalar(summations, coefficients, "=", objective).post();

        model.setObjective(Model.MINIMIZE, objective);
    }

    private boolean ancestorPreserved(N s1, N t1, N s2, N t2) {
        boolean s2AncestorOfS1 = tSource.isAncestor(s2, s1); //s1 < s2
        boolean t2AncestorOfT1 = tSource.isAncestor(t2, t1); //t1 < t2

        return s2AncestorOfS1 ^ t2AncestorOfT1; //s1 < s2 XOR t1 < t2
    }

    private TEDMapping constuctTED() {

        HashMap<N, N> nodeMapping = new HashMap();
        HashMap<E, E> edgeMapping = new HashMap();

        for (Entry<Pair<N, N>, BoolVar> entry : variableMapping.entrySet()) {

            if (solution.getIntVal(entry.getValue()) == 1) //0 or 1, but a double so using 0.9
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

    private void printResult() {
        System.out.println("Printing results of LP. Only showing variables that are non-zero");
        for (BoolVar var : variableMapping.values()) {
            if (solution.getIntVal(var) == 1) {
                System.out.println(var.getName() + " set to 1");
            }
        }

    }

}
