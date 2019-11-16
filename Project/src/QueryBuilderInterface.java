import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

/**
 * @author nedimazar
 *
 *
 * The interface that querybuilders will implement.
 *
 */
public interface QueryBuilderInterface {
	/**
	 * Get Queries
	 *
	 * @return a Set of Queries
	 */
	public Set<String> getQueries();

	/**
	 * Gets results
	 *
	 * @param queryLine a line of queries
	 * @return a list of results
	 */
	public List<InvertedIndex.Result> getResults(String queryLine);

	/**
	 * Method that writes the queries.
	 *
	 * @param outputFile Output file.
	 * @throws IOException Possible?
	 */
	public void writeQuery(Path outputFile) throws IOException;

	/**
	 * Function that checks if the map is empty.
	 *
	 * @return True if empty.
	 */
	public boolean isEmpty();


	/**
	 * Parses a line of query
	 *
	 * @param query query in question
	 * @param exactSearch exact search?
	 */
	public void parseQueryLine(String query, boolean exactSearch);

	/**
	 * Parses a query file
	 *
	 * @param path the path to the file
	 * @param exactSearch true if exact search
	 * @throws IOException could happen
	 */
	default void parseQueryFile(Path path, boolean exactSearch) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {
			String query;
			while ((query = reader.readLine()) != null) {
				parseQueryLine(query, exactSearch);
			}
		}
	}
}
