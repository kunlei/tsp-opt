package com.opt.tsp.util;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearIntExpr;
import ilog.cplex.IloCplex;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Set;

public class Util {

    /**
     * Define a predicate function that return boolean value
     * @param set
     * @param <T>
     * @return
     */
    public static <T> Predicate<T> inSet(final Set<T> set) {
        return new Predicate<T>() {
            public boolean apply(@Nullable T t) {
                return set.contains(t);
            }

            public boolean test(@Nullable T input) {
                return apply(input);
            }
        };
    }

    /**
     * Create a BiMap containing edges and their associated boolean variables
     * @param cplex
     * @param set
     * @param <T>
     * @return
     * @throws IloException
     */
    public static <T> ImmutableBiMap<T, IloIntVar> makeBinaryVariables(IloCplex cplex, Iterable<T> set) throws IloException {
        ImmutableBiMap.Builder<T, IloIntVar> ans = ImmutableBiMap.builder();
        for (T t : set) {
            ans.put(t, cplex.boolVar());
        }
        return ans.build();
    }

    /**
     * Build an IloLinearIntExpr using given set of edges and efault edge weight of 1
     * @param cplex
     * @param variables
     * @param set
     * @param <T>
     * @return
     * @throws IloException
     */
    public static <T> IloLinearIntExpr integerSum(IloCplex cplex,
                                                 BiMap<T, IloIntVar> variables,
                                                 Iterable<T> set) throws IloException {
        return integerSum(cplex, variables, set, unity);
    }

    /**
     * Build an IloLinearIntExpr consisting of selected edges and chosen weight function
     * @param cplex
     * @param variables
     * @param set
     * @param coeffients
     * @param <T>
     * @return
     * @throws IloException
     */
    public static <T> IloLinearIntExpr integerSum(IloCplex cplex,
                                                  BiMap<T, IloIntVar> variables,
                                                  Iterable<T> set,
                                                  Function<? super T, Integer> coeffients) throws IloException {
        IloLinearIntExpr sum = cplex.linearIntExpr();
        for (T t : set) {
            sum.addTerm(variables.get(t), coeffients.apply(t));
        }
        return sum;
    }

    /**
     * Define an unity edge weight function
     */
    public static Function<Object, Integer> unity = new Function<Object, Integer>() {
        @Override
        public Integer apply(@Nullable Object o) {
            return 1;
        }
    };

    public static double epsilon = 0.000001;
    public static boolean doubleToBoolean(double value) {
        if (Math.abs(1 - value) < epsilon) {
            return true;
        } else if (Math.abs(value) < epsilon) {
            return false;
        } else {
            throw new RuntimeException("Failed to convert to boolean, not near zero or one: " + value);
        }
    }
}
