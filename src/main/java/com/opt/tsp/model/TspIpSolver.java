package com.opt.tsp.model;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableSet;
import com.opt.tsp.data.TspInstance;
import com.opt.tsp.util.Util;
import edu.uci.ics.jung.graph.UndirectedGraph;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.cplex.IloCplex;

import java.util.*;

public class TspIpSolver<V, E> {
    private EnumSet<Option> options;
    private IloCplex cplex;
    private TspInstance<V, E> tspInstance;
    private ImmutableBiMap<E, IloIntVar> edgeVariables;

    private ImmutableSet<E> edgesInOpt;
    private double optVal;
    private IloIntVar[] edgeVariablesAsArray;

    public static enum Option {
        lazy, userCut, randomizedUserCut, christofidesApprox,
        christofidesHeurisitc, twoOpt, incumbent;
    }

    public TspIpSolver(TspInstance<V, E> tspInstance) throws IloException {
        this(tspInstance, EnumSet.of(Option.lazy, Option.userCut, Option.christofidesApprox, Option.christofidesHeurisitc));
    }

    public TspIpSolver(TspInstance<V, E> tspInstance, EnumSet<Option> options) throws IloException {
        this.options = options;
        this.tspInstance = tspInstance;
        this.cplex = new IloCplex();
        UndirectedGraph<V, E> graph = tspInstance.getGraph();
        this.edgeVariables = Util.makeBinaryVariables(cplex, graph.getEdges());
        edgeVariablesAsArray = edgeVariables.inverse().keySet().toArray(new IloIntVar[0]);

        // add degree constraints
        for (V vertex : graph.getVertices()) {
            cplex.addEq(Util.integerSum(cplex, edgeVariables, graph.getIncidentEdges(vertex)), 2);
        }

        // add objective
        cplex.addMinimize(Util.integerSum(cplex, edgeVariables, graph.getEdges(),
                tspInstance.getEdgeWeights()));

        if (options.contains(Option.lazy)) {
            cplex.use(new IntegerCutCallback());
        }
    }

    public void solve() throws IloException {
        if (!cplex.solve()) {
            throw new RuntimeException();
        }
        optVal = cplex.getObjValue();
        edgesInOpt = ImmutableSet.copyOf(edgesUsed(cplex.getValues(edgeVariablesAsArray)));
        cplex.end();
    }

    private Set<E> edgesUsed(double[] edgeVarVals) {
        Set<E> ans = new HashSet<>();
        for (int e = 0; e < edgeVarVals.length; ++e) {
            if (Util.doubleToBoolean(edgeVarVals[e])) {
                ans.add(edgeVariables.inverse().get(edgeVariablesAsArray[e]));
            }
        }
        return ans;
    }

    private double[] inverseEdgesUsed(Set<E> edgeUsed) {
        double[] edgeVals = new double[this.edgeVariablesAsArray.length];
        for (int i = 0; i < edgeVals.length; ++i) {
            edgeVals[i] = edgeUsed.contains(edgeVariables.inverse().get(edgeVariablesAsArray[i])) ? 1 : 0;
        }
        return edgeVals;
    }

    private Map<E, Double> getNonZeroEdgeWeights(double[] edgeValues) {
        Map<E, Double> edgeWeights = new HashMap<>();
        for (int i = 0; i < edgeValues.length; ++i) {
            if (edgeValues[i] > Util.epsilon) {
                edgeWeights.put(edgeVariables.inverse().get(edgeVariablesAsArray[i]), edgeValues[i]);
            }
        }
        return edgeWeights;
    }

    private Set<E> edgesAtOne(double[] edgeVarVals) {
        Set<E> ans = new HashSet<>();
        for (int e = 0; e < edgeVarVals.length; ++e) {
            if (edgeVarVals[e] >= (1 - Util.epsilon)) {
                ans.add(edgeVariables.inverse().get(edgeVariablesAsArray[e]));
            }
        }
        return ans;
    }

    private class IntegerCutCallback extends IloCplex.LazyConstraintCallback {
        public IntegerCutCallback() {
        }

        @Override
        protected void main() throws IloException {
            Set<E> edgesUsed = edgesUsed(this.getValues(edgeVariablesAsArray));
            Set<Set<V>> connectedComponents = tspInstance.getConnectedComponents(edgesUsed);
            if (connectedComponents.size() > 1) {
                for (Set<V> connectedComponent : connectedComponents) {
                    this.add(cplex.ge(Util.integerSum(cplex, edgeVariables, tspInstance.cutEdges(connectedComponent)), 2));
                }
            }
        }
    }
}
