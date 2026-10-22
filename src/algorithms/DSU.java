package algorithms;

public final class DSU {
    private int[] parent;
    private int[] rank;
    private int sets;
    private long finds;
    private long unions;

    public DSU(int n) {
        makeSet(n);
    }

    public void makeSet(int n) {
        parent = new int[n];
        rank = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;
        sets = n;
        finds = 0;
        unions = 0;
    }

    public int find(int x) {
        finds++;
        return root(x);
    }

    public boolean union(int x, int y) {
        int rx = root(x), ry = root(y);
        if (rx == ry) return false;
        if (rank[rx] < rank[ry]) { int t = rx; rx = ry; ry = t; }
        parent[ry] = rx;
        if (rank[rx] == rank[ry]) rank[rx]++;
        sets--;
        unions++;
        return true;
    }

    public int components() { return sets; }
    public long finds() { return finds; }
    public long unions() { return unions; }
    public long ops() { return finds + unions; }

    private int root(int x) {
        int r = x;
        while (parent[r] != r) r = parent[r];
        while (parent[x] != r) { int nx = parent[x]; parent[x] = r; x = nx; }
        return r;
    }
}
