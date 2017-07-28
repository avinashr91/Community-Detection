import java.io.IOException;
import java.util.Scanner;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import community.algorithm.CommunityDetector;
import community.graphml.GraphML;

public class Program {
	public static void main(String[] args) throws ParserConfigurationException,
			SAXException, IOException {
		System.out
				.println("------------------------------------------------------------");
		System.out
				.println("-------------- Community Detection -------------------------");
		System.out
				.println("------------------------------------------------------------");

		Scanner scanner = new Scanner(System.in);
		System.out.print("Please input .graphml file: ");
		String filename = scanner.nextLine();

		int optAlg = 0;
		do {
			System.out
					.print("Please choose an algorithm (0. Non-normalized; 1. Normalized): ");
			optAlg = scanner.nextInt();
		} while (optAlg != 0 && optAlg != 1);
		scanner.close();

		System.out
				.println("------------------------------------------------------------");

		long startTime = System.currentTimeMillis();

		GraphML graph = new GraphML(filename);
		CommunityDetector detection = new CommunityDetector(graph, optAlg);
		detection.mergeToOne();

		long endTime = System.currentTimeMillis();

		System.out
				.println("------------------------------------------------------------");
		double qVal = detection.optParseCommunity();
		System.out.println("Optimal Q: " + String.format("%.3f", qVal));
		System.out
				.println("------------------------------------------------------------");
		detection.printCommunities();
		// System.out.println("------------------------------------------------------------");
		// System.out.println("Merge Steps:");
		// detection.printSteps();

		System.out
				.println("------------------------------------------------------------");
		System.out.println("Time: " + (endTime - startTime) + " ms");
	}
}
