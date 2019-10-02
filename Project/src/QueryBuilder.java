import java.io.IOException;
import java.nio.file.Path;

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
	 * The default stemming algorithm that is used to clean up search queries.
	 */
	private static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;


	/**
	 * Constructor for the QueryBuilder class.
	 *
	 * @param invertedIndex The inverted index to use for querying.
	 * @throws IOException
	 */
	public QueryBuilder(InvertedIndex invertedIndex) throws IOException {
		this.invertedIndex = invertedIndex;
		this.invertedIndex.writeIndex(Path.of("test.txt"));
	}




	/**
	 * A simple main method to test the class.
	 *
	 * @param args
	 */
	public static void main(String args[]) {

	}
}
