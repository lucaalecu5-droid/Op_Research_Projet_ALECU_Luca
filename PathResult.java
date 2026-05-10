class PathResult {
	public final long[] dist;
	public final Arrete[] prev;
	public final boolean found;

	public PathResult(long[] dist, Arrete[] prev, boolean found) {
		this.dist = dist;
		this.prev = prev;
		this.found = found;
	}
}
