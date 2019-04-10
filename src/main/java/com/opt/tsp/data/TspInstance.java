package com.opt.tsp.data;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.opt.tsp.util.Util;
import edu.uci.ics.jung.algorithms.cluster.WeakComponentClusterer;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.algorithms.filters.EdgePredicateFilter;

import java.util.HashSet;
import java.util.Set;

/**
 * Represent a TSP instance
 * @param <V>
 * @param <E>
 */
public class TspInstance<V, E> {
    private UndirectedGraph<V, E> graph;
    private Function<E, Integer> edgeWeights;
    private String name;

    public TspInstance(UndirectedGraph<V, E> graph, Function<E, Integer> edgeWeights, String name) {
        this.graph = graph;
        this.edgeWeights = edgeWeights;
        this.name = name;
    }

    public UndirectedGraph<V, E> getGraph() {
        return graph;
    }

    public Function<E, Integer> getEdgeWeights() {
        return edgeWeights;
    }

    public String getName() {
        return name;
    }

    public Set<E> cutEdges(Set<V> cutVertices) {
        Set<E> ans = new HashSet<>();
        for (V vertex : graph.getVertices()) {
            if (!cutVertices.contains(vertex)) {
                for (V cutVertex : cutVertices) {
                    E cutEdge = graph.findEdge(vertex, cutVertex);
                    if (cutEdge != null) {
                        ans.add(cutEdge);
                    }
                }
            }
        }
        return ans;
    }

    public Set<Set<V>> getConnectedComponents(final Set<E> includedEdges) {
        Predicate<E> edgesUsed = Util.inSet(includedEdges);
        EdgePredicateFilter<V, E> filter = new EdgePredicateFilter<V, E>(edgesUsed);
        Graph<V, E> subGraph = filter.apply(this.graph);
        WeakComponentClusterer<V, E> clusterer = new WeakComponentClusterer<V, E>();
        return clusterer.apply(subGraph);
    }

    public int cost(Iterable<E> edgeSet) {
        int ans = 0;
        for (E edge : edgeSet) {
            ans += this.edgeWeights.apply(edge);
        }
        return ans;
    }
}
