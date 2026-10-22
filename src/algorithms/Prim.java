package algorithms;

import graph.Graph;
import metrics.Metrics;

public final class Prim {
    private Prim() {}

    public static MstResult run(Graph g, Metrics m) {
        if (g.isDirected()) throw new IllegalArgumentException("MST needs an undirected graph");
        int n = g.n();
        boolean[] inTree = new boolean[n];
        int cap = Math.max(n - 1, 0);
        int[] tu = new int[cap], tv = new int[cap], tw = new int[cap];
        int taken = 0, components = 0;
        long total = 0;
        LongMinHeap pq = new LongMinHeap(m);

        for (int s = 0; s < n; s++) {
            if (inTree[s]) continue;
            components++;
            inTree[s] = true;
            scan(g, s, inTree, pq, m);
            while (!pq.isEmpty()) {
                long key = pq.pop();
                int arc = (int) (key & 0xFFFFFFFFL);
                int v = g.arcTo(arc);
                if (inTree[v]) continue;
                inTree[v] = true;
                int w = g.arcWeight(arc);
                tu[taken] = g.arcFrom(arc); tv[taken] = v; tw[taken] = w;
                total += w;
                taken++;
                scan(g, v, inTree, pq, m);
            }
        }
        return new MstResult(total, components, taken, tu, tv, tw);
    }

    private static void scan(Graph g, int u, boolean[] inTree, LongMinHeap pq, Metrics m) {
        for (int a = g.firstArc(u); a != -1; a = g.nextArc(a)) {
            m.edgeScans++;
            if (!inTree[g.arcTo(a)]) pq.push(((long) g.arcWeight(a) << 32) | a);
        }
    }
}
