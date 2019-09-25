import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * @author nedimazar
 *
 */
public class InvertedIndexBuilder {

	/**
	 * The Inverted Index to populate
	 */
	private final InvertedIndex invertedIndex;

	/**
	 * The default stemming algorithm.
	 */
	private static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

	/**
	 * @param invertedIndex Inverted Index structure that will be built.
	 */
	public InvertedIndexBuilder(InvertedIndex invertedIndex) {
		this.invertedIndex = invertedIndex;
	}

	/**
	 * This function will add the given file to the inverted ndex structure.
	 *
	 * @param inputFile The file that will be added into the structure
	 * @throws IOException
	 */
	public void addPath(Path inputFile) throws IOException {
		Stemmer stemmer = new SnowballStemmer(DEFAULT);

		BufferedReader reader = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8);
		String line = reader.readLine();
		String location = inputFile.toString();
		int i = 0;
		while (line != null) {
			String[] parsed = TextParser.parse(line);
			for (String word : parsed) {
				String stemmed = stemmer.stem(word).toString();
				this.invertedIndex.add(stemmed, location, ++i);
			}
			line = reader.readLine();
		}

	}

	/**
	 * Checks if path is a text file
	 *
	 * @param path
	 * @return returns true if file is a text file false if not
	 */
	public static boolean isTextFile(Path path) {
		return (path.toString().toLowerCase().endsWith(".txt")
				|| path.toString().toLowerCase().endsWith(".text")
				&& Files.isRegularFile(path));

	}

	/**
	 * This function calls addpath on every subfile starting from a given directory
	 *
	 * @param path Starting point of traversal
	 * @throws IOException could happen
	 */
	public void traversePath(Path path) throws IOException {
		try (Stream<Path> subPaths = Files.walk(path, FileVisitOption.FOLLOW_LINKS)){
			subPaths.filter(w -> isTextFile(w)).forEach(w -> {
				try { addPath(w); }
				catch (IOException e) {
					System.out.println("This file could not be added properly: " + w.toString());
				}
			});
		}
	}
}
