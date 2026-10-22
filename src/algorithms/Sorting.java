package algorithms;

import metrics.Metrics;

final class Sorting {
    private Sorting() {}

    static void sortByWeight(int[] idx, int len, int[] w, Metrics m) {
        int[] src = idx, dst = new int[len];
        for (int width = 1; width < len; width <<= 1) {
            for (int lo = 0; lo < len; lo += 2 * width) {
                int mid = Math.min(lo + width, len), hi = Math.min(lo + 2 * width, len);
                int i = lo, j = mid, k = lo;
                while (i < mid && j < hi) {
                    m.comparisons++;
                    if (w[src[i]] <= w[src[j]]) dst[k++] = src[i++];
                    else dst[k++] = src[j++];
                }
                while (i < mid) dst[k++] = src[i++];
                while (j < hi) dst[k++] = src[j++];
            }
            int[] t = src; src = dst; dst = t;
        }
        if (src != idx) System.arraycopy(src, 0, idx, 0, len);
    }
}
