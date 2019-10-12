import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

/*
 * TODO Not sure you really need this class
 */

/**
 * @author nedimazar
 * This class stores each word in a query and makes the collection comparable.
 */
public class Query implements Comparable<Query>{

	/**
	 * All the words in a single Query.
	 */
	private TreeSet<String> words;

	/**
	 * Constructor for the class Query.
	 */
	public Query() {
		this.words = new TreeSet<>();
	}

	/**
	 * A getter method for the words of a query.
	 *
	 * @return An unmodifiable set of the words.
	 */
	public Set<String> getWords() {
		return Collections.unmodifiableSet(this.words);
	}

	/**
	 * Adds a word to the query.
	 *
	 * @param word word to be added
	 * @return true if succesfully added
	 */
	public boolean add(String word) {
		return this.words.add(word);
	}

	/**
	 * Size of the query in words.
	 *
	 * @return number of words.
	 */
	public int size() {
		return this.words.size();
	}

	@Override
	public String toString() {
		return String.join(" ", this.words);
	}


	@Override
	public int compareTo(Query o) {
		return this.toString().compareTo(o.toString());
	}

}
