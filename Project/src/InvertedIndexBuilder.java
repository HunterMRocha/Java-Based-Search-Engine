import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * This builder class will initialize an inverted index with a provided path.
 *
 * @author nedimazar
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
	 * Drives static method.
	 *
	 * @param inputFile
	 * @throws IOException
	 */
	public void addPath(Path inputFile) throws IOException {
		addPath(inputFile, this.invertedIndex);
	}

	/**
	 * Populates the inverted index.
	 *
	 * @param inputFile
	 * @param index
	 * @throws IOException
	 */
	public static void addPath(Path inputFile, InvertedIndex index) throws IOException {
		Stemmer stemmer = new SnowballStemmer(DEFAULT);

		try (BufferedReader reader = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8);) {
			String line = reader.readLine();
			String location = inputFile.toString();
			int i = 0;

			while (line != null) {
				String[] parsed = TextParser.parse(line);
				for (String word : parsed) {
					String stemmed = stemmer.stem(word).toString();
					index.add(stemmed, location, ++i);
				}
				line = reader.readLine();
			}
		}
	}

	/**
	 * Checks if path is a text file
	 *
	 * @param path
	 * @return returns true if file is a text file false if not
	 */
	public static boolean isTextFile(Path path) {
		String lower = path.toString().toLowerCase();
		return ((lower.endsWith(".txt") || lower.endsWith(".text")) && Files.isRegularFile(path));
	}

	/**
	 * Returns a list of all subfiles from a starting file.
	 *
	 * @param path
	 * @return returns a list of all subfiles from a starting file.
	 * @throws IOException
	 */
	public static List<Path> getTextFiles(Path path) throws IOException {
		List<Path> list = Files.walk(path, FileVisitOption.FOLLOW_LINKS).collect(Collectors.toList());
		return list;
	}

	/**
	 * This function calls addpath on every subfile starting from a given directory
	 *
	 * @param path Starting point of traversal
	 * @throws IOException could happen
	 */
	public void traversePath(Path path) throws IOException {
		for (Path currentPath : getTextFiles(path)) {
			if (isTextFile(currentPath)) {
				addPath(currentPath);
			}
		}
	}

}
