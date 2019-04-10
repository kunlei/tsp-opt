package com.opt.tsp.data;


import com.google.common.base.Function;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import org.junit.Test;

public class GraphTest {
    private static double tolerance = 0.00001;
    private static String graphName = "Simple Test Graph";

    private static enum Node {
        a, b, c, d, e, f;
    }

    private static enum Edge {
        ab(Node.a, Node.b, 1),
        bc(Node.b, Node.c, 1),
        ac(Node.a, Node.c, 1),
        bd(Node.b,Node.d,10),
        ce(Node.c,Node.e,10),
        de(Node.d,Node.e,1),
        df(Node.d,Node.f,1),
        ef(Node.e,Node.f,1)
        ;

        private Node first;
        private Node second;
        private int weight;

        Edge(Node first, Node second, int weight) {
            this.first = first;
            this.second = second;
            this.weight = weight;
        }

        public Node getFirst() {
            return first;
        }

        public Node getSecond() {
            return second;
        }

        public int getWeight() {
            return weight;
        }
    }

    private static Function<Edge, Integer> makeEdgeWeights() {
        return new Function<Edge, Integer>() {
            public Integer apply(Edge edge) {
                return edge.getWeight();
            }
        };
    }

    private static TspInstance<Node, Edge> makeGraph() {
        UndirectedGraph<Node, Edge> graph = new UndirectedSparseGraph<Node, Edge>();
        for (Node node : Node.values()) {
            graph.addVertex(node);
        }
        for (Edge edge : Edge.values()) {
            graph.addEdge(edge, edge.getFirst(), edge.getSecond());
        }
        return new TspInstance<Node, Edge>(graph, makeEdgeWeights(), graphName);
    }

    @Test
    public void testNewGraph() {
        TspInstance<Node, Edge> instance = makeGraph();
        Graph<Node, Edge> graph = instance.getGraph();
        System.out.println("Vetex count = " + graph.getVertexCount());
        System.out.println("Edge count = " + graph.getEdgeCount());
    }
}
