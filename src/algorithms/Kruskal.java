package algorithms;

import graph.Graph;
import metrics.Metrics;

public final class Kruskal {
    private Kruskal() {}

    public static MstResult run(Graph g, Metrics m) {
        if (g.isDirected()) throw new IllegalArgumentException("MST needs an undirected graph");
        int n = g.n(), e = g.m();
        int[] idx = new int[e];
        for (int i = 0; i < e; i++) idx[i] = i;
        Sorting.sortByWeight(idx, e, g.edgeWeights(), m);

        DSU dsu = new DSU(n);
        int cap = Math.max(n - 1, 0);
        int[] tu = new int[cap], tv = new int[cap], tw = new int[cap];
        int taken = 0;
        long total = 0;
        for (int k = 0; k < e; k++) {
            if (dsu.components() <= 1) break;
            int i = idx[k];
            m.edgeScans++;
            int u = g.edgeU(i), v = g.edgeV(i);
            if (dsu.find(u) != dsu.find(v)) {
                dsu.union(u, v);
                tu[taken] = u; tv[taken] = v; tw[taken] = g.edgeW(i);
                total += g.edgeW(i);
                taken++;
            }
        }
        m.dsuOps += dsu.ops();
        return new MstResult(total, dsu.components(), taken, tu, tv, tw);
    }
}
