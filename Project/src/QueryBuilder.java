import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author nedimazar
 *
 *A class to build beautiful search queries for inverted index objects.
 */
public class QueryBuilder {

	/**
	 * This is the inverted index that the search query will be performed on.
	 */
	private final InvertedIndex invertedIndex;

	/**
	 * The set that will hold cleaned up queries mapped to their results.
	 */
	private final TreeMap<String, ArrayList<InvertedIndex.Result>> querySet;


	/**
	 * Constructor for the QueryBuilder class.
	 *
	 * @param invertedIndex The inverted index to use for querying.
	 */
	public QueryBuilder(InvertedIndex invertedIndex) {
		this.invertedIndex = invertedIndex;
		this.querySet = new TreeMap<>();
	}

	/**
	 * Getter for our querySet data structure.
	 *
	 * @return the map
	 */
	public Map<String, ArrayList<InvertedIndex.Result>> getQuerySet() { // TODO Remove
		return Collections.unmodifiableMap(this.querySet);
	}

	/* TODO
	public Set<String> getQueries() {
		returns the unmodifiable keyset of querySet
	}

	public List<InvertedIndex.Result> getResults(String queryLine) {
		return the unmodifiable list
	}
	 */

	/**
	 * Function that checks if the map is empty.
	 *
	 * @return True if empty.
	 */
	public boolean isEmpty() {
		return this.querySet.keySet().size() == 0;
	}

	/**
	 * Gets queries from the input path and performs the searches.
	 *
	 * @param path The path to the Query file.
	 * @param exactSearch True if we are doing exact search.
	 * @throws IOException
	 */
	public void parseQueryFile(Path path, boolean exactSearch) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {
			String query;
			while ((query = reader.readLine()) != null) {
				parseQueryLine(query, exactSearch);
			}
		}
	}


	/**
	 * Parses a Query line made up of words.
	 *
	 * @param line The line we are parsing.
	 * @param exactSearch Wether we are doing exact search or not.
	 */
	public void parseQueryLine(String line, boolean exactSearch) {
		TreeSet<String> queries = TextFileStemmer.uniqueStems(line);
		String joined = String.join(" ", queries);
		if (queries.size() != 0 && !querySet.containsKey(joined)) {
			this.querySet.put(joined, invertedIndex.search(queries, exactSearch));
		}
	}

}
