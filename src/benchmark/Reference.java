package benchmark;

final class Reference {
    private Reference() {}

    static final long[][] TABLE = {
        {1000, 4, 147970, 27, 396274},
        {1000, 20, 30363, 1, 252411},
        {3162, 4, 468911, 137, 1271911},
        {3162, 20, 97589, 1, 723157},
        {10000, 4, 1486139, 373, 3941828},
        {10000, 20, 309550, 1, 2381342},
        {31623, 4, 4715515, 1188, 12551891},
        {31623, 20, 968517, 1, 7376612},
        {100000, 4, 15012094, 3923, 40096539},
        {100000, 20, 3056681, 1, 23512087},
    };

    static long[] find(int n, int density) {
        for (long[] r : TABLE) if (r[0] == n && r[1] == density) return r;
        throw new IllegalArgumentException("no reference row for n=" + n + " density=" + density);
    }
}
