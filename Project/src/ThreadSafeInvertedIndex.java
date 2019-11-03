import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author nedimazar
 *
 *         This is a Threadsafe version of the InvertedIndex class.
 *
 */
public class ThreadSafeInvertedIndex extends InvertedIndex {
	/**
	 * The lock object that will be used for multithreading.
	 */
	private final SimpleReadWriteLock lock;
	//TODO
	public final int numThreads;

	/**
	 * Initializes an instance of the ThreadSafeInvertedIndex class.
	 * @param numThreads Number of Threads
	 */
	public ThreadSafeInvertedIndex(int numThreads) {
		super();
		this.numThreads = numThreads;
		lock = new SimpleReadWriteLock();
	}

	@Override
	public Set<String> getWords() {
		lock.readLock().lock();
		try {
			return super.getWords();
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean add(String word, String filename, int position) {
		lock.writeLock().lock();
		try {
			return super.add(word, filename, position);
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public void writeIndex(Path outputFile) throws IOException {
		lock.readLock().lock();
		try {
			super.writeIndex(outputFile);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Map<String, Integer> getUnmodifiableCounts() {
		lock.readLock().lock();
		try {
			return super.getUnmodifiableCounts();
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean hasWord(String word) {
		lock.readLock().lock();
		try {
			return super.hasWord(word);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean hasLocation(String word, String location) {
		lock.readLock().lock();
		try {
			return super.hasLocation(word, location);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public ArrayList<Result> exactSearch(Collection<String> queries) {
		lock.readLock().lock();
		try {
			return super.exactSearch(queries);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public ArrayList<Result> partialSearch(Collection<String> queries) {
		lock.readLock().lock();
		try {
			return super.partialSearch(queries);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public ArrayList<Result> search(Collection<String> queries, boolean exact) {
		lock.readLock().lock();
		try {
			return super.search(queries, exact);
		} finally {
			lock.readLock().unlock();
		}
	}
}
