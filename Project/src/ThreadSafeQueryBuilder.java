import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author nedimazar
 *
 */
public class ThreadSafeQueryBuilder extends QueryBuilder{
	/**
	 * The Thread Safe index
	 */
	private final ThreadSafeInvertedIndex invertedIndex;

	/**
	 * The set that will hold cleaned up queries mapped to their results.
	 */
	private final TreeMap<String, ArrayList<InvertedIndex.Result>> querySet;

	WorkQueue workQueue;

	/**
	 * @param invertedIndex the index to use
	 */
	public ThreadSafeQueryBuilder(ThreadSafeInvertedIndex invertedIndex) {
		super(invertedIndex);
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
		//		synchronized(querySet) {
		//			return Collections.unmodifiableSet(this.querySet.keySet());
		//		}
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
		//		synchronized (querySet) {
		//			return Collections.unmodifiableList(this.querySet.get(queryLine));
		//		}
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
		//		//synchronized(querySet) {
		//		SimpleJsonWriter.asQuery(this.querySet, outputFile);
		//		//}
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
		//		//synchronized(querySet) {
		//		TreeSet<String> queries = TextFileStemmer.uniqueStems(line);
		//		String joined = String.join(" ", queries);
		//		if (queries.size() != 0 && !querySet.containsKey(joined)) {
		//			this.querySet.put(joined, invertedIndex.search(queries, exactSearch));
		//		}
		//		//}

		super.parseQueryLine(line, exactSearch);
	}


	@Override
	public void parseQueryFile(Path path, boolean exactSearch, int numThreads) throws IOException {
		//synchronized(querySet) {
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

		//private final ThreadSafeInvertedIndex invertedIndex;

		/**
		 * @param line line to parse
		 * param invertedIndex index to use
		 * @param exact
		 */
		public Task(String line,/* ThreadSafeInvertedIndex invertedIndex,*/ boolean exact) {
			this.line = line;
			//this.invertedIndex = invertedIndex;
			this.exact = exact;
		}

		//this function should actually check for prime numbers

		@Override
		public void run() {
			synchronized(workQueue) {
				parseQueryLine(line, exact);
			}
		}
	}

}
