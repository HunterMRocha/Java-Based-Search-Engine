import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * An inverted index data structure that maps words to a map mapping filenames
 * to indexes where the word is located in the file.
 *
 * @author nedimazar
 */
public class InvertedIndex {

	/**
	 * Inner Class that holds the result of a search. Every Query has a collection
	 * of results associated with it.
	 *
	 * @author nedimazar
	 *
	 */
	public class Result implements Comparable<Result> {
		/**
		 * This will hold the location of the search result.
		 */
		private final String location;
		/**
		 * This will hold the count of matches for a specific result object.
		 */
		private int count;
		/**
		 * This will hold the scoire of the search result.
		 */
		private double score;

		/**
		 * Constructor for Result object.
		 *
		 * @param location To construct a result given a location.
		 *
		 */
		public Result(String location) {
			this.location = location;
			this.count = 0;
			this.score = 0;
		}

		/**
		 * Debug constructor.
		 *
		 * @param location The location of a search result.
		 * @param count    The number of matches in that location.
		 * @param score    The score calculated by matches/wordCount
		 */
		public Result(String location, int count, double score) {
			this.location = location;
			this.count = count;
			this.score = score;
		}

		/**
		 * Updates the count and score associated to a word.
		 *
		 * @param word The word to be updated.
		 */
		private void updateCount(String word) {
			this.count += invertedIndex.get(word).get(this.location).size();
			this.score = (double) this.count / counts.get(this.location);
		}

		/**
		 * Getter for the count data member.
		 *
		 * @return The count data member
		 */
		public int getCount() {
			return this.count;
		}

		/**
		 * Getter for the score data member.
		 *
		 * @return the score
		 */
		public double getScore() {
			return this.score;
		}

		/**
		 * Checks if another Results location is the same as this ones.
		 *
		 * @param other The Result object to check against.
		 * @return True if same;
		 */
		public boolean sameLocation(Result other) {
			return this.location.compareTo(other.location) == 0;
		}

		/**
		 * @return A formatted string ready to write.
		 */
		public String getWhereString() {
			return String.format("\"where\": \"%s\",", this.location);
		}

		/**
		 * @return A formatted string ready to write.
		 */
		public String getCountString() {
			return String.format("\"count\": %s,", this.count);
		}

		/**
		 * @return A formatted string ready to write.
		 */
		public String getScoreString() {
			return String.format("\"score\": %s", String.format("%.8f", this.score));
		}

		@Override
		public int compareTo(Result o) {
			return Comparator.comparing(Result::getScore, Comparator.reverseOrder())
					.thenComparing(Result::getCount, Comparator.reverseOrder()).thenComparing(Result::getWhereString)
					.compare(this, o);
		}

	}

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
	 * @return Unmodifiable set of words.
	 */
	public Set<String> getWords() {
		return Collections.unmodifiableSet(this.invertedIndex.keySet());
	}

	/**
	 * Updates the invertedIndex with the necessary info like files it appears in
	 * and its position
	 *
	 * @param word     The word in question.
	 * @param filename The file that word appears in.
	 * @param position Position of the word in the file.
	 * @return returns true if the data structure was modified as a result of add()
	 */
	public boolean add(String word, String filename, int position) {
		this.invertedIndex.putIfAbsent(word, new TreeMap<>());
		this.invertedIndex.get(word).putIfAbsent(filename, new TreeSet<>());

		boolean added = this.invertedIndex.get(word).get(filename).add(position);

		this.counts.putIfAbsent(filename, position);

		if (position > counts.get(filename)) {
			this.counts.put(filename, position);
		}

		return added;
	}

