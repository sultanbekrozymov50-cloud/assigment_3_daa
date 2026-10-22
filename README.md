## Project layout

* src/main/java/graph/ Graph (adjacency lists), GraphIO (text format), GraphGenerator (exact generator from the task)
* src/main/java/algorithms/ DSU, Kruskal, Prim, Kosaraju, Kahn, DagShortestPath
* src/main/java/metrics/ Metrics (dsuOps, edgeScans, comparisons, maxDepth)
* src/main/java/benchmark/ Benchmark (self-checking), Reference (the reference table)
* src/test/java/algorithms/ JUnit 5 tests
* data/ small_connected, small_disconnected, small_cyclic, small_dag
* results/results.csv Benchmark output
* scripts/ plots.py
* REPORT.md Complexity table, counters vs formulas, correctness arguments, discussion

## Runs all JUnit 5 tests:
27 JUnit 5 tests — correctness against brute force and Bellman-Ford, plus a 100 000-vertex stack test.
Edge cases tested include isolated vertices, self-loops, parallel edges, and negative weights in DAGs (small_disconnected, small_cyclic, small_dag).
java.util collections and reference algorithms (like Bellman-Ford) are used only inside the tests, as the "known-correct" reference to compare against — never inside the algorithms themselves.

## Run the benchmark
Runs the self-checking benchmark for all six algorithms (DSU, Kruskal, Prim, Kosaraju, Kahn, and shortest paths in a DAG). It checks the results against the reference table of the assignment and fails loudly if anything is off. All data is generated from new Random(42) to give the same counters on every machine, and writes results/results.csv with 50 rows.
Run via Maven: mvn compile exec:exec (takes about 30 s)
Run directly: java -Xmx2g -cp target/classes benchmark.Benchmark results [maxN] (where maxN limits the sizes, e.g., 10000 for a quick run).
Without Maven (build + benchmark + plots in one go): sh scripts/run_benchmark.sh.

## Charts
Charts were built using Python 3 with matplotlib and numpy. To generate them from your benchmark run, execute python3 scripts/plots.py. The script reads results/results.csv and outputs 3 PNG plots in results/plots/, as well as generates the formatted tables used in REPORT.md.

## Design highlights
Graph stores adjacency lists (directed or undirected). Parses input format: n m directed|undirected followed by m lines of u v w.
DFS is iterative, utilizing its own int[] stack and a per-vertex arc pointer, ensuring 100 000 vertices are processed fine without StackOverflowError.
Sorting and the binary heap are own custom implementations (used in Kruskal and Prim respectively).
DagShortestPath uses a virtual super-source (every in-degree-0 vertex starts at 0).
Metrics counts exact operations: dsuOps (find calls + successful union calls), edgeScans (edges/arcs looked at), comparisons (weight/key comparisons in merge sort, heap sift, or relaxations), and maxDepth (deepest the explicit DFS stack got).
See REPORT.md for the full complexity table, counters vs formulas, correctness arguments, the "which algorithm when" discussion, and plots.
