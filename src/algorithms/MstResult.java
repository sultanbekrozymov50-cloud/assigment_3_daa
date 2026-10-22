package algorithms;

public final class MstResult {
    public final long totalWeight;
    public final int components;
    public final int edgeCount;
    public final int[] u, v, w;

    MstResult(long totalWeight, int components, int edgeCount, int[] u, int[] v, int[] w) {
        this.totalWeight = totalWeight;
        this.components = components;
        this.edgeCount = edgeCount;
        this.u = u;
        this.v = v;
        this.w = w;
    }
}
