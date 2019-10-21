import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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
		 * This will hold the count of matches.
		 */
		private int count;
		/**
		 * This will hold the scoire of the search result.
		 */
		private double score;

		/**
		 * Constructor for Result object.
		 *
		 * @param location
		 *
		 */
		public Result(String location) {
			this.location = location;
			this.count = 0;
			this.score = 0;
		}
		
		// TODO Give better descriptions to your javadoc parameters for this class.

		/**
		 * Debug constructor.
		 *
		 * @param location set 
		 * @param count    set
		 * @param score    set
		 */
		public Result(String location, int count, double score) {
			this.location = location;
			this.count = count;
			this.score = score;
		}

		/**
		 * Sets the count data member.
		 *
		 * @param count
		 */
		public void setCount(int count) {
			this.count = count;
			this.score = (double) this.count / counts.get(this.location);
		}

		/**
		 * Adds the input count to the current count of a Result instance.
		 *
		 * @param count
		 */
		public void addCount(int count) {
			this.count += count;
			this.score = (double) this.count / counts.get(this.location);
		}

		/**
		 * Updates the count and score associated to a word.
		 *
		 * @param word The word to be updated.
		 */
		public void updateCount(String word) {
			this.count += invertedIndex.get(word).get(this.location).size();
			this.score = (double) this.count / counts.get(this.location);
		}

		/**
		 * Getter for the count data member.
		 *
		 * @return the count data member
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
		 * @param other
		 * @return true if same;
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
	 * @param word
	 * @param filename
	 * @param position
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
		if (hasWord(word)) {
			return this.invertedIndex.get(word).containsKey(location);
		}
		return false;
	}

	/**
	 * Given a word returns a treeset of results about that word.
	 *
	 * @param word
	 * @return treeset of words.
	 */
	public ArrayList<Result> makeResult(String word) { // TODO Can delete this method now
		ArrayList<Result> results = new ArrayList<>();

		if (this.hasWord(word)) {
			var files = this.invertedIndex.get(word).keySet();
			for (String file : files) {
				Result result = new Result(file);
				result.addCount(this.invertedIndex.get(word).get(file).size());
				results.add(result);
			}
		}
		return results;
	}

	// TODO Fix formatting in search methods to be consistent.

	/**
	 * Returns TreeSet of Results given a query.
	 *
	 * @param queries
	 *
	 * @return A set of Results associated to a query.
	 */
	public ArrayList<Result> exactSearch(Collection<String> queries) {
		ArrayList<Result> results = new ArrayList<>();
		Map<String, Result> lookup = new TreeMap<>(); // TODO Why a TreeMap???
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
	 * @param queries
	 * @return An ArrayList of Results.
	 */
	public ArrayList<Result> partialSearch(Collection<String> queries) {
		ArrayList<Result> results = new ArrayList<>();
		Map<String, Result> lookup = new TreeMap<>(); // TODO Why a TreeMap???

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

	// TODO JAVADOC!
	
	/**
	 * @param results
	 * @param word
	 * @param lookup
	 */
	private void searchHelper(ArrayList<Result> results, String word, Map<String, Result> lookup) {
		for (String location : this.invertedIndex.get(word).keySet()) {
			if (lookup.containsKey(location)) {
				lookup.get(location).updateCount(word);
			} else {
				Result result = new Result(location);
				result.addCount(this.invertedIndex.get(word).get(location).size());
				lookup.put(location, result);
				results.add(result);
			}
		}
	}

	/**
	 * Calls the necessary search algorithm.
	 *
	 * @param queries
	 * @param exact
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
