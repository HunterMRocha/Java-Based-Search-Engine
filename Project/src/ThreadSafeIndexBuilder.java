import java.io.IOException;
import java.nio.file.Path;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

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
	 * The default stemming algorithm.
	 */
	private static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;


	/**
	 * @param invertedIndex
	 */
	public ThreadSafeIndexBuilder (ThreadSafeInvertedIndex invertedIndex){
		super(invertedIndex);
		this.invertedIndex = invertedIndex;
	}


	//PUT WORK QUEUE
	@Override
	public void traversePath(Path path) throws IOException {
		WorkQueue queue = new WorkQueue(this.invertedIndex.numThreads);
		for (Path currentPath : getTextFiles(path)) {
			if (isTextFile(currentPath)) {
				queue.execute(new Task(currentPath, this.invertedIndex));
				//addPath(currentPath);
			}
		}
		queue.shutdown();
	}

	/**
	 * @author nedimazar
	 *
	 */
	private static class Task implements Runnable {
		/** The prime number to add or list. */
		private final Path path;

		private final ThreadSafeInvertedIndex invertedIndex;

		/**
		 * @param path
		 * @param invertedIndex
		 */
		public Task(Path path, ThreadSafeInvertedIndex invertedIndex) {
			this.path = path;
			this.invertedIndex = invertedIndex;
		}

		//this function should actually check for prime numbers

		@Override
		public void run() {
			try {
				addPath(path, invertedIndex);
			} catch (IOException e) {
				//TODO
			}
		}
	}
}
