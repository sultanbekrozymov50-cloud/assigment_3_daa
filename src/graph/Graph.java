package graph;

import java.util.Arrays;


public final class Graph {
    private final int n;
    private final boolean directed;
    private final int[] head;

    private int[] arcNext, arcTo, arcWeight, arcFrom;
    private int arcCount;

    private int[] eu, ev, ew;
    private int edgeCount;

    public Graph(int n, boolean directed) {
        this(n, directed, 16);
    }

    public Graph(int n, boolean directed, int expectedEdges) {
        if (n < 0) throw new IllegalArgumentException("n < 0");
        this.n = n;
        this.directed = directed;
        this.head = new int[n];
        Arrays.fill(head, -1);
        int e = Math.max(expectedEdges, 4);
        int a = directed ? e : 2 * e;
        eu = new int[e];
        ev = new int[e];
        ew = new int[e];
        arcNext = new int[a];
        arcTo = new int[a];
        arcWeight = new int[a];
        arcFrom = new int[a];
    }

    public void addEdge(int u, int v, int w) {
        if (u < 0 || u >= n || v < 0 || v >= n)
            throw new IllegalArgumentException("vertex out of range: " + u + " " + v + " (n=" + n + ")");
        if (edgeCount == eu.length) {
            int c = eu.length * 2;
            eu = Arrays.copyOf(eu, c);
            ev = Arrays.copyOf(ev, c);
            ew = Arrays.copyOf(ew, c);
        }
        eu[edgeCount] = u;
        ev[edgeCount] = v;
        ew[edgeCount] = w;
        edgeCount++;
        addArc(u, v, w);
        if (!directed && u != v) addArc(v, u, w);
    }

    private void addArc(int u, int v, int w) {
        if (arcCount == arcTo.length) {
            int c = arcTo.length * 2;
            arcNext = Arrays.copyOf(arcNext, c);
            arcTo = Arrays.copyOf(arcTo, c);
            arcWeight = Arrays.copyOf(arcWeight, c);
            arcFrom = Arrays.copyOf(arcFrom, c);
        }
        arcFrom[arcCount] = u;
        arcTo[arcCount] = v;
        arcWeight[arcCount] = w;
        arcNext[arcCount] = head[u];
        head[u] = arcCount++;
    }

    public int n() { return n; }
    public int m() { return edgeCount; }
    public boolean isDirected() { return directed; }

    public int firstArc(int v) { return head[v]; }
    public int nextArc(int a) { return arcNext[a]; }
    public int arcTo(int a) { return arcTo[a]; }
    public int arcFrom(int a) { return arcFrom[a]; }
    public int arcWeight(int a) { return arcWeight[a]; }
    public int arcCount() { return arcCount; }

    public int edgeU(int i) { return eu[i]; }
    public int edgeV(int i) { return ev[i]; }
    public int edgeW(int i) { return ew[i]; }
    public int[] edgeWeights() { return ew; }

    public int[] inDegrees() {
        int[] d = new int[n];
        for (int i = 0; i < edgeCount; i++) d[ev[i]]++;
        return d;
    }

    public Graph reverse() {
        if (!directed) throw new IllegalStateException("reverse() needs a directed graph");
        Graph r = new Graph(n, true, edgeCount);
        for (int i = 0; i < edgeCount; i++) r.addEdge(ev[i], eu[i], ew[i]);
        return r;
    }
}
