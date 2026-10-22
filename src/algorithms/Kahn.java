package algorithms;

import graph.Graph;
import metrics.Metrics;

public final class Kahn {
    private Kahn() {}

    public static TopoResult run(Graph g, Metrics m) {
        if (!g.isDirected()) throw new IllegalArgumentException("topological order needs a directed graph");
        int n = g.n();
        int[] deg = g.inDegrees();
        int[] queue = new int[n];
        int head = 0, tail = 0;
        for (int v = 0; v < n; v++) if (deg[v] == 0) queue[tail++] = v;
        while (head < tail) {
            int u = queue[head++];
            for (int a = g.firstArc(u); a != -1; a = g.nextArc(a)) {
                m.edgeScans++;
                int v = g.arcTo(a);
                if (--deg[v] == 0) queue[tail++] = v;
            }
        }
        return new TopoResult(queue, head, head < n);
    }

    public static boolean isValidOrder(Graph g, TopoResult r) {
        if (r.hasCycle || r.count != g.n()) return false;
        int[] pos = r.positions(g.n());
        for (int v = 0; v < g.n(); v++) if (pos[v] < 0) return false;
        for (int i = 0; i < g.m(); i++) if (pos[g.edgeU(i)] >= pos[g.edgeV(i)]) return false;
        return true;
    }
}
