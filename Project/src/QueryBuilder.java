import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author nedimazar
 *
 *A class to build beautiful search queries for inverted index objects.
 */
public class QueryBuilder implements QueryBuilderInterface {

	/**
	 * This is the inverted index that the search query will be performed on.
	 */
	protected final InvertedIndex invertedIndex;

	/**
	 * The set that will hold cleaned up queries mapped to their results.
	 */
	public final TreeMap<String, ArrayList<InvertedIndex.Result>> querySet;

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
	@Override
	public Set<String> getQueries() {
		return Collections.unmodifiableSet(this.querySet.keySet());
	}

	/**
	 * Gets the results associated to a specific query.
	 *
	 * @param queryLine The line that we are looking for.
	 * @return An unmodifiable List of Results.
	 */
	@Override
	public List<InvertedIndex.Result> getResults(String queryLine) {
		if (this.querySet.get(queryLine) != null){
			return Collections.unmodifiableList(this.querySet.get(queryLine));
		} else {
			return Collections.emptyList();
		}
	}



	/**
	 * Method that writes the queries.
	 *
	 * @param outputFile Output file.
	 * @throws IOException Possible?
	 */
	@Override
	public void writeQuery(Path outputFile) throws IOException {
		SimpleJsonWriter.asQuery(this.querySet, outputFile);
	}


	/**
	 * Function that checks if the map is empty.
	 *
	 * @return True if empty.
	 */
	@Override
	public boolean isEmpty() {
		return this.querySet.keySet().size() == 0;
	}


	/**
	 * Parses a Query line made up of words.
	 *
	 * @param line The line we are parsing.
	 * @param exactSearch Wether we are doing exact search or not.
	 */
	@Override
	public void parseQueryLine(String line, boolean exactSearch) {
		TreeSet<String> queries = TextFileStemmer.uniqueStems(line);

		if (queries.size() == 0) {
			return;
		}

		String joined = String.join(" ", queries);

		if (querySet.containsKey(joined)) {
			return;
		}

		ArrayList<InvertedIndex.Result> local = invertedIndex.search(queries, exactSearch);
		this.querySet.put(joined, local);
	}

}
