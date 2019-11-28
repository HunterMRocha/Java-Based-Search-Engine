import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

import org.eclipse.jetty.server.Server;


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
	 *
	 */
	public static void main(String[] args) {
		int numThreads;
		/* Store initial start time */
		Instant start = Instant.now();
		ArgumentParser argumentParser = new ArgumentParser(args);
		InvertedIndex invertedIndex;
		InvertedIndexBuilder builder;
		QueryBuilderInterface queryBuilder;
		WebCrawler webCrawler;

		if (argumentParser.hasFlag("-threads") || argumentParser.hasFlag("-url") || argumentParser.hasFlag("-port")) {

			try {
				numThreads = Integer.parseInt(argumentParser.getString("-threads"));
				if (numThreads == 0) {
					numThreads = 5;
				}
			} catch (Exception e) {
				numThreads = 5;
			}

			ThreadSafeInvertedIndex threadSafe = new ThreadSafeInvertedIndex();
			invertedIndex = threadSafe;
			builder = new ThreadSafeIndexBuilder(threadSafe, numThreads);
			queryBuilder = new ThreadSafeQueryBuilder(threadSafe, numThreads);

			if (argumentParser.hasFlag("-limit")) {
				webCrawler = new WebCrawler(threadSafe, numThreads,
						Integer.parseInt(argumentParser.getString("-limit")));
			} else {
				webCrawler = new WebCrawler(threadSafe, numThreads, 50);
			}

			if (argumentParser.hasFlag("-url")) {
				try {
					URL seedURL = new URL(argumentParser.getString("-url"));
					webCrawler.traverse(seedURL);
				} catch (Exception e) {
					System.out.println(
							"Something went wrong while creating a URL from: " + argumentParser.getString("-url"));
				}
			}

			if (argumentParser.hasFlag("-port")) {
				int port;
				try {
					port = Integer.parseInt(argumentParser.getString("-port"));
				} catch (Exception e){
					port = 8080;
				}

				Server server = new Server(port);

			}




		} else {
			invertedIndex = new InvertedIndex();
			builder = new InvertedIndexBuilder(invertedIndex);
			queryBuilder = new QueryBuilder(invertedIndex);
		}

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

		if (argumentParser.hasFlag("-counts")) {
			Path path = argumentParser.getPath("-counts", Path.of("counts.json"));
			try {
				SimpleJsonWriter.asObject(invertedIndex.getUnmodifiableCounts(), path);
			} catch (IOException e) {
				System.out.println("There was an issue while writing counts info to file: " + path.toString());
			}
		}

		if (argumentParser.hasFlag("-query") && argumentParser.getPath("-query") != null) {
			Path queryPath = argumentParser.getPath("-query");
			try {
				queryBuilder.parseQueryFile(queryPath, argumentParser.hasFlag("-exact"));
			} catch (Exception r) {
				System.out.println("There was an issue while doing things with file: " + queryPath.toString());
			}
		}

		if (argumentParser.hasFlag("-results")) {
			Path path = argumentParser.getPath("-results", Path.of("results.json"));

			try {
				queryBuilder.writeQuery(path);
			} catch (IOException e) {
				System.out.println("Something went wrong while writing search results to path: " + path);
			}

		}

		/* Calculate time elapsed and output */
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}
}
