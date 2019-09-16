import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Stream;

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
	 * @throws Exception
	 */
	public static void main(String[] args) throws IOException{
		// store initial start time
		Instant start = Instant.now();

		// TODO Fill in and modify this method as necessary.

		InvertedIndex invertIndex = new InvertedIndex();

		ArgumentParser argumentParser = new ArgumentParser(args);

		if (argumentParser.hasFlag("-path") && argumentParser.getPath("-path") != null) {			Path path = argumentParser.getPath("-path");

		try (Stream<Path> subPaths = Files.walk(path)){
			Iterator<Path> iterator = subPaths.iterator();
			while(iterator.hasNext()) {
				var nextPath = iterator.next();
				if (nextPath.toString().toLowerCase().endsWith(".txt") || nextPath.toString().toLowerCase().endsWith(".text") ){
					invertIndex.addPath(nextPath);
				}

			}
		}


		}

		if (argumentParser.hasFlag("-index") && argumentParser.getString("-index") != null) {
			invertIndex.writeIndex(argumentParser.getString("-index"));
		}

		System.out.println(Arrays.toString(args));

		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}
}
