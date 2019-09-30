import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * TODO Describe the class here.
 * 
 * @author nedimazar
 */
public class InvertedIndex {

	/**
	 * The data structure that will store the inverted index info.
	 */
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> invertedIndex;

	/**
	 * This map will keep track of the wordcounts of files.
	 */
	private final TreeMap<String, Integer> counts;

	/**
	 * Constructor for the InvertedIndex class, initializes the structure.
	 */
	public InvertedIndex() {
		this.invertedIndex = new TreeMap<>();
		this.counts = new TreeMap<>();
	}

	/**
	 * Updates the invertedIndex with the necessary info like files it appears in
	 * and its position
	 *
	 * @param word
	 * @param filename
	 * @param position
	 * @return returns true if the data structure was modified as a result of add()
	 */
	public boolean add(String word, String filename, int position) {
		this.invertedIndex.putIfAbsent(word, new TreeMap<>());
		this.invertedIndex.get(word).putIfAbsent(filename, new TreeSet<>());

		boolean added = this.invertedIndex.get(word).get(filename).add(position);

		// TODO Only increase counts if added is true! (Do not increase the count if this was a duplicate entry.)
		this.counts.putIfAbsent(filename, 0);
		this.counts.put(filename, counts.get(filename) + 1);

		return added;
	}

	/**
	 * Writes the invertedIndex in a pretty Json format to the specified output file
	 *
	 * @param outputFile
	 * @throws IOException
	 */
	public void writeIndex(Path outputFile) throws IOException {
		SimpleJsonWriter.asInvertedIndex(this.invertedIndex, outputFile);
	}

	/**
	 * @return returns counts as an unmodifiable
	 */
	public Map<String, Integer> getUnmodifiableCounts() {
		return Collections.unmodifiableMap(this.counts);
	}

	/**
	 * Checks if there is an entry for the word passed
	 *
	 * @param word word to look for
	 * @return true if the word is stored false if not
	 */
	public boolean hasWord(String word) {
		return this.invertedIndex.containsKey(word);
	}

	/**
	 * This function checks if a given word entry contains a given location
	 *
	 * @param word     word entry to look in
	 * @param location location we are looking for
	 * @return true if the word entry exists snd contains an entry for the location
	 */
	public boolean hasLocation(String word, String location) {
		if (hasWord(word)) { // TODO I suggest you just access the index directly here... makes it easier to handle multithreading later on
			// TODO return this.invertedIndex.get(word).containsKey(location);
			return this.invertedIndex.get(word).keySet().contains(location);
		}

		return false;
	}

	/*
	 * TODO Unfortunately, the getWord() method below breaks encapsulation because
	 * it returns a NESTED data structure. You'll need to remove and replace this
	 * functionality wtih something else. Maybe...
	 * 
	 * public Set<String> getLocations(String word) that just returns the keyset of locations
	 * 
	 * ... the user can then use getLocations to fetch the individual position sets later if needed
	 */
	/**
	 * Returns the entries for a word as an unmodifiable map
	 *
	 * @param word the word we want to get the info about
	 * @return returns a map of filenames to a treeset of locations (unmodifiable)
	 */
	public Map<String, TreeSet<Integer>> getWord(String word) {
		if (this.invertedIndex.get(word) != null) {
			return Collections.unmodifiableMap(this.invertedIndex.get(word));
		} else {
			return Collections.emptyMap();
		}
	}

	/**
	 * Returns a treeset of locations for the given word-file pair
	 *
	 * @param word word to look for
	 * @param file file to get positions from
	 * @return returns the found structure in unmodifiable form
	 */
	public Set<Object> getLocationsInFile(String word, String file){
		if (getWord(word) != Collections.EMPTY_MAP) { // TODO Again access index directly... test if the word and file exist.... if invertedIndex.containsKey(word) && invertedIndex.get(word).containsKey(file)
			return Collections.unmodifiableSet(getWord(word).get(file));
		} else {
			return Collections.emptySet();
		}
	}
}
