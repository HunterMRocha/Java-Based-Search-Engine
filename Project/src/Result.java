
/**
 * Class that holds the result of a search. Every Query has a result pair.
 *
 * @author nedimazar
 *
 */
public class Result implements Comparable<Result>{
	/**
	 * The inverted index which the searches will be conducted on.
	 */
	private InvertedIndex invertedIndex;


	/**
	 *
	 */
	private String location;
	/**
	 *
	 */
	private int count;
	/**
	 *
	 */
	private float score;

	/**
	 * Constructor for Result object.
	 *
	 * @param invertedIndex the inverted index searches will be conducted on.
	 */
	public Result(InvertedIndex invertedIndex) {
		this.invertedIndex = invertedIndex;
		this.location = "";
		this.count = 0;
		this.score = 0;
		/** TODO Get rid of this ignore statement. */
		this.invertedIndex.hashCode();
	}

	/**
	 * Debug constructor.
	 *
	 * @param location set
	 * @param count set
	 * @param score set
	 */
	public Result(String location, int count, float score) {
		this.location = location;
		this.count = count;
		this.score = score;
	}

	@Override
	public String toString() {
		String out = "";

		out += "\"where\": ";
		out += "\"" + this.location + "\",\n";

		out += "\"count\": ";
		out += "\"" + this.count + "\",\n";

		out += "\"score\": ";
		out += "\"" + this.score + "\"\n";

		return out;
	}

	@Override
	public int compareTo(Result o) {
		float scoreDiff = this.score - o.score;

		if(scoreDiff != 0) {
			return scoreDiff > 0 ? 1 : -1;
		} else {
			int countDiff = this.count - o.count;

			if(countDiff != 0) {
				return countDiff;
			} else {
				return this.location.toLowerCase().compareTo(o.location.toLowerCase());
			}
		}
	}

	/**
	 * Simple main method for debugging.
	 *
	 * @param args Commandline arguments.
	 */
	public static void main (String args[]) {
		Result r = new Result("heRe", 10, (float) 1.24);
		Result r2 = new Result("here", 10, (float) 1.25);



		System.out.println(r.compareTo(r2));
		System.out.println(r2.compareTo(r));


	}

}
