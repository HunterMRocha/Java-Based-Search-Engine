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

public class ThreadSafeQueryBuilder extends QueryBuilder{
	//TODO
	private final ThreadSafeInvertedIndex invertedIndex;

	/**
	 * The set that will hold cleaned up queries mapped to their results.
	 */
	private final TreeMap<String, ArrayList<InvertedIndex.Result>> querySet;

	/**
	 * @param invertedIndex
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
		synchronized(querySet) {
			return Collections.unmodifiableSet(this.querySet.keySet());
		}
	}

	/**
	 * Gets the results associated to a specific query.
	 *
	 * @param queryLine The line that we are looking for.
	 * @return An unmodifiable List of Results.
	 */
	@Override
	public List<InvertedIndex.Result> getResults(String queryLine) {
		synchronized (querySet) {
			return Collections.unmodifiableList(this.querySet.get(queryLine));
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
		synchronized(querySet) {
			SimpleJsonWriter.asQuery(this.querySet, outputFile);
		}
	}


	/**
	 * Function that checks if the map is empty.
	 *
	 * @return True if empty.
	 */
	@Override
	public boolean isEmpty() {
		synchronized (querySet) {
			return this.querySet.keySet().size() == 0;
		}
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
		String joined = String.join(" ", queries);
		if (queries.size() != 0 && !querySet.containsKey(joined)) {
			this.querySet.put(joined, invertedIndex.search(queries, exactSearch));
		}
	}


	@Override
	public void parseQueryFile(Path path, boolean exactSearch) throws IOException {
		WorkQueue queue = new WorkQueue(this.invertedIndex.numThreads);
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {
			String query;
			while ((query = reader.readLine()) != null) {
				queue.execute(new Task(query, this.invertedIndex, exactSearch));
			}
		}
		queue.shutdown();
	}

	private class Task implements Runnable {
		/** The prime number to add or list. */
		private final String line;
		private final boolean exact;

		private final ThreadSafeInvertedIndex invertedIndex;

		public Task(String line, ThreadSafeInvertedIndex invertedIndex, boolean exact) {
			this.line = line;
			this.invertedIndex = invertedIndex;
			this.exact = exact;
		}

		//this function should actually check for prime numbers

		@Override
		public void run() {
			parseQueryLine(line, exact);
		}
	}

}
