package student;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import graph.Edge;
import graph.Graph;

public class ProblemSolverREPConnectionTest {
    
    Graph<String> graph;
    IProblem solver;

    @BeforeEach
    void setUp() throws Exception {
        makeSampleTree();
        solver = new ProblemSolver();
    }

    /**
     * Tree structure overview:
     *
     * a
     * ├── b
     * │   ├── d
     * │   │   └── e
     * │   │       └── f
     * │   └── g
     * │       ├── h
     * │       └── i
     * └── c
     *     ├── j
     *     │   └── k
     *     │       └── l
     *     │           └── m
     *     └── n
     *         ├── o
     *         ├── p
     *         ├── q
     *         ├── r
     *         ├── s
     *         ├── t
     *         ├── u
     *         ├── v
     *         ├── w
     *         ├── x
     *         ├── y
     *         └── z
     */
    private void makeSampleTree() {
        graph = new Graph<String>();
        
        // Vertices
        String[] vertices = {
            "a", "b", "c", "d", "e", "f", "g", "h", "i",
            "j", "k", "l", "m", "n", "o", "p", "q", "r",
            "s", "t", "u", "v", "w", "x", "y", "z"
        };
        for (String vertex : vertices) {
            graph.addVertex(vertex);
        }

        // Edges
        graph.addEdge("a", "b");
        graph.addEdge("a", "c");

        // Subtree under b
        graph.addEdge("b", "d");
        graph.addEdge("d", "e");
        graph.addEdge("e", "f");
        graph.addEdge("b", "g");
        graph.addEdge("g", "h");
        graph.addEdge("g", "i");

        // Subtree under c
        graph.addEdge("c", "j");
        graph.addEdge("j", "k");
        graph.addEdge("k", "l");
        graph.addEdge("l", "m");
        graph.addEdge("c", "n");

        // Subtree under n
        String[] nChildren = {"o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
        for (String child : nChildren) {
            graph.addEdge("n", child);
        }
    }

    @Test
    void testRedundantEdgeConnection() {
        Edge<String> e = solver.addRedundant(graph, "a");
        PowerOutage<String> po = new PowerOutage<String>();
        int outageSize = po.largestPowerBreak(graph, e, "a");
        assertEquals(4, outageSize);
    }

    
}