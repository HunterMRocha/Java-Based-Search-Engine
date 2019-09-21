import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
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
	 * @param inputFile
	 * @throws IOException
	 */
	public void addPath(Path inputFile) throws IOException {
		Stemmer stemmer = new SnowballStemmer(DEFAULT);

		try (BufferedReader reader = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8);) {
			String line = reader.readLine();
			int i = 0;
			while (line != null) {
				String[] parsed = TextParser.parse(line);
				for (String word : parsed) {
					String stemmed = (String) stemmer.stem(word.toString());
					this.invertedIndex.add(stemmed, inputFile.toString(), ++i);
				}
				line = reader.readLine();
			}

		} catch (IOException e) {
			System.out.println("Something went wrong while reading following file: " + inputFile.toString());
		}
	}


	/**
	 * @param path Path to be added to the Inverted Index
	 */
	public void traversePath(Path path) {
		try (Stream<Path> subPaths = Files.walk(path, FileVisitOption.FOLLOW_LINKS)){
			Iterator<Path> iterator = subPaths.iterator();
			while(iterator.hasNext()) {
				var nextPath = iterator.next();
				if (nextPath.toString().toLowerCase().endsWith(".txt") || nextPath.toString().toLowerCase().endsWith(".text") ){
					addPath(nextPath);
				}
			}
		} catch (Exception e) {
			System.out.println("Unable to properly build the inverted index from path: " + path);
		}
	}
}
