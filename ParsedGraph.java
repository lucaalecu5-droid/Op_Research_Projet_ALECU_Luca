class ParsedGraph {
	public final Graph graph;
	public final int source;
	public final int sink;

	public ParsedGraph(Graph graph, int source, int sink) {
		this.graph = graph;
		this.source = source;
		this.sink = sink;
	}
}
