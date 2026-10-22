import static org.junit.jupiter.api.Assertions.*;

import algorithms.Kosaraju;
import algorithms.SccResult;
import graph.Graph;
import graph.GraphIO;
import java.util.ArrayDeque;
import java.util.Random;
import metrics.Metrics;
import org.junit.jupiter.api.Test;

class SccTest {
    static boolean[][] reach(Graph g) {
        int n = g.n();
        boolean[][] r = new boolean[n][n];
        for (int s = 0; s < n; s++) {
            ArrayDeque<Integer> q = new ArrayDeque<>();
            r[s][s] = true;
            q.add(s);
            while (!q.isEmpty()) {
                int u = q.poll();
                for (int a = g.firstArc(u); a != -1; a = g.nextArc(a)) {
                    int v = g.arcTo(a);
                    if (!r[s][v]) { r[s][v] = true; q.add(v); }
                }
            }
        }
        return r;
    }

    static void assertMatchesBruteForce(Graph g, SccResult res) {
        boolean[][] r = reach(g);
        int n = g.n();
        for (int u = 0; u < n; u++)
            for (int v = 0; v < n; v++)
                assertEquals(r[u][v] && r[v][u], res.comp[u] == res.comp[v], "pair " + u + "," + v);
        long distinct = java.util.Arrays.stream(res.comp).distinct().count();
        assertEquals(distinct, res.count);
    }

    @Test
    void kosarajuMatchesBruteForceOnRandomGraphs() {
        Random rnd = new Random(3);
        for (int t = 0; t < 60; t++) {
            int n = 1 + rnd.nextInt(200);
            int m = rnd.nextInt(3 * n + 1);
            Graph g = TestUtil.randomDirected(rnd, n, m);
            assertMatchesBruteForce(g, Kosaraju.run(g, new Metrics()));
        }
    }

    @Test
    void cyclicFile() throws Exception {
        Graph g = GraphIO.read(TestUtil.data("cyclic.txt"));
        SccResult k = Kosaraju.run(g, new Metrics());
        assertEquals(4, k.count);
        assertEquals(k.comp[0], k.comp[2]);
        assertEquals(k.comp[3], k.comp[4]);
        assertNotEquals(k.comp[2], k.comp[3]);
    }

    @Test
    void kosarajuScansExactly2m() {
        Graph g = TestUtil.randomDirected(new Random(5), 300, 900);
        Metrics k = new Metrics();
        Kosaraju.run(g, k);
        assertEquals(2L * g.m(), k.edgeScans);
    }

    @Test
    void stackSafetyOnPathOf100000Vertices() {
        int n = 100_000;
        Graph g = new Graph(n, true, n);
        for (int i = 0; i + 1 < n; i++) g.addEdge(i, i + 1, 1);
        Metrics m = new Metrics();
        SccResult r = Kosaraju.run(g, m);
        assertEquals(n, r.count);
        assertEquals(n, m.maxDepth);
    }

    @Test
    void stackSafetyOnOneBigCycle() {
        int n = 100_000;
        Graph g = new Graph(n, true, n);
        for (int i = 0; i < n; i++) g.addEdge(i, (i + 1) % n, 1);
        assertEquals(1, Kosaraju.run(g, new Metrics()).count);
    }
}
