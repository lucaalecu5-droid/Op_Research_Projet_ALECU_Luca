import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GraphVizWriter {
	private GraphVizWriter() {
	}

	public static void writeGraph(Graph graph, String outputPath, Integer source, Integer sink) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("digraph G {\n");
		sb.append("  graph [nodesep=\"0.3\", ranksep=\"0.3\", fontsize=12];\n");
		sb.append("  node [shape=circle, fixedsize=true, width=.3, height=.3, fontsize=12];\n");
		sb.append("  edge [arrowsize=0.6];\n\n");

		for (Arrete e : graph.getEdges()) {
			sb.append("  ")
				.append(e.from)
				.append(" -> ")
				.append(e.to)
				.append(" [label = <<font color=\"green\">")
				.append(e.flow)
				.append("/")
				.append(e.capacity)
				.append("</font>,<font color=\"red\">")
				.append(e.cost)
				.append("</font>>];\n");
		}
		sb.append("\n");

		for (int i = 0; i < graph.size(); i++) {
			String label = String.valueOf(i);
			String color = null;
			if (source != null && i == source) {
				label = "s";
				color = "green";
			} else if (sink != null && i == sink) {
				label = "t";
				color = "blue";
			}
			sb.append("  ").append(i).append(" [label=\"").append(label).append("\"");
			if (color != null) {
				sb.append(",color=").append(color);
			}
			sb.append("];\n");
		}
		sb.append("}\n");

		Path path = Paths.get(outputPath);
		try (BufferedWriter writer = Files.newBufferedWriter(path)) {
			writer.write(sb.toString());
		}
	}
}
