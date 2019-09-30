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
	 * This function will add the given file to the inverted ndex structure.
	 *
	 * @param inputFile The file that will be added into the structure
	 * @throws IOException
	 */
	public void addPath(Path inputFile) throws IOException {
		Stemmer stemmer = new SnowballStemmer(DEFAULT);

		try (BufferedReader reader = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8);){
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
	}

	/**
	 * Checks if path is a text file
	 *
	 * @param path
	 * @return returns true if file is a text file false if not
	 */
	public static boolean isTextFile(Path path) {
		String lower = path.toString().toLowerCase();
		return (lower.endsWith(".txt")
				|| lower.endsWith(".text")
				&& Files.isRegularFile(path));
	}

	/**
	 * This function calls addpath on every subfile starting from a given directory
	 *
	 * @param path Starting point of traversal
	 * @throws IOException could happen
	 */
	public void traversePath(Path path) throws IOException {
		/*
		 * TODO Streams and lambda expressions do not work great when (a) you have
		 * exceptions and (b) you are modifying data.
		 *
		 * I suggest breaking this up so you have a method dedicated to giving you
		 * all of the text files, and use that here and just loop to add each file.
		 * This will also help when you get to multithreading.
		 *
		 * public static List<Path> getTextFiles(Path path) ...
		 *
		 * for (Path current : getTextFiles(path)) {
		 * 		addPath(current);
		 * }
		 *
		 * (It is a little bit slower, since there is a delay between when you know
		 * about a text file and when you add that file to the index. But this will
		 * help us break up the problem for multithreading later on.)
		 */
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
