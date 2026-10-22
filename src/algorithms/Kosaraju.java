package algorithms;

import graph.Graph;
import metrics.Metrics;

public final class Kosaraju {
    private Kosaraju() {}

    public static SccResult run(Graph g, Metrics m) {
        if (!g.isDirected()) throw new IllegalArgumentException("SCC needs a directed graph");
        int n = g.n();
        int[] order = new int[n];
        int fin = 0;
        boolean[] seen = new boolean[n];
        int[] stack = new int[n];
        int[] ptr = new int[n];
        for (int v = 0; v < n; v++) ptr[v] = g.firstArc(v);

        for (int r = 0; r < n; r++) {
            if (seen[r]) continue;
            int sp = 0;
            seen[r] = true;
            stack[sp++] = r;
            m.observeDepth(sp);
            while (sp > 0) {
                int u = stack[sp - 1];
                int a = ptr[u];
                if (a != -1) {
                    ptr[u] = g.nextArc(a);
                    m.edgeScans++;
                    int v = g.arcTo(a);
                    if (!seen[v]) {
                        seen[v] = true;
                        stack[sp++] = v;
                        m.observeDepth(sp);
                    }
                } else {
                    sp--;
                    order[fin++] = u;
                }
            }
        }

        Graph rg = g.reverse();
        int[] comp = new int[n];
        java.util.Arrays.fill(comp, -1);
        for (int v = 0; v < n; v++) ptr[v] = rg.firstArc(v);
        int count = 0;
        for (int i = n - 1; i >= 0; i--) {
            int r = order[i];
            if (comp[r] != -1) continue;
            int sp = 0;
            comp[r] = count;
            stack[sp++] = r;
            m.observeDepth(sp);
            while (sp > 0) {
                int u = stack[sp - 1];
                int a = ptr[u];
                if (a != -1) {
                    ptr[u] = rg.nextArc(a);
                    m.edgeScans++;
                    int v = rg.arcTo(a);
                    if (comp[v] == -1) {
                        comp[v] = count;
                        stack[sp++] = v;
                        m.observeDepth(sp);
                    }
                } else {
                    sp--;
                }
            }
            count++;
        }
        return new SccResult(comp, count);
    }
}
