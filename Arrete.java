public class Arrete {
	public final int from;
	public final int to;
	public final int id;
	public final boolean original;
	public int capacity;
	public int cost;
	public int flow;
	public Arrete rev;

	public Arrete(int from, int to, int capacity, int cost, int id, boolean original) {
		this.from = from;
		this.to = to;
		this.capacity = capacity;
		this.cost = cost;
		this.id = id;
		this.original = original;
		this.flow = 0;
	}

	public int residualCapacity() {
		return capacity - flow;
	}

	@Override
	public String toString() {
		return from + " -> " + to + " " + flow + "/" + capacity + " cost=" + cost;
	}
}
