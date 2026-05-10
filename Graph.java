import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class Graph {
	private static final long INF = Long.MAX_VALUE / 4;
	private final int n;
	private final List<Arrete>[] adj;
	private final List<Arrete> edges;
	private int nextEdgeId;

	@SuppressWarnings("unchecked")
	public Graph(int n) {
		this.n = n;
		this.adj = new List[n];
		for (int i = 0; i < n; i++) {
			this.adj[i] = new ArrayList<>();
		}
		this.edges = new ArrayList<>();
		this.nextEdgeId = 0;
	}

	public int size() {
		return n;
	}

	public List<Arrete> getEdges() {
		return edges;
	}

	public Arrete addEdge(int from, int to, int capacity, int cost) {
		Arrete forward = new Arrete(from, to, capacity, cost, nextEdgeId++, true);
		Arrete backward = new Arrete(to, from, 0, -cost, -1, false);
		forward.rev = backward;
		backward.rev = forward;
		adj[from].add(forward);
		adj[to].add(backward);
		edges.add(forward);
		return forward;
	}

	public MaxFlowResult maxFlowMinCut(int source, int sink) {
		int flow = 0;
		Arrete[] parent = new Arrete[n];
		while (bfs(source, sink, parent)) {
			int add = Integer.MAX_VALUE;
			for (Arrete e = parent[sink]; e != null; e = parent[e.from]) {
				add = Math.min(add, e.residualCapacity());
			}
			for (Arrete e = parent[sink]; e != null; e = parent[e.from]) {
				e.flow += add;
				e.rev.flow -= add;
			}
			flow += add;
			Arrays.fill(parent, null);
		}

		boolean[] reachable = new boolean[n];
		ArrayDeque<Integer> queue = new ArrayDeque<>();
		reachable[source] = true;
		queue.add(source);
		while (!queue.isEmpty()) {
			int u = queue.poll();
			for (Arrete e : adj[u]) {
				if (e.residualCapacity() > 0 && !reachable[e.to]) {
					reachable[e.to] = true;
					queue.add(e.to);
				}
			}
		}

		List<Arrete> minCutEdges = new ArrayList<>();
		int minCutCapacity = 0;
		for (Arrete e : edges) {
			if (reachable[e.from] && !reachable[e.to] && e.capacity > 0) {
				minCutEdges.add(e);
				minCutCapacity += e.capacity;
			}
		}

		return new MaxFlowResult(flow, minCutCapacity, minCutEdges);
	}

	public MinCostFlowResult minCostFlowBellmanFord(int source, int sink, int maxFlow) {
		return minCostFlow(source, sink, maxFlow, false);
	}

	public MinCostFlowResult minCostFlowDijkstra(int source, int sink, int maxFlow) {
		return minCostFlow(source, sink, maxFlow, true);
	}

	public boolean hasNegativeCycle() {
		long[] dist = new long[n];
		Arrays.fill(dist, 0L);
		for (int i = 0; i < n - 1; i++) {
			boolean updated = false;
			for (int u = 0; u < n; u++) {
				if (dist[u] == INF) {
					continue;
				}
				for (Arrete e : adj[u]) {
					if (e.residualCapacity() <= 0) {
						continue;
					}
					long nd = dist[u] + e.cost;
					if (nd < dist[e.to]) {
						dist[e.to] = nd;
						updated = true;
					}
				}
			}
			if (!updated) {
				break;
			}
		}

		for (int u = 0; u < n; u++) {
			if (dist[u] == INF) {
				continue;
			}
			for (Arrete e : adj[u]) {
				if (e.residualCapacity() <= 0) {
					continue;
				}
				if (dist[u] + e.cost < dist[e.to]) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean bfs(int source, int sink, Arrete[] parent) {
		boolean[] visited = new boolean[n];
		ArrayDeque<Integer> queue = new ArrayDeque<>();
		visited[source] = true;
		queue.add(source);
		while (!queue.isEmpty()) {
			int u = queue.poll();
			for (Arrete e : adj[u]) {
				if (!visited[e.to] && e.residualCapacity() > 0) {
					visited[e.to] = true;
					parent[e.to] = e;
					if (e.to == sink) {
						return true;
					}
					queue.add(e.to);
				}
			}
		}
		return visited[sink];
	}

	private MinCostFlowResult minCostFlow(int source, int sink, int maxFlow, boolean usePotentials) {
		int flow = 0;
		long cost = 0;
		int[] potential = new int[n];
		if (usePotentials) {
			long[] init = bellmanFordDistances(source);
			if (init == null) {
				throw new IllegalStateException("Negative cycle detected");
			}
			for (int i = 0; i < n; i++) {
				if (init[i] < INF) {
					potential[i] = (int) init[i];
				}
			}
		}

		while (flow < maxFlow) {
			PathResult path = usePotentials
					? shortestPathDijkstra(source, sink, potential)
					: shortestPathBellmanFord(source, sink);
			if (!path.found) {
				break;
			}

			int add = maxFlow - flow;
			for (Arrete e = path.prev[sink]; e != null; e = path.prev[e.from]) {
				add = Math.min(add, e.residualCapacity());
			}

			for (Arrete e = path.prev[sink]; e != null; e = path.prev[e.from]) {
				e.flow += add;
				e.rev.flow -= add;
				cost += (long) add * e.cost;
			}
			flow += add;

			if (usePotentials) {
				for (int i = 0; i < n; i++) {
					if (path.dist[i] < INF) {
						potential[i] += (int) path.dist[i];
					}
				}
			}
		}

		return new MinCostFlowResult(flow, cost);
	}

	private PathResult shortestPathBellmanFord(int source, int sink) {
		long[] dist = new long[n];
		Arrete[] prev = new Arrete[n];
		Arrays.fill(dist, INF);
		dist[source] = 0L;

		for (int i = 0; i < n - 1; i++) {
			boolean updated = false;
			for (int u = 0; u < n; u++) {
				if (dist[u] == INF) {
					continue;
				}
				for (Arrete e : adj[u]) {
					if (e.residualCapacity() <= 0) {
						continue;
					}
					long nd = dist[u] + e.cost;
					if (nd < dist[e.to]) {
						dist[e.to] = nd;
						prev[e.to] = e;
						updated = true;
					}
				}
			}
			if (!updated) {
				break;
			}
		}

		for (int u = 0; u < n; u++) {
			if (dist[u] == INF) {
				continue;
			}
			for (Arrete e : adj[u]) {
				if (e.residualCapacity() <= 0) {
					continue;
				}
				if (dist[u] + e.cost < dist[e.to]) {
					throw new IllegalStateException("Negative cycle detected");
				}
			}
		}

		return new PathResult(dist, prev, dist[sink] < INF);
	}

	private long[] bellmanFordDistances(int source) {
		long[] dist = new long[n];
		Arrays.fill(dist, INF);
		dist[source] = 0L;

		for (int i = 0; i < n - 1; i++) {
			boolean updated = false;
			for (int u = 0; u < n; u++) {
				if (dist[u] == INF) {
					continue;
				}
				for (Arrete e : adj[u]) {
					if (e.residualCapacity() <= 0) {
						continue;
					}
					long nd = dist[u] + e.cost;
					if (nd < dist[e.to]) {
						dist[e.to] = nd;
						updated = true;
					}
				}
			}
			if (!updated) {
				break;
			}
		}

		for (int u = 0; u < n; u++) {
			if (dist[u] == INF) {
				continue;
			}
			for (Arrete e : adj[u]) {
				if (e.residualCapacity() <= 0) {
					continue;
				}
				if (dist[u] + e.cost < dist[e.to]) {
					return null;
				}
			}
		}
		return dist;
	}

	private PathResult shortestPathDijkstra(int source, int sink, int[] potential) {
		long[] dist = new long[n];
		Arrete[] prev = new Arrete[n];
		Arrays.fill(dist, INF);
		dist[source] = 0L;

		PriorityQueue<long[]> pq = new PriorityQueue<>(Comparator.comparingLong(a -> a[0]));
		pq.add(new long[]{0L, source});

		while (!pq.isEmpty()) {
			long[] cur = pq.poll();
			long d = cur[0];
			int u = (int) cur[1];
			if (d != dist[u]) {
				continue;
			}
			for (Arrete e : adj[u]) {
				if (e.residualCapacity() <= 0) {
					continue;
				}
				long nd = d + e.cost + potential[u] - potential[e.to];
				if (nd < dist[e.to]) {
					dist[e.to] = nd;
					prev[e.to] = e;
					pq.add(new long[]{nd, e.to});
				}
			}
		}

		return new PathResult(dist, prev, dist[sink] < INF);
	}
}
