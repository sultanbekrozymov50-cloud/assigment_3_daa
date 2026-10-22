package graph;

import java.util.Random;

public final class GraphGenerator {
    public static final long SEED = 42;

    private GraphGenerator() {}

    private static int[] shuffled(int n, Random rnd) {
        int[] p = new int[n];
        for (int i = 0; i < n; i++) p[i] = i;
        for (int i = n - 1; i > 0; i--) {
            int j = rnd.nextInt(i + 1);
            int t = p[i]; p[i] = p[j]; p[j] = t;
        }
        return p;
    }

    private static int weight(Random rnd) {
        return 1 + rnd.nextInt(1000);
    }

    public static Graph undirected(int n, int m) {
        Random rnd = new Random(SEED);
        Graph g = new Graph(n, false, m);
        int[] perm = shuffled(n, rnd);
        for (int i = 1; i < n; i++) {
            int u = perm[rnd.nextInt(i)];
            int v = perm[i];
            g.addEdge(u, v, weight(rnd));
        }
        for (int k = n - 1; k < m; k++) {
            int a = rnd.nextInt(n), b;
            do { b = rnd.nextInt(n); } while (b == a);
            g.addEdge(a, b, weight(rnd));
        }
        return g;
    }

    public static Graph directed(int n, int m) {
        Random rnd = new Random(SEED);
        Graph g = new Graph(n, true, m);
        for (int k = 0; k < m; k++) {
            int a = rnd.nextInt(n), b;
            do { b = rnd.nextInt(n); } while (b == a);
            g.addEdge(a, b, weight(rnd));
        }
        return g;
    }

    public static Graph dag(int n, int m) {
        Random rnd = new Random(SEED);
        Graph g = new Graph(n, true, m);
        int[] rank = shuffled(n, rnd);
        for (int k = 0; k < m; k++) {
            int a = rnd.nextInt(n), b;
            do { b = rnd.nextInt(n); } while (b == a);
            if (rank[a] > rank[b]) { int t = a; a = b; b = t; }
            g.addEdge(a, b, weight(rnd));
        }
        return g;
    }
}
