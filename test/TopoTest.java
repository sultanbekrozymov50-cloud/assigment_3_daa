import static org.junit.jupiter.api.Assertions.*;

import algorithms.Kahn;
import algorithms.TopoResult;
import graph.Graph;
import graph.GraphIO;
import java.util.Random;
import metrics.Metrics;
import org.junit.jupiter.api.Test;

class TopoTest {
    @Test
    void validOrderOnRandomDags() {
        Random rnd = new Random(11);
        for (int t = 0; t < 100; t++) {
            int n = 1 + rnd.nextInt(100);
            Graph g = TestUtil.randomDag(rnd, n, rnd.nextInt(4 * n));
            TopoResult r = Kahn.run(g, new Metrics());
            assertFalse(r.hasCycle);
            assertEquals(n, r.count);
            int[] pos = r.positions(n);
            for (int i = 0; i < g.m(); i++) assertTrue(pos[g.edgeU(i)] < pos[g.edgeV(i)]);
            assertTrue(Kahn.isValidOrder(g, r));
        }
    }

    @Test
    void cycleIsDetectedInsteadOfReturningAWrongOrder() throws Exception {
        Graph g = GraphIO.read(TestUtil.data("cyclic.txt"));
        TopoResult r = Kahn.run(g, new Metrics());
        assertTrue(r.hasCycle);
        assertTrue(r.count < g.n());
        assertFalse(Kahn.isValidOrder(g, r));
    }

    @Test
    void selfLoopIsACycle() {
        Graph g = new Graph(3, true);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 1, 1);
        assertTrue(Kahn.run(g, new Metrics()).hasCycle);
    }

    @Test
    void kahnScansEachEdgeOnce() {
        Graph g = TestUtil.randomDag(new Random(2), 500, 2000);
        Metrics m = new Metrics();
        Kahn.run(g, m);
        assertEquals(g.m(), m.edgeScans);
    }

    @Test
    void emptyAndIsolated() {
        assertEquals(0, Kahn.run(new Graph(0, true), new Metrics()).count);
        TopoResult r = Kahn.run(new Graph(4, true), new Metrics());
        assertEquals(4, r.count);
        assertFalse(r.hasCycle);
    }
}
