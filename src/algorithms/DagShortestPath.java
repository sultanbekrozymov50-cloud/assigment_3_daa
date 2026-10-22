package algorithms;

import graph.Graph;
import metrics.Metrics;


public final class DagShortestPath {
    public static final long INF = Long.MAX_VALUE;

    public final long[] dist;
    public final int[] parent;

    private DagShortestPath(long[] dist, int[] parent) {
        this.dist = dist;
        this.parent = parent;
    }

    public static DagShortestPath run(Graph g, Metrics m) {
        TopoResult t = Kahn.run(g, new Metrics());
        return run(g, t, m);
    }

    public static DagShortestPath run(Graph g, TopoResult topo, Metrics m) {
        if (topo.hasCycle) throw new IllegalArgumentException("graph has a cycle: shortest paths via topological order are undefined");
        int n = g.n();
        long[] dist = new long[n];
        int[] parent = new int[n];
        java.util.Arrays.fill(dist, INF);
        java.util.Arrays.fill(parent, -1);
        int[] indeg = g.inDegrees();
        for (int v = 0; v < n; v++) if (indeg[v] == 0) dist[v] = 0;

        for (int i = 0; i < topo.count; i++) {
            int u = topo.order[i];
            if (dist[u] == INF) continue;
            for (int a = g.firstArc(u); a != -1; a = g.nextArc(a)) {
                m.edgeScans++;
                int v = g.arcTo(a);
                long cand = dist[u] + g.arcWeight(a);
                m.comparisons++;
                if (cand < dist[v]) {
                    dist[v] = cand;
                    parent[v] = u;
                }
            }
        }
        return new DagShortestPath(dist, parent);
    }

    public long sumOfDistances() {
        long s = 0;
        for (long d : dist) if (d != INF) s += d;
        return s;
    }

    public int[] pathTo(int v) {
        int len = 0;
        for (int x = v; x != -1; x = parent[x]) len++;
        int[] p = new int[len];
        for (int x = v, i = len - 1; x != -1; x = parent[x]) p[i--] = x;
        return p;
    }
}
