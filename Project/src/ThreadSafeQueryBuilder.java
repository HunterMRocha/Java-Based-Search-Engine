import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author nedimazar
 *
 */
public class ThreadSafeQueryBuilder extends QueryBuilder{
	/**
	 * Our workQueue
	 */
	WorkQueue workQueue;

	/**
	 * @param invertedIndex the index to use
	 */
	public ThreadSafeQueryBuilder(ThreadSafeInvertedIndex invertedIndex) {
		super(invertedIndex);
		new TreeMap<>();
	}

	/**
	 * Gets the unmodifiable Set of Queries.
	 *
	 * @return An unmodifiable Set of Queries.
	 */
	@Override
	public Set<String> getQueries() {
		return super.getQueries();
	}

	/**
	 * Gets the results associated to a specific query.
	 *
	 * @param queryLine The line that we are looking for.
	 * @return An unmodifiable List of Results.
	 */
	@Override
	public List<InvertedIndex.Result> getResults(String queryLine) {
		return super.getResults(queryLine);
	}

	/**
	 * Method that writes the queries.
	 *
	 * @param outputFile Output file.
	 * @throws IOException Possible?
	 */
	@Override
	public void writeQuery(Path outputFile) throws IOException {
		super.writeQuery(outputFile);
	}


	/**
	 * Function that checks if the map is empty.
	 *
	 * @return True if empty.
	 */
	@Override
	public boolean isEmpty() {
		return super.isEmpty();
	}


	/**
	 * Parses a Query line made up of words.
	 *
	 * @param line The line we are parsing.
	 * @param exactSearch Wether we are doing exact search or not.
	 */
	@Override
	public void parseQueryLine(String line, boolean exactSearch) {
		super.parseQueryLine(line, exactSearch);
	}


	@Override
	public void parseQueryFile(Path path, boolean exactSearch, int numThreads) throws IOException {
		this.workQueue = new WorkQueue(numThreads);
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {
			String query;
			while ((query = reader.readLine()) != null) {
				workQueue.execute(new Task(query/*, this.invertedIndex,*/, exactSearch));
			}
		}
		try {
			workQueue.finish();
		} catch (Exception e) {
			// TODO Fix
		}
		workQueue.shutdown();
	}

	/**
	 * @author nedimazar
	 *
	 * The inner runnable task class
	 */
	private class Task implements Runnable {
		/** The prime number to add or list. */
		private final String line;
		/**
		 * Exact search or not
		 */
		private final boolean exact;

		/**
		 * @param line line to parse
		 * param invertedIndex index to use
		 * @param exact
		 */
		public Task(String line, boolean exact) {
			this.line = line;
			this.exact = exact;
		}

		@Override
		public void run() {
			synchronized(workQueue) { // TODO Prevents all multithreading
				parseQueryLine(line, exact);
			}
		}
	}
	
	/*
	 * TODO 
	 * 
	 * Create a QueryBuilderInterface with the common methods between these two classes
	 * Implement this interface in both QueryBuilder and ThreadSafeQueryBuilder, each of
	 * these classes need their own parseQueryLine implementation.
	 */

}
