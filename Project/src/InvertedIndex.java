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
	 * The stemmer that will be used to stem the words.
	 */
	private static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH; // TODO Move to the InvertedIndexBuilder class

	// TODO private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> invertedIndex;
	/**
	 * The data structure that will store our inverted index info.
	 */
	public TreeMap<String, TreeMap<String, ArrayList<Integer>>> invertedIndex;

	/**
	 * This map will keep track of the wordcounts of files.
	 */
	private TreeMap<String, Integer> counts; // TODO private final

	/**
	 * Constructor for the InvertedIndex class, initializes the structure.
	 */
	public InvertedIndex() {
		this.invertedIndex = new TreeMap<>();
		this.counts = new TreeMap<>();
	}

	// TODO Remove this method that breaks encapsulation
	/**
	 * @return returns the inverted index
	 */
	public TreeMap<String, TreeMap<String, ArrayList<Integer>>> getStructure() {
		return (this.invertedIndex);
	}

	/**
	 * Will read the given file and pass the stemmed words to the add() method
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
	 * Updates the invertedIndex with the necessary info like files it appears in and its position
	 * @param word
	 * @param filename
	 * @param position
	 * @return returns true if the data structure was modified as a result of add()
	 */
	public boolean add(String word, String filename, int position) {

		this.invertedIndex.putIfAbsent(word, new TreeMap<>());
		this.invertedIndex.get(word).putIfAbsent(filename, new ArrayList<>());
		boolean added = this.invertedIndex.get(word).get(filename).add(position);

		this.getCounts().putIfAbsent(filename, 0);
		this.getCounts().put(filename, getCounts().get(filename) + 1);

		return  added;
	}

	/**
	 * Writes the invertedIndex in a pretty Json format to the specified output file
	 * @param outputFile
	 * @throws IOException
	 */
	public void writeIndex(String outputFile) throws IOException { // TODO writeIndex(Path outputFile)
		SimpleJsonWriter.asInvertedIndex(this.invertedIndex, Path.of(outputFile));
	}

	/**
	 * Please don't modify her.
	 * @return returns a treemap storing the count info
	 */
	public TreeMap<String, Integer> getCounts() { // TODO public Map<String, Integer> getCounts()
		return counts;

		// TODO return Collections.unmodifiableMap(this.counts);
	}
}
