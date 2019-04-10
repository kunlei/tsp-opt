package com.opt.tsp.console;

import com.opt.tsp.data.GeoNode;
import com.opt.tsp.data.TspInstance;
import com.opt.tsp.data.TspLibParser;
import com.opt.tsp.data.WeightedEdge;
import com.opt.tsp.model.TspIpSolver;
import ilog.concert.IloException;

import java.util.EnumSet;

public class Main {
    public static void main(String[] args) {
        String[] problemNames = new String[]{"eil51", "bier127", "ch130", "ch150", "d198", "d493", "d657", "d1291", "fl1400"};
        String problemName = problemNames[5];
        System.out.print("Reading file " + problemName + "...");
        TspInstance<GeoNode, WeightedEdge> instance;
        try {
            instance = TspLibParser.parse(problemName);
            System.out.println(" complete!");
        } catch (TspLibParser.UnsupportedFileTypeException e) {
            throw new RuntimeException(e);
        }

        TspIpSolver<GeoNode, WeightedEdge> solver;
        try {
            System.out.print("Building problem...");
            solver = new TspIpSolver<>(instance, EnumSet.of(TspIpSolver.Option.lazy));
            System.out.println(" complete!");

            System.out.print("Solving TSP...");
            solver.solve();
            System.out.println(" complete!");
        } catch (IloException e) {
            throw new RuntimeException();
        }
    }
}
