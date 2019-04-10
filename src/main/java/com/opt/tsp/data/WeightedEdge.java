package com.opt.tsp.data;

import com.google.common.base.Function;
import org.apache.commons.collections4.Transformer;

public class WeightedEdge {
    private int distance;

    public WeightedEdge(int distance) {
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }

    public static final Function<WeightedEdge, Integer> edgeWeights = new Function<WeightedEdge, Integer>() {
        public Integer apply(WeightedEdge weightedEdge) {
            return weightedEdge.getDistance();
        }
    };

    public static final Transformer<WeightedEdge, Double> edgeWeightsTrans = new Transformer<WeightedEdge, Double>() {
        public Double transform(WeightedEdge weightedEdge) {
            return (double)weightedEdge.getDistance();
        }
    };
}
