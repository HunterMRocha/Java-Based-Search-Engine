import java.io.IOException;
import java.nio.file.Path;

/**
 * @author nedimazar
 *
 */
public class ThreadSafeIndexBuilder extends InvertedIndexBuilder {


	/**
	 * The Inverted Index to populate
	 */
	private final ThreadSafeInvertedIndex invertedIndex;

	/**
	 * Number of Threads
	 */
	private final int numThreads;

	/**
	 * Constructor for the builder class
	 *
	 * @param invertedIndex The index to use
	 * @param numThreads the number of threads to use
	 */
	public ThreadSafeIndexBuilder (ThreadSafeInvertedIndex invertedIndex, int numThreads){
		super(invertedIndex);
		this.invertedIndex = invertedIndex;
		this.numThreads = numThreads;
	}



	/**
	 * Adds all files in the path to the index.
	 *
	 * @param path start path
	 * @throws IOException could happen
	 */
	@Override
	public void traversePath(Path path) throws IOException {
		WorkQueue queue = new WorkQueue(numThreads);
		for (Path currentPath : getTextFiles(path)) {
			if (isTextFile(currentPath)) {
				queue.execute(new Task(currentPath));
			}
		}
		try {
			queue.finish();
		} catch (Exception e) {
			System.out.println("The work queue encountered an error.");
		}
		queue.shutdown();
	}

	/**
	 * A task class for multithreadig.
	 * @author nedimazar
	 *
	 */
	private class Task implements Runnable {

		/**
		 * Path to the file in question.
		 */
		private final Path path;


		/**
		 * Constructor for the Task.
		 * @param path the path to work on.
		 */
		public Task(Path path) {
			this.path = path;
		}

		@Override
		public void run() {
			try {
				InvertedIndex local = new InvertedIndex();
				addPath(path, local);
				invertedIndex.addAll(local);

			} catch (IOException e) {
				System.out.println("Problem encountered while adding file: " + path.toString());
			}
		}
	}
}
