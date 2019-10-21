import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
	 * Gets the unmodifiable Set of Queries.
	 *
	 * @return An unmodifiable Set of Queries.
	 */
	public Set<String> getQueries() {
		return Collections.unmodifiableSet(this.querySet.keySet());
	}

	/**
	 * Gets the results associated to a specific query.
	 *
	 * @param queryLine The line that we are looking for.
	 * @return An unmodifiable List of Results.
	 */
	public List<InvertedIndex.Result> getResults(String queryLine) {
		return Collections.unmodifiableList(this.querySet.get(queryLine));
	}

	/*
	 * TODO Remove this---should not be necessary with getQueries and getResults
	 * And it causes a copy so not ideal. Put a write method in this class just like
	 * you did to write the inverted index.
	 */
	/**
	 * Gets an unmodsifiable map of Queries to their results.
	 *
	 * @return An unmodifiable map of Queries to a List of Results.
	 */
	public Map<String, List<InvertedIndex.Result>> getUnmodifiableMap(){
		TreeMap<String, List<InvertedIndex.Result>> map = new TreeMap<>();
		for (String line : getQueries()) {
			map.put(line, getResults(line));
		}

		return Collections.unmodifiableMap(map);
	}


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
	 * @throws IOException Could happen.
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
