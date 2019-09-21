import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Stream;

// TODO Remove old TODO comments when you do not need them.
// TODO Eclipse --> Window --> Show View --> Tasks

// TODO Clean up formatting, especially blank lines

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
	 * TODO Exceptions
	 * The only place you should never throw an exception is Driver.main()
	 * 
	 * Always want the user to see "user-friendly" output and "informative" output.
	 * - A stack trace is not user friendly.
	 * - A message like "Error occurred." is not informative.
	 * 
	 * Goal is to give the user enough information they can rerun the code without
	 * the same problem.
	 */

	/*
	 * TODO
	 * Driver is the only class we do not share with other developers. It is our
	 * programmer-specific way to invoke other code.
	 * 
	 * So anything generally useful should not be included in Driver... like
	 * directory traversal.
	 * 
	 * Move InvertedIndex.addPath(...) and your directory traversal into a "builder" class...
	 * InvertedIndexBuilder.
	 * 
	 * public class InvertedIndexBuilder {
	 * 
	 * 	private final InvertedIndex index;
	 * 
	 * 	public InvertedIndexBuilder(InvertedIndex index) {
	 * 		this.index = index;
	 * }
	 *
	 * 
	 * 	public void addPath(Path path) {
	 * 		add to this.index
	 * }
	 * 
	 * 	public void traverse(Path path) {
	 * 		directory traversal code that calls addPath
	 * }
	 * }
	 * 
	 * 
	 */
	
	/**
	 * Initializes the classes necessary based on the provided command-line
	 * arguments. This includes (but is not limited to) how to build or search an
	 * inverted index.
	 *
	 * @param args flag/value pairs used to start this program
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException{
		// store initial start time
		Instant start = Instant.now();

		// TODO Fill in and modify this method as necessary.

		InvertedIndex invertIndex = new InvertedIndex();

		ArgumentParser argumentParser = new ArgumentParser(args);
		
		// TODO InvertedIndexBuilder builder = new InvertedIndexBuilder(invertIndex);

		if (argumentParser.hasFlag("-path") && argumentParser.getPath("-path") != null) {
			Path path = argumentParser.getPath("-path");
			
			// TODO builder.traverse(path);

			try (Stream<Path> subPaths = Files.walk(path, FileVisitOption.FOLLOW_LINKS)){
				Iterator<Path> iterator = subPaths.iterator();
				while(iterator.hasNext()) {
					var nextPath = iterator.next();
					if (nextPath.toString().toLowerCase().endsWith(".txt") || nextPath.toString().toLowerCase().endsWith(".text") ){
						invertIndex.addPath(nextPath);


					}

				}
			} catch (Exception e) {
				System.out.println("Something went wrong?!");
				e.printStackTrace();
				
				// TODO System.out.println("Unable to properly build the inverted index from path: " + path);
			}


		}

		if (argumentParser.hasFlag("-index") && argumentParser.getString("-index") != null) {
			invertIndex.writeIndex(argumentParser.getString("-index"));
			//SimpleJsonWriter.asInvertedIndex(invertIndex.getStructure(), Path.of("index.json"));
		}
		if(argumentParser.hasFlag("-index")) {
			// TODO invertIndex.writeIndex(outputFile, argumentParser.hasFlag("-index"));
			SimpleJsonWriter.asInvertedIndex(invertIndex.getStructure(), Path.of("index.json"));
		}
		if(argumentParser.hasFlag("-counts")) {
			SimpleJsonWriter.asObject(invertIndex.getCounts(), Path.of("actual/counts.json"));
		}




		System.out.println(Arrays.toString(args)); // TODO Remove

		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}
}
