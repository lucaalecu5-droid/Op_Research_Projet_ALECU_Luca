import java.util.List;

class LowerBoundResult {
	public final Graph graph;
	public final int superSource;
	public final int superSink;
	public final int totalDemand;
	public final List<BoundedEdgeRef> refs;
	public final long baseCost;

	public LowerBoundResult(Graph graph, int superSource, int superSink, int totalDemand,
							List<BoundedEdgeRef> refs, long baseCost) {
		this.graph = graph;
		this.superSource = superSource;
		this.superSink = superSink;
		this.totalDemand = totalDemand;
		this.refs = refs;
		this.baseCost = baseCost;
	}
}
