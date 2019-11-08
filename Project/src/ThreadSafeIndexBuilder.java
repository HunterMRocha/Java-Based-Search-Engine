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

	// TODO private final int threads;

	/**
	 * @param invertedIndex
	 */
	// TODO public ThreadSafeIndexBuilder (ThreadSafeInvertedIndex invertedIndex, int numThreads){
	public ThreadSafeIndexBuilder (ThreadSafeInvertedIndex invertedIndex){
		super(invertedIndex);
		this.invertedIndex = invertedIndex;
	}

	// TODO Remove numThreads as a parameter from traversePath
	@Override
	public void traversePath(Path path, int numThreads) throws IOException {
		WorkQueue queue = new WorkQueue(numThreads);
		for (Path currentPath : getTextFiles(path)) {
			if (isTextFile(currentPath)) {
				queue.execute(new Task(currentPath, this.invertedIndex));
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
	 * @author nedimazar
	 *
	 */
	private static class Task implements Runnable {

		/**
		 * Path to the file in question.
		 */
		private final Path path;

		/**
		 * The invertedIndex to use
		 */
		private final ThreadSafeInvertedIndex invertedIndex;

		/**
		 * TODO 
		 * @param path TODO
		 * @param invertedIndex TODO
		 */
		public Task(Path path, ThreadSafeInvertedIndex invertedIndex) {
			this.path = path;
			this.invertedIndex = invertedIndex;
		}

		@Override
		public void run() {
			try {
				addPath(path, invertedIndex);
				
				/* TODO
				InvertedIndex local = new InvertedIndex();
				addPath(path, local);
				invertedIndex.addAll(local);
				*/
				
			} catch (IOException e) {
				System.out.println("Problem encountered while adding file: " + path.toString());
			}
		}
	}
}
