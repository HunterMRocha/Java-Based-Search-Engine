import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

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
	 * The set that will hold cleaned up queries.
	 */
	private TreeMap<Query, ArrayList<Result>> querySet;

	/**
	 * This is the path to the file containing the queries.
	 */
	private final Path queryPath;

	/**
	 * The default stemming algorithm that is used to clean up search queries.
	 */
	private static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;


	/**
	 * Constructor for the QueryBuilder class.
	 *
	 * @param invertedIndex The inverted index to use for querying.
	 * @param queryPath The path to the query input file.
	 * @throws IOException
	 */
	public QueryBuilder(InvertedIndex invertedIndex, Path queryPath) throws IOException {
		this.invertedIndex = invertedIndex;
		this.querySet = new TreeMap<>();
		this.queryPath = queryPath;
	}

	/**
	 * Getter for our querySet data structure.
	 *
	 * @return the map
	 */
	public Map<Query, ArrayList<Result>> getQuerySet() {
		return Collections.unmodifiableMap(this.querySet);
	}

	/**
	 * This function will open the query file, clean and stem the queries, and store them in a TreeSet.
	 * @throws IOException
	 */
	public void makeQueries() throws IOException {
		Stemmer stemmer = new SnowballStemmer(DEFAULT);


		try (BufferedReader reader = Files.newBufferedReader(this.queryPath, StandardCharsets.UTF_8);) {
			String query;

			while ((query = reader.readLine()) != null) {
				Query put = new Query();

				String[] parsedQuery = TextParser.parse(query);

				for (String word : parsedQuery) {
					String stemmed = stemmer.stem(word).toString();
					put.add(stemmed);
				}

				if (put.size() != 0) {
					this.querySet.put(put, null);
				}
			}
		}
	}

	/**
	 * This function will trigger an exact search on the queries.
	 */
	public void exactSearch() {
		for (Query query : this.querySet.keySet()) {
			this.querySet.put(query, this.invertedIndex.getResults(query));
		}
	}




	/**
	 * A simple main method to test the class.
	 *
	 * @param args
	 */
	public static void main(String args[]) {

	}





}
