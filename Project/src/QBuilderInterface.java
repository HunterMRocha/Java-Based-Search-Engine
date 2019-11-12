import java.io.IOException;
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
public interface QBuilderInterface {
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
	 * Parses a query file
	 *
	 * @param queryPath The path to start at
	 * @param hasFlag checks for flag
	 * @param numThreads number of threads
	 * @throws IOException could happen
	 */
	public void parseQueryFile(Path queryPath, boolean hasFlag, int numThreads) throws IOException;



}
