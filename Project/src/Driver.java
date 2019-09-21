import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2019
 */
public class Driver {
	/**
	 * Initializes the classes necessary based on the provided command-line
	 * arguments. This includes (but is not limited to) how to build or search an
	 * inverted index.
	 *
	 * @param args flag/value pairs used to start this program
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException{
		/* Store initial start time */
		Instant start = Instant.now();

		InvertedIndex invertIndex = new InvertedIndex();

		ArgumentParser argumentParser = new ArgumentParser(args);

		InvertedIndexBuilder builder = new InvertedIndexBuilder(invertIndex);

		if (argumentParser.hasFlag("-path") && argumentParser.getPath("-path") != null) {
			Path path = argumentParser.getPath("-path");

			builder.traversePath(path);
		}

		if (argumentParser.hasFlag("-index")) {
			invertIndex.writeIndex((argumentParser.getPath("-index") != null) ?
					argumentParser.getPath("-index")
					: Path.of("index.json"));
		}

		if(argumentParser.hasFlag("-counts")) {
			SimpleJsonWriter.asObject(invertIndex.getUnmodifiableCounts(), Path.of("actual/counts.json"));
		}

		/* Calculate time elapsed and output */
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}
}
