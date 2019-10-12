import java.util.Comparator;

/*
 * TODO Try to make this an inner class (public) in InvertedIndex
 * 
 * InvertedIndex.Result
 */

/**
 * Class that holds the result of a search. Every Query has a result pair.
 *
 * @author nedimazar
 *
 */
public class Result implements Comparable<Result> {

	/**
	 * This will hold the location of the search result.
	 */
	private String location; // TODO make final
	/**
	 * This will hold the count of matches.
	 */
	private int count;
	/**
	 * This will hold the scoire of the search result.
	 */
	private double score;

	/**
	 * Constructor for Result object.
	 *
	 */
	public Result() { // TODO public Result(location)
		this.location = "";
		this.count = 0;
		this.score = 0;
	}

	/**
	 * Debug constructor.
	 *
	 * @param location set
	 * @param count set
	 * @param score set
	 */
	public Result(String location, int count, double score) {
		this.location = location;
		this.count = count;
		this.score = score;
	}

	/**
	 * Sets the location data member.
	 *
	 * @param location
	 */
	public void setLocation(String location) { // TODO Remove
		this.location = location;
	}

	/**
	 * Sets the count data member.
	 *
	 * @param count
	 */
	public void setCount(int count) {
		this.count = count;
	}
	
	/* TODO
	public void addCount(int count) {
		this.count += count;
	}
	*/

	/**
	 * Sets the score data member.
	 *
	 * @param d
	 */
	public void setScore(double d) { // TODO Remove
		this.score = d;
	}

	/**
	 * Getter for the count data member.
	 *
	 * @return the count data member
	 */
	public int getCount() {
		return this.count;
	}

	/**
	 * Getter for the score data member.
	 *
	 * @return the score
	 */
	public double getScore() {
		// TODO Get the total directly from the word count in your inverted index
		// return (double) this.count / total
		// --or-- calculate the score again every time you change the count
		return this.score;
	}

	/**
	 * Chescks if another Results location is the same as this ones.
	 *
	 * @param other
	 * @return true if same;
	 */
	public boolean sameLocation(Result other) {
		return this.location.compareTo(other.location) == 0;
	}



	@Override
	public String toString() {
		// TODO Avoid string concatenation
		String out = "";

		out += "\"where\": ";
		out += "\"" + this.location + "\",\n";

		out += "\"count\": ";
		out += "\"" + this.count + "\",\n";

		out += "\"score\": ";
		out += "\"" + this.score + "\"";

		return out;
	}

	/**
	 * @return A formatted string ready to write.
	 */
	public String getWhereString() {
		return ("\"where\": " + "\"" + this.location + "\",");
	}

	/**
	 * @return A formatted string ready to write.
	 */
	public String getCountString() {
		return ("\"count\": " + this.count + ",");
	}

	/**
	 * @return A formatted string ready to write.
	 */
	public String getScoreString() {
		return ("\"score\": " + String.format("%.8f", this.score));
	}


	@Override
	public int compareTo(Result o) {
		return Comparator.comparing(Result::getScore, Comparator.reverseOrder())
				.thenComparing(Result::getCount, Comparator.reverseOrder())
				.thenComparing(Result::getWhereString)
				.compare(this, o);
	}

}
