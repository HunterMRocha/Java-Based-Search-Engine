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
	/*
	 * Make sure Driver.main does not throw any exceptions... should catch and output
	 * a user-friendly informative message instead.
	 */

	/**
	 * Initializes the classes necessary based on the provided command-line
	 * arguments. This includes (but is not limited to) how to build or search an
	 * inverted index.
	 *
	 * @param args flag/value pairs used to start this program
	 *
	 */
	public static void main(String[] args) {
		/* Store initial start time */
		Instant start = Instant.now();

		InvertedIndex invertedIndex = new InvertedIndex();

		ArgumentParser argumentParser = new ArgumentParser(args);

		InvertedIndexBuilder builder = new InvertedIndexBuilder(invertedIndex);

		if (argumentParser.hasFlag("-path") && argumentParser.getPath("-path") != null) {
			Path path = argumentParser.getPath("-path");
			try {
				builder.traversePath(path);
			} catch (IOException e) {
				System.out.println("Path can not be traversed: " + path.toString());
			}
		}

		if (argumentParser.hasFlag("-index")) {
			Path path = argumentParser.getPath("-index", Path.of("index.json"));
			try {
				invertedIndex.writeIndex(path);
			} catch (IOException e) {
				System.out.println("There was an issue while writing inverted index to file: " + path.toString());
			}
		}

		if(argumentParser.hasFlag("-counts")) {
			Path path = argumentParser.getPath("-counts", Path.of("counts.json"));
			try {
				SimpleJsonWriter.asObject(invertedIndex.getUnmodifiableCounts(), path);
			} catch (IOException e) {
				System.out.println("There was an issue while writing counts info to file: " + path.toString());
			}
		}

		if(argumentParser.hasFlag("-query") && argumentParser.getPath("-query") != null) {
			Path queryPath = argumentParser.getPath("-query");
			try {
				QueryBuilder queryBuilder = new QueryBuilder(invertedIndex, queryPath);
				queryBuilder.makeQueries();

				if (argumentParser.hasFlag("-results")) {
					Path path = argumentParser.getPath("-results");

					if (path == null) {
						path = Path.of("results.json");
					}

					SimpleJsonWriter.asQuery(queryBuilder.getQuerySet(), path);
				}

			} catch (IOException e) {
				System.out.println("There was an issue while reading the query file: " + queryPath.toString());
			} catch (Exception r ) {
				r.printStackTrace();
			}



		}

		/* Calculate time elapsed and output */
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}
}
