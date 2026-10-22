import graph.Graph;

import java.nio.file.Path;
import java.util.Random;

final class TestUtil {
    private TestUtil() {}

    static Path data(String name) {
        return Path.of("data", name);
    }

    static Graph randomConnected(Random rnd, int n, int extra, int maxW) {
        Graph g = new Graph(n, false);
        for (int i = 1; i < n; i++) g.addEdge(rnd.nextInt(i), i, 1 + rnd.nextInt(maxW));
        for (int k = 0; k < extra; k++) g.addEdge(rnd.nextInt(n), rnd.nextInt(n), 1 + rnd.nextInt(maxW));
        return g;
    }

    static Graph randomDirected(Random rnd, int n, int m) {
        Graph g = new Graph(n, true);
        for (int k = 0; k < m; k++) g.addEdge(rnd.nextInt(n), rnd.nextInt(n), 1 + rnd.nextInt(20));
        return g;
    }

    static Graph randomDag(Random rnd, int n, int m) {
        int[] rank = new int[n];
        for (int i = 0; i < n; i++) rank[i] = i;
        for (int i = n - 1; i > 0; i--) { int j = rnd.nextInt(i + 1); int t = rank[i]; rank[i] = rank[j]; rank[j] = t; }
        Graph g = new Graph(n, true);
        for (int k = 0; k < m; k++) {
            int a = rnd.nextInt(n), b = rnd.nextInt(n);
            if (a == b) continue;
            if (rank[a] > rank[b]) { int t = a; a = b; b = t; }
            g.addEdge(a, b, rnd.nextInt(31) - 10);
        }
        return g;
    }
}