	/* TODO */
	/**
	 * Merges two inverted indexes.
	 *
	 * @param other The other inverted index
	 */
	public void addAll1(InvertedIndex other) {
		// combine results of invertedIndex
		for (String key : other.invertedIndex.keySet()) {
			if (this.invertedIndex.containsKey(key) == false) {
				this.invertedIndex.put(key, other.invertedIndex.get(key));
			}
			else {
				for (var x : other.invertedIndex.get(key).keySet()) {
					if (this.invertedIndex.get(key).get(x) == null) {
						this.invertedIndex.get(key).put(x, other.invertedIndex.get(key).get(x));
					} else {
						this.invertedIndex.get(key).get(x).addAll(other.invertedIndex.get(key).get(x));
					}
				}
			}
		}

		// combine results of counts
	}
	/**/

	/**
	 * Add all method for going through the inverted index
	 *
	 * @param other is the other inverted index
	 */
	public void addAll(InvertedIndex other) {
		for (String word : other.invertedIndex.keySet()) {
			if (this.invertedIndex.containsKey(word) == false) {
				this.invertedIndex.put(word, other.invertedIndex.get(word));
			} else {
				for (String location : other.invertedIndex.get(word).keySet()) {
					if (this.invertedIndex.get(word).containsKey(location) == false) {
						this.invertedIndex.get(word).put(location, other.invertedIndex.get(word).get(location));
					} else {
						this.invertedIndex.get(word).get(location).addAll(other.invertedIndex.get(word).get(location));
					}
				}
			}
		}

		for (String location : other.counts.keySet()) {
			if (this.counts.containsKey(location) == false) {
				this.counts.put(location, other.counts.get(location));
			} else {
				this.counts.put(location, Math.max(this.counts.get(location), other.counts.get(location)));
			}
		}
	}


	/**
	 * Writes the invertedIndex in a pretty Json format to the specified output file
	 *
	 * @param outputFile Where to write.
	 * @throws IOException Very possible.
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
	 * @param word Word to look for
	 * @return True if the word is stored false if not
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
		if (hasWord(word)) {
			return this.invertedIndex.get(word).containsKey(location);
		}
		return false;
	}

	/**
	 * Returns TreeSet of Results given a query.
	 *
	 * @param queries The collection of queries.
	 * @return A set of Results associated to a query.
	 */
	public ArrayList<Result> exactSearch(Collection<String> queries) {
		ArrayList<Result> results = new ArrayList<>();
		HashMap<String, Result> lookup = new HashMap<>();

		for (String query : queries) {
			if (invertedIndex.containsKey(query)) {
				searchHelper(results, query, lookup);
			}
		}

		Collections.sort(results);
		return results;
	}

	/**
	 * Performs Partial search on a collection of queries.
	 *
	 * @param queries The collection of queries.
	 * @return An ArrayList of Results.
	 */
	public ArrayList<Result> partialSearch(Collection<String> queries) {
		ArrayList<Result> results = new ArrayList<>();
		HashMap<String, Result> lookup = new HashMap<>();

		for (String query : queries) {
			for (String word : this.invertedIndex.tailMap(query).keySet()) {
				if (word.startsWith(query)) {
					searchHelper(results, word, lookup);
				} else {
					break;
				}
			}
		}

		Collections.sort(results);
		return results;
	}

	/**
	 * A helper method called by the two search methods.
	 *
	 * @param results List of Result objects.
	 * @param word    The word being searched.
	 * @param lookup  A lookup map for updating counts and scores.
	 */
	private void searchHelper(ArrayList<Result> results, String word, Map<String, Result> lookup) {
		for (String location : this.invertedIndex.get(word).keySet()) {
			if (lookup.containsKey(location)) {
				lookup.get(location).updateCount(word);
			} else {
				Result result = new Result(location);
				result.updateCount(word);
				lookup.put(location, result);
				results.add(result);
			}
		}
	}

	/**
	 * Calls the necessary search algorithm.
	 *
	 * @param queries The collection of queries.
	 * @param exact   Is it an exact search?
	 * @return An ArrayList of Results.
	 */
	public ArrayList<Result> search(Collection<String> queries, boolean exact) {
		if (exact) {
			return exactSearch(queries);
		} else {
			return partialSearch(queries);
		}
	}
}
