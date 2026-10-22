package algorithms;

import java.util.Arrays;
import metrics.Metrics;

final class LongMinHeap {
    private long[] a = new long[64];
    private int size;
    private final Metrics m;

    LongMinHeap(Metrics m) { this.m = m; }

    boolean isEmpty() { return size == 0; }

    void push(long x) {
        if (size == a.length) a = Arrays.copyOf(a, size * 2);
        int i = size++;
        while (i > 0) {
            int p = (i - 1) >> 1;
            m.comparisons++;
            if (x < a[p]) { a[i] = a[p]; i = p; } else break;
        }
        a[i] = x;
    }

    long pop() {
        long top = a[0];
        long x = a[--size];
        int i = 0;
        while (true) {
            int c = 2 * i + 1;
            if (c >= size) break;
            if (c + 1 < size) {
                m.comparisons++;
                if (a[c + 1] < a[c]) c++;
            }
            m.comparisons++;
            if (a[c] < x) { a[i] = a[c]; i = c; } else break;
        }
        if (size > 0) a[i] = x;
        return top;
    }
}
