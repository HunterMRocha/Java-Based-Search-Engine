
/**
 * Class that holds the result of a search. Every Query has a result pair.
 *
 * @author nedimazar
 *
 */
public class Result implements Comparable<Result>{

	/**
	 * This will hold the location of the search result.
	 */
	private String location;
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
	public Result() {
		this.location = "";
		this.count = 0;
		this.score = 0;
	}

	/**
	 * Sets the location data member.
	 *
	 * @param location
	 */
	public void setLocation(String location) {
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

	/**
	 * Sets the score data member.
	 *
	 * @param d
	 */
	public void setScore(double d) {
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
		out += "\"" + this.score + "\"";

		return out;
	}

	public String getWhereString() {
		return ("\"where\": " + "\"" + this.location + "\",");
	}

	public String getCountString() {
		return ("\"count\": " + this.count + ",");
	}

	public String getScoreString() {
		return ("\"score\": " + String.format("%.8f", this.score));
	}

	@Override
	public int compareTo(Result o) {
		double scoreDiff = this.score - o.score;

		if(scoreDiff != 0) {
			return scoreDiff > 0 ? -1 : 1;
		} else {
			int countDiff = this.count - o.count;

			if(countDiff != 0) {
				return countDiff> 0 ? -1 : 1;
			} else {
				return (this.location.toLowerCase().compareTo(o.location.toLowerCase()));
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
