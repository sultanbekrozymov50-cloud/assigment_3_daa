import static org.junit.jupiter.api.Assertions.*;

import algorithms.*;
import graph.Graph;
import graph.GraphIO;
import java.util.Random;
import metrics.Metrics;
import org.junit.jupiter.api.Test;

class MstTest {
    @Test
    void kruskalAndPrimAgreeOn100RandomConnectedGraphs() {
        Random rnd = new Random(7);
        for (int t = 0; t < 100; t++) {
            int n = 2 + rnd.nextInt(60);
            Graph g = TestUtil.randomConnected(rnd, n, rnd.nextInt(4 * n), 1 + rnd.nextInt(50));
            MstResult k = Kruskal.run(g, new Metrics());
            MstResult p = Prim.run(g, new Metrics());
            assertEquals(k.totalWeight, p.totalWeight, "weights differ on graph " + t);
            assertEquals(n - 1, k.edgeCount);
            assertEquals(n - 1, p.edgeCount);
            assertEquals(1, k.components);
            assertEquals(1, p.components);
            for (MstResult r : new MstResult[] {k, p}) {
                DSU d = new DSU(n);
                long sum = 0;
                for (int i = 0; i < r.edgeCount; i++) {
                    assertTrue(d.union(r.u[i], r.v[i]), "tree edge closes a cycle");
                    sum += r.w[i];
                }
                assertEquals(r.totalWeight, sum);
            }
        }
    }

    @Test
    void connectedFile() throws Exception {
        Graph g = GraphIO.read(TestUtil.data("connected.txt"));
        assertEquals(14, Kruskal.run(g, new Metrics()).totalWeight);
        assertEquals(14, Prim.run(g, new Metrics()).totalWeight);
    }

    @Test
    void disconnectedGraphGivesForestWithoutThrowing() throws Exception {
        Graph g = GraphIO.read(TestUtil.data("disconnected.txt"));
        MstResult k = Kruskal.run(g, new Metrics());
        MstResult p = Prim.run(g, new Metrics());
        assertEquals(12, k.totalWeight);
        assertEquals(12, p.totalWeight);
        assertEquals(4, k.components);
        assertEquals(4, p.components);
        assertEquals(g.n() - 4, k.edgeCount);
        assertEquals(g.n() - 4, p.edgeCount);
    }

    @Test
    void degenerateGraphs() {
        assertEquals(0, Kruskal.run(new Graph(0, false), new Metrics()).totalWeight);
        assertEquals(0, Prim.run(new Graph(0, false), new Metrics()).components);
        Graph one = new Graph(1, false);
        one.addEdge(0, 0, 5);
        assertEquals(0, Kruskal.run(one, new Metrics()).totalWeight);
        assertEquals(0, Prim.run(one, new Metrics()).totalWeight);
        Graph isolated = new Graph(5, false);
        assertEquals(5, Kruskal.run(isolated, new Metrics()).components);
        assertEquals(5, Prim.run(isolated, new Metrics()).components);
    }

    @Test
    void kruskalCountsOperationsInsideTheAlgorithm() throws Exception {
        Graph g = GraphIO.read(TestUtil.data("connected.txt"));
        Metrics m = new Metrics();
        Kruskal.run(g, m);
        assertTrue(m.edgeScans > 0 && m.edgeScans <= g.m());
        assertTrue(m.dsuOps >= 2 * (g.n() - 1));
        assertTrue(m.comparisons > 0);
    }
}
