import java.util.Iterator;
import java.util.TreeSet;


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
		String out = "";
		Iterator<String> i = this.words.iterator();

		if (i.hasNext()) {
			out += i.next();
		}

		while (i.hasNext()) {
			out += " " + i.next();
		}

		return out;
	}


	@Override
	public int compareTo(Query o) {
		return this.toString().compareTo(o.toString());
	}

}
