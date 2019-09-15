import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * @author nedimazar
 *
 */
public class InvertedIndex {
	/**
	 *
	 */
	private static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;
	/**
	 * The data structure that will store our inverted index info.
	 */
	private TreeMap<String, TreeMap<String, ArrayList<Integer>>> invertedIndex;

	/**
	 * Checks if invertedIndex has an entry for word
	 *
	 * @param word The word we are checking
	 * @return Returns wether ot not the invertedIndex contains a word.
	 */
	private boolean containsWord(String word) {

		if (this.invertedIndex.keySet() != null) {
			return this.invertedIndex.keySet().contains(word);
		}

		return false;
	}

	/**
	 * @param word
	 * @param filename
	 * @return returns true if a word entry contains a certain file
	 */
	private boolean wordEntryContainsFilename(String word, String filename) {
		if (this.invertedIndex.get(word) != null) {
			return (this.invertedIndex.get(word).get(filename) != null);
		}
		return false;
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
					add(stemmed, inputFile.toString(), ++i);
				}
				line = reader.readLine();
			}

		} catch (IOException e) {
			// System.out.println("Something went wrong while reading the file!");
		}

	}

	/**
	 * Constructor for the InvertedIndex class, initializes the structure.
	 */
	public InvertedIndex() {
		this.invertedIndex = new TreeMap<>();
	}

	/**
	 * @param word
	 * @param filename
	 * @param position
	 * @return returns true if the data structure was modified as a result of add()
	 */
	public boolean add(String word, String filename, int position) {

		if (!this.containsWord(word)) {

			TreeMap<String, ArrayList<Integer>> map = new TreeMap<>();
			ArrayList<Integer> positions = new ArrayList<>();
			positions.add(position);
			map.put(filename, positions);

			this.invertedIndex.put(word, map);
			return true;
		} else {
			if (wordEntryContainsFilename(word, filename)) {
				if (!this.invertedIndex.get(word).get(filename).contains(position)) {
					this.invertedIndex.get(word).get(filename).add(position);
					return true;
				} else {
					return false;
				}
			} else {
				ArrayList<Integer> positions = new ArrayList<>();
				positions.add(position);
				this.invertedIndex.get(word).put(filename, positions);
				return true;
			}

		}
	}

	/**
	 * @param outputFile
	 * @throws IOException
	 */
	public void writeIndex(String outputFile) throws IOException {
		SimpleJsonWriter.asInvertedIndex(this.invertedIndex, Path.of(outputFile));
	}
}
