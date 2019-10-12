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
	private TreeMap<String, ArrayList<InvertedIndex.Result>> querySet;

	/**
	 * This is the path to the file containing the queries.
	 */
	private final Path queryPath;

	/**
	 * Constructor for the QueryBuilder class.
	 *
	 * @param invertedIndex The inverted index to use for querying.
	 * @param queryPath The path to the query input file.

	 */
	public QueryBuilder(InvertedIndex invertedIndex, Path queryPath) {
		this.invertedIndex = invertedIndex;
		this.querySet = new TreeMap<>();
		this.queryPath = queryPath;
	}

	/**
	 * Getter for our querySet data structure.
	 *
	 * @return the map
	 */
	public Map<String, ArrayList<InvertedIndex.Result>> getQuerySet() {
		return Collections.unmodifiableMap(this.querySet);
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
	 * @param path
	 * @param exactSearch
	 * @throws IOException
	 */
	public void parseQueryFile(Path path, boolean exactSearch) throws IOException {

		try (BufferedReader reader = Files.newBufferedReader(this.queryPath, StandardCharsets.UTF_8);) {
			String query;
			while ((query = reader.readLine()) != null) {
				TreeSet<String> queries = TextFileStemmer.uniqueStems(query);
				String joined = String.join(" ", queries);
				if (queries.size() != 0 && !querySet.containsKey(joined)) {
					this.querySet.put(joined, invertedIndex.search(queries, exactSearch));
				}
			}
		}
	}
}
