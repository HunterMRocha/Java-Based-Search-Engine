import java.util.ConcurrentModificationException;

/**
 * Maintains a pair of associated locks, one for read-only operations and one
 * for writing. The read lock may be held simultaneously by multiple reader
 * threads, so long as there are no writers. The write lock is exclusive, but
 * also tracks which thread holds the lock. If unlock is called by any other
 * thread, a {@link ConcurrentModificationException} is thrown.
 *
 * @see SimpleLock
 * @see SimpleReadWriteLock
 */
public class SimpleReadWriteLock {

	/**
	 * n of reader
	 */
	private int reader;
	/**
	 * n of writer
	 */
	private int writer;

	/** The lock used for reading. */
	private final SimpleLock readerLock;

	/** The lock used for writing. */
	private final SimpleLock writerLock;

	/**
	 * a lock
	 */
	private final Object lock;


	/**
	 * Initializes a new simple read/write lock.
	 */
	public SimpleReadWriteLock() {
		readerLock = new ReadLock();
		writerLock = new WriteLock();
		lock = new Object();
		writer = 0;
		reader = 0;
	}

	/**
	 * Returns the reader lock.
	 *
	 * @return the reader lock
	 */
	public SimpleLock readLock() {
		// NOTE: DO NOT MODIFY THIS METHOD
		return readerLock;
	}

	/**
	 * Returns the writer lock.
	 *
	 * @return the writer lock
	 */
	public SimpleLock writeLock() {
		// NOTE: DO NOT MODIFY THIS METHOD
		return writerLock;
	}

	/**
	 * Determines whether the thread running this code and the other thread are
	 * in fact the same thread.
	 *
	 * @param other the other thread to compare
	 * @return true if the thread running this code and the other thread are not
	 * null and have the same ID
	 *
	 * @see Thread#getId()
	 * @see Thread#currentThread()
	 */
	public static boolean sameThread(Thread other) {
		// NOTE: DO NOT MODIFY THIS METHOD
		return other != null && other.getId() == Thread.currentThread().getId();
	}

	/**
	 * Used to maintain simultaneous read operations.
	 */
	private class ReadLock implements SimpleLock {

		@Override
		public void lock() {
			synchronized(lock) {
				while (writer > 0) {
					try {
						lock.wait();
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				reader++;
			}
		}

		@Override
		public void unlock() {
			synchronized(lock) {
				reader--;
				if (reader == 0) {
					lock.notifyAll();
				}
			}
		}
	}

	/**
	 * Used to maintain exclusive write operations.
	 */
	private class WriteLock implements SimpleLock {


		/**
		 * The write thread.
		 */
		private Thread writeThread;

		@Override
		public void lock() {
			synchronized(lock) {
				while (writer > 0 || reader > 0) {
					try {
						lock.wait();
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				writeThread = Thread.currentThread();
				writer++;
			}
		}


		@Override
		public void unlock() throws ConcurrentModificationException {
			if(sameThread(writeThread)) {
				synchronized(lock) {
					writer--;
					if (writer == 0) {
						writeThread = null;
						lock.notifyAll();
					}
				}
			}
			else {
				throw new ConcurrentModificationException();
			}
		}

	}
}
