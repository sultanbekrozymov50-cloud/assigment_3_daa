import algorithms.DagShortestPath;
import graph.Graph;
import graph.GraphIO;
import metrics.Metrics;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class DagShortestPathTest {
    static long[] bellmanFord(Graph g) {
        int n = g.n();
        long[] d = new long[n];
        java.util.Arrays.fill(d, DagShortestPath.INF);
        int[] indeg = g.inDegrees();
        for (int v = 0; v < n; v++) if (indeg[v] == 0) d[v] = 0;
        for (int round = 0; round < n; round++) {
            boolean changed = false;
            for (int i = 0; i < g.m(); i++) {
                int u = g.edgeU(i), v = g.edgeV(i);
                if (d[u] != DagShortestPath.INF && d[u] + g.edgeW(i) < d[v]) { d[v] = d[u] + g.edgeW(i); changed = true; }
            }
            if (!changed) break;
        }
        return d;
    }

    @Test
    void matchesBellmanFordOnRandomDagsWithNegativeWeights() {
        Random rnd = new Random(13);
        for (int t = 0; t < 200; t++) {
            int n = 1 + rnd.nextInt(60);
            Graph g = TestUtil.randomDag(rnd, n, rnd.nextInt(4 * n));
            DagShortestPath sp = DagShortestPath.run(g, new Metrics());
            assertArrayEquals(bellmanFord(g), sp.dist, "graph " + t);
            for (int v = 0; v < n; v++) {
                int[] p = sp.pathTo(v);
                assertEquals(v, p[p.length - 1]);
                assertEquals(0, g.inDegrees()[p[0]]);
                long len = 0;
                for (int i = 0; i + 1 < p.length; i++) len += cheapest(g, p[i], p[i + 1], sp.dist);
                assertEquals(sp.dist[v], len);
            }
        }
    }

    private static long cheapest(Graph g, int u, int v, long[] dist) {
        long best = Long.MAX_VALUE;
        for (int a = g.firstArc(u); a != -1; a = g.nextArc(a))
            if (g.arcTo(a) == v) best = Math.min(best, g.arcWeight(a));
        assertEquals(dist[v], dist[u] + best, "parent edge is not tight");
        return best;
    }

    @Test
    void negativeWeightFile() throws Exception {
        Graph g = GraphIO.read(TestUtil.data("dag.txt"));
        DagShortestPath sp = DagShortestPath.run(g, new Metrics());
        assertArrayEquals(new long[] {0, 2, -2, 1, -2, 0, 1}, sp.dist);
        assertEquals(0, sp.sumOfDistances());
        assertArrayEquals(new int[] {5, 6, 4}, sp.pathTo(4));
        assertArrayEquals(new int[] {0, 1, 2}, sp.pathTo(2));
    }

    @Test
    void relaxationScansEachEdgeOnce() {
        Graph g = TestUtil.randomDag(new Random(9), 400, 1600);
        Metrics m = new Metrics();
        DagShortestPath.run(g, m);
        assertEquals(g.m(), m.edgeScans);
        assertEquals(g.m(), m.comparisons);
    }

    @Test
    void cycleIsRejected() throws Exception {
        Graph g = GraphIO.read(TestUtil.data("cyclic.txt"));
        assertThrows(IllegalArgumentException.class, () -> DagShortestPath.run(g, new Metrics()));
    }
}
