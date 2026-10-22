import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

import algorithms.DSU;
import org.junit.jupiter.api.Test;

class DsuTest {
    @Test
    void agreesWithReferenceAfterRandomUnions() {
        Random rnd = new Random(1);
        for (int round = 0; round < 50; round++) {
            int n = 1 + rnd.nextInt(200);
            DSU dsu = new DSU(n);
            int[] label = new int[n];
            for (int i = 0; i < n; i++) label[i] = i;
            int ops = rnd.nextInt(3 * n);
            for (int k = 0; k < ops; k++) {
                int x = rnd.nextInt(n), y = rnd.nextInt(n);
                boolean differentBefore = label[x] != label[y];
                assertEquals(differentBefore, dsu.union(x, y));
                if (differentBefore) {
                    int old = label[y], nw = label[x];
                    for (int i = 0; i < n; i++) if (label[i] == old) label[i] = nw;
                }
            }
            for (int x = 0; x < n; x++)
                for (int y = 0; y < n; y++)
                    assertEquals(label[x] == label[y], dsu.find(x) == dsu.find(y));
            long distinct = java.util.Arrays.stream(label).distinct().count();
            assertEquals(distinct, dsu.components());
        }
    }

    @Test
    void countersCountFindsAndSuccessfulUnionsOnly() {
        DSU d = new DSU(4);
        assertTrue(d.union(0, 1));
        assertFalse(d.union(1, 0));
        d.find(2);
        d.find(3);
        assertEquals(1, d.unions());
        assertEquals(2, d.finds());
        assertEquals(3, d.ops());
        assertEquals(3, d.components());
    }

    @Test
    void makeSetResets() {
        DSU d = new DSU(3);
        d.union(0, 1);
        d.makeSet(5);
        assertEquals(5, d.components());
        assertEquals(0, d.ops());
    }

    @Test
    void longChainIsStackSafe() {
        int n = 200_000;
        DSU d = new DSU(n);
        for (int i = 1; i < n; i++) d.union(i - 1, i);
        assertEquals(1, d.components());
        assertEquals(d.find(0), d.find(n - 1));
    }
}
