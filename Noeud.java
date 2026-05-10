public class Noeud {
	public final int id;
	public final String label;

	public Noeud(int id) {
		this(id, String.valueOf(id));
	}

	public Noeud(int id, String label) {
		this.id = id;
		this.label = label;
	}

	@Override
	public String toString() {
		return label;
	}
}
