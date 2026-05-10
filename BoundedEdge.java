class BoundedEdge {
	public final int from;
	public final int to;
	public final int lower;
	public final int capacity;
	public final int cost;

	public BoundedEdge(int from, int to, int lower, int capacity, int cost) {
		this.from = from;
		this.to = to;
		this.lower = lower;
		this.capacity = capacity;
		this.cost = cost;
	}
}
