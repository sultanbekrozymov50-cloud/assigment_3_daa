package graph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public final class GraphIO {
    private GraphIO() {}

    public static Graph read(Path file) throws IOException {
        try (BufferedReader br = Files.newBufferedReader(file)) {
            String line = nextLine(br);
            if (line == null) throw new IOException("empty file: " + file);
            String[] h = line.split("\\s+");
            if (h.length != 3) throw new IOException("bad header (expected 'n m directed|undirected'): " + line);
            int n = Integer.parseInt(h[0]);
            int m = Integer.parseInt(h[1]);
            boolean directed;
            if (h[2].equalsIgnoreCase("directed")) directed = true;
            else if (h[2].equalsIgnoreCase("undirected")) directed = false;
            else throw new IOException("third header token must be directed|undirected: " + h[2]);
            Graph g = new Graph(n, directed, m);
            for (int i = 0; i < m; i++) {
                line = nextLine(br);
                if (line == null) throw new IOException("expected " + m + " edges, found " + i);
                String[] t = line.split("\\s+");
                if (t.length != 3) throw new IOException("bad edge line: " + line);
                g.addEdge(Integer.parseInt(t[0]), Integer.parseInt(t[1]), Integer.parseInt(t[2]));
            }
            return g;
        }
    }

    public static void write(Graph g, Path file) throws IOException {
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(file))) {
            pw.println(g.n() + " " + g.m() + " " + (g.isDirected() ? "directed" : "undirected"));
            for (int i = 0; i < g.m(); i++) pw.println(g.edgeU(i) + " " + g.edgeV(i) + " " + g.edgeW(i));
        }
    }

    private static String nextLine(BufferedReader br) throws IOException {
        String s;
        while ((s = br.readLine()) != null) {
            s = s.trim();
            if (!s.isEmpty() && !s.startsWith("#")) return s;
        }
        return null;
    }
}
