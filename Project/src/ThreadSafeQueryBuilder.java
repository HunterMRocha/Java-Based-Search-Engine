import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
 */
public class ThreadSafeQueryBuilder implements QueryBuilderInterface {
	/**
	 * Our workQueue
	 */

	/**
	 * This is the inverted index that the search query will be performed on.
	 */
	private final InvertedIndex invertedIndex;

	/**
	 * The set that will hold cleaned up queries mapped to their results.
	 */
	private final TreeMap<String, ArrayList<InvertedIndex.Result>> querySet;

	/**
	 * Number of Threads
	 */
	private final int numThreads;


	/**
	 * @param invertedIndex the index to use
	 * @param numThreads number of threads
	 */
	public ThreadSafeQueryBuilder(ThreadSafeInvertedIndex invertedIndex, int numThreads) {
		this.invertedIndex = invertedIndex;
		this.querySet = new TreeMap<>();
		this.numThreads = numThreads;
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
		return Collections.unmodifiableList(this.querySet.get(queryLine));
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

		synchronized (querySet) {
			if (querySet.containsKey(joined)) {
				return;
			}
		}

		ArrayList<InvertedIndex.Result> local = invertedIndex.search(queries, exactSearch);

		synchronized (querySet) {
			this.querySet.put(joined, local);
		}
	}


	/**
	 * Parses a query file
	 *
	 * @param path the path to start at
	 * @param exactSearch the kind of search
	 * @throws IOException could happen
	 */
	@Override
	public void parseQueryFile(Path path, boolean exactSearch) throws IOException {
		WorkQueue workQueue = new WorkQueue(this.numThreads);
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {
			String query;
			while ((query = reader.readLine()) != null) {
				workQueue.execute(new Task(query, exactSearch));
			}
		}
		try {
			workQueue.finish();
		} catch (Exception e) {
			System.out.println("An InterruptedException happened while locking.");
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
			parseQueryLine(line, exact);
		}
	}
}
