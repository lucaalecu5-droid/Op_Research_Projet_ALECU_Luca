import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {
	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			runDefault();
			return;
		}

		switch (args[0]) {
			case "maxflow":
				runMaxFlowFromFile(args.length > 1 ? args[1] : "graph_data.txt");
				break;
			case "mincost-bf":
				runMinCostFromFile(args.length > 1 ? args[1] : "graph_data.txt", false);
				break;
			case "mincost-dij":
				runMinCostFromFile(args.length > 1 ? args[1] : "graph_data.txt", true);
				break;
			case "examples":
				runExamples();
				break;
			default:
				printUsage();
				break;
		}
	}

	private static void runDefault() throws Exception {
		Path data = Paths.get("graph_data.txt");
		if (Files.exists(data)) {
			System.out.println("=== Max flow (graph_data.txt) ===");
			runMaxFlowFromFile(data.toString());
			System.out.println();
		}
		runExamples();
	}

	private static void printUsage() {
		System.out.println("Usage:");
		System.out.println("  java Main maxflow [fichier]");
		System.out.println("  java Main mincost-bf [fichier]");
		System.out.println("  java Main mincost-dij [fichier]");
		System.out.println("  java Main examples");
	}

	private static void runMaxFlowFromFile(String path) throws IOException {
		ParsedGraph parsed = readGraphFromFile(path);
		MaxFlowResult result = parsed.graph.maxFlowMinCut(parsed.source, parsed.sink);
		System.out.println("Flot max: " + result.maxFlow);
		for (Arrete e : parsed.graph.getEdges()) {
			System.out.println(e.from + " -> " + e.to + " : " + e.flow + "/" + e.capacity + " cost=" + e.cost);
		}
		System.out.println("Min cut capacity: " + result.minCutCapacity);
		for (Arrete e : result.minCutEdges) {
			System.out.println("  cut: " + e.from + " -> " + e.to + " cap=" + e.capacity);
		}
		String outputPath = buildOutputPath(path, "_maxflow.gv");
		GraphVizWriter.writeGraph(parsed.graph, outputPath, parsed.source, parsed.sink);
		System.out.println("GraphViz: " + outputPath);
	}

	private static void runMinCostFromFile(String path, boolean usePotentials) throws IOException {
		ParsedGraph parsed = readGraphFromFile(path);
		MinCostFlowResult result = usePotentials
				? parsed.graph.minCostFlowDijkstra(parsed.source, parsed.sink, Integer.MAX_VALUE)
				: parsed.graph.minCostFlowBellmanFord(parsed.source, parsed.sink, Integer.MAX_VALUE);
		System.out.println("Flot: " + result.flow + " cout: " + result.cost);
		for (Arrete e : parsed.graph.getEdges()) {
			if (e.flow > 0) {
				System.out.println(e.from + " -> " + e.to + " : " + e.flow + "/" + e.capacity + " cost=" + e.cost);
			}
		}
		String suffix = usePotentials ? "_mincost_dij.gv" : "_mincost_bf.gv";
		String outputPath = buildOutputPath(path, suffix);
		GraphVizWriter.writeGraph(parsed.graph, outputPath, parsed.source, parsed.sink);
		System.out.println("GraphViz: " + outputPath);
	}

	private static ParsedGraph readGraphFromFile(String path) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(Paths.get(path))) {
			String line;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (!line.isEmpty()) {
					String[] first = line.split("\\s+");
					int n = Integer.parseInt(first[0]);
					int m = Integer.parseInt(first[1]);
					int source = Integer.parseInt(first[2]);
					int sink = Integer.parseInt(first[3]);
					Graph graph = new Graph(n);
					int count = 0;
					while ((line = reader.readLine()) != null && count < m) {
						line = line.trim();
						if (line.isEmpty()) {
							continue;
						}
						String[] parts = line.split("\\s+");
						int u = Integer.parseInt(parts[0]);
						int v = Integer.parseInt(parts[1]);
						int cap = Integer.parseInt(parts[2]);
						int cost = Integer.parseInt(parts[3]);
						graph.addEdge(u, v, cap, cost);
						count++;
					}
					return new ParsedGraph(graph, source, sink);
				}
			}
		}
		throw new IOException("Fichier vide ou format invalide: " + path);
	}

	private static void runExamples() throws IOException {
		System.out.println("=== Exemple 1: Max flow ===");
		int[] start = {0, 0, 0, 1, 1, 2, 2, 3, 3};
		int[] end = {1, 2, 3, 2, 4, 3, 4, 2, 4};
		int[] caps = {20, 30, 10, 40, 30, 10, 20, 5, 20};
		Graph g1 = buildGraphFromArrays(5, start, end, caps, null);
		MaxFlowResult maxFlow = g1.maxFlowMinCut(0, 4);
		System.out.println("Flot max (0 -> 4): " + maxFlow.maxFlow);
		for (Arrete e : g1.getEdges()) {
			System.out.println(e.from + " -> " + e.to + " : " + e.flow + "/" + e.capacity);
		}
		GraphVizWriter.writeGraph(g1, "example1_maxflow.gv", 0, 4);
		System.out.println();

		System.out.println("=== Exemple 2: Assignment min cost ===");
		int[][] cost = {
				{90, 76, 75, 70, 50, 74, 12, 68},
				{35, 85, 55, 65, 48, 101, 70, 83},
				{125, 95, 90, 105, 59, 120, 36, 73},
				{45, 110, 95, 115, 104, 83, 37, 71},
				{60, 105, 80, 75, 59, 62, 93, 88},
				{45, 65, 110, 95, 47, 31, 81, 34},
				{38, 51, 107, 41, 69, 99, 115, 48},
				{47, 85, 57, 71, 92, 77, 109, 36},
				{39, 63, 97, 49, 118, 56, 92, 61},
				{47, 101, 71, 60, 88, 109, 52, 90}
		};
		solveAssignment(cost, cost.length, "Sans capacite sur les taches", "example2_assignment_nocap.gv");
		solveAssignment(cost, 2, "Capacite 2 par tache", "example2_assignment_cap2.gv");
		solveAssignmentWithTaskMin(cost, 1, "Chaque tache prise au moins une fois", "example2_assignment_min1.gv");
		System.out.println();

		System.out.println("=== Exemple 3: Min cost flow ===");
		runMinCostFlowExample();
	}

	private static Graph buildGraphFromArrays(int n, int[] start, int[] end, int[] caps, int[] costs) {
		Graph g = new Graph(n);
		for (int i = 0; i < start.length; i++) {
			int cost = costs == null ? 0 : costs[i];
			g.addEdge(start[i], end[i], caps[i], cost);
		}
		return g;
	}

	private static void solveAssignment(int[][] cost, int taskCap, String label, String outputFile) throws IOException {
		int people = cost.length;
		int tasks = cost[0].length;
		int source = people + tasks;
		int sink = source + 1;
		Graph g = new Graph(sink + 1);

		for (int i = 0; i < people; i++) {
			g.addEdge(source, i, 1, 0);
		}
		int cap = taskCap <= 0 ? people : taskCap;
		for (int j = 0; j < tasks; j++) {
			g.addEdge(people + j, sink, cap, 0);
		}
		for (int i = 0; i < people; i++) {
			for (int j = 0; j < tasks; j++) {
				g.addEdge(i, people + j, 1, cost[i][j]);
			}
		}

		MinCostFlowResult result = g.minCostFlowDijkstra(source, sink, people);
		System.out.println(label + ": flow=" + result.flow + " cost=" + result.cost);
		printAssignments(g, people, tasks);
		GraphVizWriter.writeGraph(g, outputFile, source, sink);
	}

	private static void solveAssignmentWithTaskMin(int[][] cost, int taskMin, String label, String outputFile)
			throws IOException {
		int people = cost.length;
		int tasks = cost[0].length;
		int n = people + tasks + 2;
		int source = people + tasks;
		int sink = source + 1;

		List<BoundedEdge> edges = new ArrayList<>();

		for (int i = 0; i < people; i++) {
			edges.add(new BoundedEdge(source, i, 1, 1, 0));
		}

		int cap = people;
		for (int j = 0; j < tasks; j++) {
			edges.add(new BoundedEdge(people + j, sink, taskMin, cap, 0));
		}

		for (int i = 0; i < people; i++) {
			for (int j = 0; j < tasks; j++) {
				edges.add(new BoundedEdge(i, people + j, 0, 1, cost[i][j]));
			}
		}

		edges.add(new BoundedEdge(sink, source, people, people, 0));

		LowerBoundResult prepared = buildLowerBoundGraph(n, edges);
		MinCostFlowResult res = prepared.graph.minCostFlowDijkstra(
				prepared.superSource, prepared.superSink, prepared.totalDemand);
		if (res.flow != prepared.totalDemand) {
			System.out.println(label + ": infeasible");
			return;
		}

		long totalCost = prepared.baseCost + res.cost;
		System.out.println(label + ": cost=" + totalCost);
		printAssignmentsFromBounds(prepared.refs, people, tasks);

		Graph display = new Graph(n);
		List<Arrete> displayEdges = new ArrayList<>();
		for (BoundedEdge e : edges) {
			displayEdges.add(display.addEdge(e.from, e.to, e.capacity, e.cost));
		}
		for (int i = 0; i < prepared.refs.size(); i++) {
			BoundedEdgeRef ref = prepared.refs.get(i);
			Arrete edge = displayEdges.get(i);
			int flow = ref.lower + ref.edge.flow;
			edge.flow = flow;
			edge.rev.flow = -flow;
		}
		GraphVizWriter.writeGraph(display, outputFile, source, sink);
	}

	private static LowerBoundResult buildLowerBoundGraph(int n, List<BoundedEdge> edges) {
		int superSource = n;
		int superSink = n + 1;
		Graph g = new Graph(n + 2);
		int[] balance = new int[n];
		List<BoundedEdgeRef> refs = new ArrayList<>();
		long baseCost = 0;

		// Lower-bound transform: adjust balances and capacities.
		for (BoundedEdge e : edges) {
			int cap = e.capacity - e.lower;
			Arrete a = g.addEdge(e.from, e.to, cap, e.cost);
			refs.add(new BoundedEdgeRef(a, e.lower, e.cost, e.from, e.to));
			balance[e.from] -= e.lower;
			balance[e.to] += e.lower;
			baseCost += (long) e.lower * e.cost;
		}

		int totalDemand = 0;
		for (int i = 0; i < n; i++) {
			if (balance[i] > 0) {
				g.addEdge(superSource, i, balance[i], 0);
				totalDemand += balance[i];
			} else if (balance[i] < 0) {
				g.addEdge(i, superSink, -balance[i], 0);
			}
		}

		return new LowerBoundResult(g, superSource, superSink, totalDemand, refs, baseCost);
	}

	private static void printAssignments(Graph g, int people, int tasks) {
		for (Arrete e : g.getEdges()) {
			if (e.from < people && e.to >= people && e.to < people + tasks && e.flow > 0) {
				int person = e.from;
				int task = e.to - people;
				System.out.println("  person " + person + " -> task " + task + " cost=" + e.cost);
			}
		}
	}

	private static void printAssignmentsFromBounds(List<BoundedEdgeRef> refs, int people, int tasks) {
		for (BoundedEdgeRef ref : refs) {
			if (ref.from < people && ref.to >= people && ref.to < people + tasks) {
				int flow = ref.lower + ref.edge.flow;
				if (flow > 0) {
					int person = ref.from;
					int task = ref.to - people;
					System.out.println("  person " + person + " -> task " + task + " cost=" + ref.cost);
				}
			}
		}
	}

	private static void runMinCostFlowExample() throws IOException {
		int[] start = {0, 0, 1, 1, 1, 2, 2, 3, 4};
		int[] end = {1, 2, 2, 3, 4, 3, 4, 4, 2};
		int[] caps = {15, 8, 20, 4, 10, 15, 4, 20, 5};
		int[] costs = {4, 4, 2, 2, 6, 1, 3, 2, 3};

		int s = 5;
		int t = 6;
		Graph g = new Graph(7);
		for (int i = 0; i < start.length; i++) {
			g.addEdge(start[i], end[i], caps[i], costs[i]);
		}
		g.addEdge(s, 0, 20, 0);
		g.addEdge(3, t, 5, 0);
		g.addEdge(4, t, 15, 0);
		g.addEdge(t, s, 20, 0);

		MinCostFlowResult resTs = g.minCostFlowDijkstra(t, s, Integer.MAX_VALUE);
		System.out.println("Max flow min cost (t -> s): flow=" + resTs.flow + " cost=" + resTs.cost);
		GraphVizWriter.writeGraph(g, "example3_mincost_ts.gv", t, s);

		Graph g2 = new Graph(7);
		for (int i = 0; i < start.length; i++) {
			g2.addEdge(start[i], end[i], caps[i], costs[i]);
		}
		g2.addEdge(s, 0, 20, 0);
		g2.addEdge(3, t, 5, 0);
		g2.addEdge(4, t, 15, 0);
		g2.addEdge(t, s, 20, 0);
		MinCostFlowResult res04 = g2.minCostFlowDijkstra(0, 4, Integer.MAX_VALUE);
		System.out.println("Max flow min cost (0 -> 4): flow=" + res04.flow + " cost=" + res04.cost);
		GraphVizWriter.writeGraph(g2, "example3_mincost_04.gv", 0, 4);

		Graph g3 = new Graph(7);
		for (int i = 0; i < start.length; i++) {
			g3.addEdge(start[i], end[i], caps[i], costs[i]);
		}
		g3.addEdge(s, 0, 20, 0);
		g3.addEdge(3, t, 5, 0);
		g3.addEdge(4, t, 15, 0);
		Arrete artificial = g3.addEdge(t, s, 20, 0);

		MaxFlowResult base = g3.maxFlowMinCut(t, s);
		int baseFlow = base.maxFlow;
		int bestDrop = -1;
		Arrete bestEdge = null;
		GraphVizWriter.writeGraph(g3, "example3_vital_base.gv", t, s);

		for (Arrete e : new ArrayList<>(g3.getEdges())) {
			if (e == artificial) {
				continue;
			}
			Graph temp = rebuildGraphWithModifiedEdge(start, end, caps, costs, e.from, e.to, 0, s, t, 20);
			MaxFlowResult tmp = temp.maxFlowMinCut(t, s);
			int drop = baseFlow - tmp.maxFlow;
			if (drop > bestDrop) {
				bestDrop = drop;
				bestEdge = e;
			}
		}

		if (bestEdge != null) {
			System.out.println("Most vital arc (t -> s): " + bestEdge.from + " -> " + bestEdge.to
					+ " drop=" + bestDrop);
		}
	}

	private static String buildOutputPath(String inputPath, String suffix) {
		Path path = Paths.get(inputPath);
		String fileName = path.getFileName().toString();
		int dot = fileName.lastIndexOf('.');
		if (dot > 0) {
			fileName = fileName.substring(0, dot);
		}
		String outputName = fileName + suffix;
		Path parent = path.getParent();
		return parent == null ? outputName : parent.resolve(outputName).toString();
	}

	private static Graph rebuildGraphWithModifiedEdge(int[] start, int[] end, int[] caps, int[] costs,
													  int cutFrom, int cutTo, int newCap,
													  int s, int t, int tsCap) {
		Graph g = new Graph(7);
		for (int i = 0; i < start.length; i++) {
			int cap = caps[i];
			if (start[i] == cutFrom && end[i] == cutTo) {
				cap = newCap;
			}
			g.addEdge(start[i], end[i], cap, costs[i]);
		}
		g.addEdge(s, 0, 20, 0);
		g.addEdge(3, t, 5, 0);
		g.addEdge(4, t, 15, 0);
		g.addEdge(t, s, tsCap, 0);
		return g;
	}
}
