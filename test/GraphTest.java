import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import algorithms.*;
import graph.Graph;
import graph.GraphGenerator;
import graph.GraphIO;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import metrics.Metrics;
import org.junit.jupiter.api.Test;

class GraphTest {
    @Test
    void readsAllDataFiles() throws Exception {
        Graph c = GraphIO.read(TestUtil.data("connected.txt"));
        assertEquals(6, c.n()); assertEquals(9, c.m()); assertFalse(c.isDirected());
        Graph d = GraphIO.read(TestUtil.data("disconnected.txt"));
        assertEquals(8, d.n()); assertEquals(7, d.m());
        Graph y = GraphIO.read(TestUtil.data("cyclic.txt"));
        assertTrue(y.isDirected());
        Graph a = GraphIO.read(TestUtil.data("dag.txt"));
        assertEquals(9, a.m());
    }

    @Test
    void writeThenReadRoundTrip() throws Exception {
        Graph g = TestUtil.randomDirected(new Random(1), 20, 50);
        Path tmp = Files.createTempFile("graph", ".txt");
        GraphIO.write(g, tmp);
        Graph h = GraphIO.read(tmp);
        assertEquals(g.n(), h.n());
        assertEquals(g.m(), h.m());
        for (int i = 0; i < g.m(); i++) {
            assertEquals(g.edgeU(i), h.edgeU(i));
            assertEquals(g.edgeV(i), h.edgeV(i));
            assertEquals(g.edgeW(i), h.edgeW(i));
        }
        Files.delete(tmp);
    }

    @Test
    void badInputIsRejected() throws Exception {
        Path tmp = Files.createTempFile("bad", ".txt");
        Files.writeString(tmp, "3 2 directed\n0 1 5\n");
        assertThrows(java.io.IOException.class, () -> GraphIO.read(tmp));
        Files.delete(tmp);
        assertThrows(IllegalArgumentException.class, () -> new Graph(2, true).addEdge(0, 2, 1));
    }

    @Test
    void generatorReproducesReferenceValuesForSmallestGraphs() {
        int n = 1000;
        assertEquals(147_970, Kruskal.run(GraphGenerator.undirected(n, 4 * n), new Metrics()).totalWeight);
        assertEquals(147_970, Prim.run(GraphGenerator.undirected(n, 4 * n), new Metrics()).totalWeight);
        assertEquals(27, Kosaraju.run(GraphGenerator.directed(n, 4 * n), new Metrics()).count);
        assertEquals(396_274, DagShortestPath.run(GraphGenerator.dag(n, 4 * n), new Metrics()).sumOfDistances());
        assertEquals(30_363, Kruskal.run(GraphGenerator.undirected(n, 20 * n), new Metrics()).totalWeight);
        assertEquals(1, Kosaraju.run(GraphGenerator.directed(n, 20 * n), new Metrics()).count);
        assertEquals(252_411, DagShortestPath.run(GraphGenerator.dag(n, 20 * n), new Metrics()).sumOfDistances());
    }
}
