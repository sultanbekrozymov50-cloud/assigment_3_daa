package benchmark;

import algorithms.*;
import graph.Graph;
import graph.GraphGenerator;
import metrics.Metrics;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.ToLongFunction;

public final class Benchmark {
    static final int[] SIZES = {1_000, 3_162, 10_000, 31_623, 100_000};
    static final int[] DENSITIES = {4, 20};
    static final int RUNS = 5;

    record Row(String algorithm, String graph, int n, int m, double timeMs, Metrics mt, long result) {
        String csv() {
            return String.format(java.util.Locale.ROOT, "%s,%s,%d,%d,%.3f,%d,%d,%d,%d,%d",
                    algorithm, graph, n, m, timeMs, mt.dsuOps, mt.edgeScans, mt.comparisons, mt.maxDepth, result);
        }
    }

    static Row measure(String algo, String graphName, Graph g, ToLongFunction<Metrics> body) {
        double[] t = new double[RUNS];
        Metrics last = null;
        long result = 0;
        for (int r = 0; r < RUNS; r++) {
            Metrics mt = new Metrics();
            long t0 = System.nanoTime();
            long res = body.applyAsLong(mt);
            t[r] = (System.nanoTime() - t0) / 1e6;
            if (last != null && (res != result || mt.edgeScans != last.edgeScans || mt.comparisons != last.comparisons
                    || mt.dsuOps != last.dsuOps || mt.maxDepth != last.maxDepth))
                throw new IllegalStateException(algo + " is not deterministic on " + graphName);
            last = mt;
            result = res;
        }
        Arrays.sort(t);
        return new Row(algo, graphName, g.n(), g.m(), t[RUNS / 2], last, result);
    }

    static void check(boolean ok, String msg) {
        if (!ok) throw new IllegalStateException("BENCHMARK CHECK FAILED: " + msg);
    }

    public static void main(String[] args) throws IOException {
        Path outDir = Path.of(args.length > 0 ? args[0] : "results");
        int maxN = args.length > 1 ? Integer.parseInt(args[1]) : Integer.MAX_VALUE;
        Files.createDirectories(outDir);
        List<Row> rows = new ArrayList<>();

        System.out.printf("%-14s %9s %9s | %12s %12s %-3s | %6s %6s %-3s | %12s %12s %-3s%n",
                "graph", "n", "m", "MST Kruskal", "MST Prim", "ok", "SCC", "ref", "ok", "DAG sum", "ref", "ok");

        for (int n : SIZES) {
            if (n > maxN) continue;
            for (int d : DENSITIES) {
                int m = n * d;
                String name = "n" + n + (d == 4 ? "_sparse" : "_dense");
                long[] ref = Reference.find(n, d);

                Graph ug = GraphGenerator.undirected(n, m);
                Row kr = measure("Kruskal", name, ug, mt -> Kruskal.run(ug, mt).totalWeight);
                Row pr = measure("Prim", name, ug, mt -> Prim.run(ug, mt).totalWeight);
                check(kr.result == pr.result, "Kruskal (" + kr.result + ") != Prim (" + pr.result + ") on " + name);
                MstResult kt = Kruskal.run(ug, new Metrics()), pt = Prim.run(ug, new Metrics());
                check(kt.edgeCount == n - 1 && pt.edgeCount == n - 1, "MST must have n-1 edges on " + name);
                check(kt.components == 1 && pt.components == 1, "generated undirected graph must be connected: " + name);
                check(kr.result == ref[2], "MST weight " + kr.result + " != reference " + ref[2] + " on " + name);

                Graph dg = GraphGenerator.directed(n, m);
                Row ko = measure("Kosaraju", name, dg, mt -> Kosaraju.run(dg, mt).count);
                check(ko.result == ref[3], "SCC count " + ko.result + " != reference " + ref[3] + " on " + name);
                check(ko.mt.edgeScans == 2L * m, "Kosaraju must scan exactly 2m edges on " + name);

                Graph dag = GraphGenerator.dag(n, m);
                Row ka = measure("Kahn", name, dag, mt -> Kahn.run(dag, mt).count);
                check(ka.result == n, "Kahn must order all n vertices of a DAG on " + name);
                check(ka.mt.edgeScans == m, "Kahn must scan exactly m edges on " + name);
                check(Kahn.isValidOrder(dag, Kahn.run(dag, new Metrics())), "invalid topological order on " + name);
                Row ds = measure("DagShortestPath", name, dag, mt -> DagShortestPath.run(dag, mt).sumOfDistances());
                check(ds.result == ref[4], "DAG distance sum " + ds.result + " != reference " + ref[4] + " on " + name);
                check(ds.mt.edgeScans == m, "DAG relaxation must scan exactly m edges on " + name);

                rows.add(kr); rows.add(pr); rows.add(ko); rows.add(ka); rows.add(ds);
                System.out.printf("%-14s %9d %9d | %12d %12d %-3s | %6d %6d %-3s | %12d %12d %-3s%n",
                        name, n, m, kr.result, pr.result, "OK", ko.result, ref[3], "OK", ds.result, ref[4], "OK");
            }
        }

        write(outDir.resolve("results.csv"), rows);
        System.out.println("\nAll checks passed. Wrote " + rows.size() + " rows to " + outDir.resolve("results.csv")
                );
    }

    static void write(Path file, List<Row> rows) throws IOException {
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(file))) {
            pw.println("algorithm,graph,n,m,time_ms,dsu_ops,edge_scans,comparisons,max_depth,result");
            for (Row r : rows) pw.println(r.csv());
        }
    }
}
