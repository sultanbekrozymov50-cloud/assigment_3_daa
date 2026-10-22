package algorithms;

public final class TopoResult {
    public final int[] order;
    public final int count;
    public final boolean hasCycle;

    TopoResult(int[] order, int count, boolean hasCycle) {
        this.order = order;
        this.count = count;
        this.hasCycle = hasCycle;
    }

    public int[] positions(int n) {
        int[] pos = new int[n];
        java.util.Arrays.fill(pos, -1);
        for (int i = 0; i < count; i++) pos[order[i]] = i;
        return pos;
    }
}
