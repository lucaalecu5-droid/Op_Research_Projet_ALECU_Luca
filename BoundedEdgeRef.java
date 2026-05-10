class BoundedEdgeRef {
	public final Arrete edge;
	public final int lower;
	public final int cost;
	public final int from;
	public final int to;

	public BoundedEdgeRef(Arrete edge, int lower, int cost, int from, int to) {
		this.edge = edge;
		this.lower = lower;
		this.cost = cost;
		this.from = from;
		this.to = to;
	}
}
