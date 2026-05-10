import java.util.List;

public class MaxFlowResult {
	public final int maxFlow;
	public final int minCutCapacity;
	public final List<Arrete> minCutEdges;

	public MaxFlowResult(int maxFlow, int minCutCapacity, List<Arrete> minCutEdges) {
		this.maxFlow = maxFlow;
		this.minCutCapacity = minCutCapacity;
		this.minCutEdges = minCutEdges;
	}
}
