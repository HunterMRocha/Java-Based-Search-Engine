import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringEscapeUtils;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Search engine front end class hello.
 *
 */
public class SearchServlet extends HttpServlet {

	/**
	 * This has to be here to get rid of warnings.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Default stemmer.
	 */
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

	/** The title to use for this webpage. */
	private static final String TITLE = "NedSearch";

	/** Everything that will be output after a search is carried out. */
	private ConcurrentLinkedQueue<String> outputQueue; // TODO Dangerous as a member, remember this stuff is multithreaded

	/**
	 * The crawler of the web.
	 */
	private WebCrawler webCrawler;


	/**
	 * The number of searches
	 */
	private int searches = 0;

	/**
	 * The time it took
	 */
	private long seconds;

	/**
	 * initializes the LinkedQueue for the output messages.
	 *
	 * @param queryBuilder The query builder
	 * @param index
	 *
	 */
	public SearchServlet(QueryBuilderInterface queryBuilder, ThreadSafeInvertedIndex index) {
		super();
		outputQueue = new ConcurrentLinkedQueue<>();
	}

	/**
	 * Constructor of the search servlet.
	 *
	 * @param queryBuilder builds the query
	 * @param threadSafe an index that is threadsafe
	 * @param webCrawler crawl the web
	 */
	public SearchServlet(QueryBuilderInterface queryBuilder, ThreadSafeInvertedIndex threadSafe,
			WebCrawler webCrawler) {
		super();
		this.webCrawler = webCrawler;
		outputQueue = new ConcurrentLinkedQueue<>();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);


		PrintWriter out = response.getWriter();

		out.printf("<!DOCTYPE html>%n");
		out.printf("<html>%n");
		out.printf("<body style=\"background-color:moccasin;\">");
		out.printf("<head>%n");
		out.printf("	<meta charset=\"utf-8\">%n");
		out.printf("	<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">%n");
		out.printf("	<title>%s</title>%n", TITLE);
		out.printf("	<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/bulma/0.7.4/css/bulma.min.css\">%n");
		out.printf("	<script defer src=\"https://use.fontawesome.com/releases/v5.8.1/js/all.js\" integrity=\"sha384-g5uSoOSBd7KkhAMlnQILrecXvzst9TdC09/VM+pjDTCM+1il8RHz5fKANTFFb+gQ\" crossorigin=\"anonymous\"></script>%n");

		out.printf("</head>%n");
		out.printf("%n");
		out.printf("<body>%n");
		out.printf("	  <div class=\"hero-body\">%n");
		out.printf("	    <div class=\"center container\" style=\"text-align:center\">%n");
		out.printf("<figure class=\"image is-96x96\">\n"
				+ "  <img  src=\"https://nedbiosystems.com/wp-content/uploads/2017/08/NED-circle-low-res.png\">\n"
				+ "</figure>");
		out.printf("	      <h1 class=\"title\">%n");
		out.printf(	        TITLE + "%n");
		out.printf("	      </h1>%n");
		out.printf("<p1>We'll get it for you.</p1>");
		out.printf("	<section class=\"section\">%n");
		out.printf("		<div class=\"container\">%n");
		out.printf("      <h2 class=\"subtitle\">\n");
		out.printf("      </h2>");
		out.printf("	  </div>%n");
		out.printf("	    </div>%n");
		out.printf("	  </div>%n");
		out.printf("	</section>%n");
		out.printf("<h2 class=\"subtitle\">\n");
		out.printf("%n");
		out.printf("	<section class=\"section\">%n");
		out.printf("		<div class=\"container\">%n");
		out.printf("%n");
		out.printf("			<form method=\"%s\" action=\"%s\">%n", "POST", request.getServletPath());
		out.printf("				<div class=\"field\">%n");
		out.printf("					<div class=\"control has-icons-left\">%n");
		out.printf("						<input class=\"input\" type=\"text\" name=\"%s\" placeholder=\"I'm a humble search bar...\">%n", "search");
		out.printf("						<span class=\"icon is-small is-left\">%n");
		out.printf("							<i class=\"fas fa-search\"></i>%n");
		out.printf("						</span>%n");
		out.printf("					</div>%n");
		out.printf("				</div>%n");
		out.printf("%n");
		out.printf("%n");
		out.printf("<input type = \"checkbox\" name = \"exact\" id = \"exact\" > Exact");
		out.printf("%n");
		out.printf("				<div class=\"control\">%n");
		out.printf("			    <button class=\"button is-link\" type=\"submit\">%n");
		out.printf("						<i class=\"fas fa-tachometer-alt\"></i>%n");
		out.printf("						&nbsp;%n");
		out.printf(TITLE + "%n");
		out.printf("					</button>%n");
		out.printf("			    <button class=\"button is-link\" onclick=\"clicked()\" name=\"lucky\" type=\"submit\">%n");
		out.printf("						<i class=\"fas fa-glass-cheers\"></i>%n");
		out.printf("						&nbsp;%n");
		out.printf("I'm feeling lucky.%n");
		out.printf("					</button>%n");

		out.printf("			  </div>%n");
		out.printf("			</form>%n");
		out.printf("		</div>%n");
		out.printf("%n");
		out.printf("	<section class=\"section\">%n");
		out.printf("		<div class=\"container\">%n");
		if (!outputQueue.isEmpty()) {
			for (String message : outputQueue) {
				out.printf("				<div class=\"box\">%n");
				out.printf(message);
				out.printf("				</div>%n");
				out.printf("%n");
			}
		}
		out.printf("			</div>%n");
		out.printf("%n");
		out.printf("		</div>%n");
		out.printf("	</section>%n");
		out.printf("	<footer class=\"footer\">%n");
		out.printf("	  <div class=\"content has-text-centered\">%n");
		out.printf("	    <p>%n");
		out.printf("	      This request was handled by thread %s.%n", Thread.currentThread().getName());
		out.printf("	    </p>%n");
		out.printf("	      <p>");
		out.printf("<p1>Results Found: " + searches + "</p1>%n");
		out.printf("	      </p>%n");
		out.printf("	    <p>%n");
		out.printf(seconds + " ms.</p1>%n");
		out.printf("	    </p>%n");
		out.printf("%n");
		out.printf("	    <p>%n");
		out.printf("					<i class=\"fas fa-calendar-alt fa-pulse\"></i>%n");
		out.printf("					&nbsp;Last Visited %s%n", getDate());
		out.printf("	    </p>%n");
		out.printf("<br />");
		out.printf("	  </div>");
		out.printf("	</footer>");
		out.printf("</body>");
		out.printf("</html>");

		response.setStatus(HttpServletResponse.SC_OK);
	}





	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		long st = System.nanoTime();
		String message = request.getParameter("search");
		String checkBox = request.getParameter("exact");

		if (message == null) {
			message = "";
		}

		//Can't attack me.
		message = StringEscapeUtils.escapeHtml4(message);

		response.getWriter();

		boolean exact;
		if (checkBox!= null && checkBox.contains("on")) {
			exact = true;
		} else {
			exact = false;
		}

		String formatString = null;

		SnowballStemmer stemmer = new SnowballStemmer(DEFAULT);
		ArrayList<String> queryList = new ArrayList<String>();
		for (String part : message.split(" ")) {
			if (part == " ") {
				part = "";
			}
			queryList.add((stemmer.stem(part.toLowerCase())).toString());
		}

		List<InvertedIndex.Result> results = this.webCrawler.search(queryList, exact);

		if (results == null || results.isEmpty()) {
			searches = 0;
			outputQueue.clear();
			formatString = String.format(
					"					<i class=\"fas fa-quote-left has-text-grey-light\"></i> %s <i class=\"fas fa-quote-right has-text-grey-light\"></i>%n"
							+ "					<p class=\"has-text-grey is-size-7 has-text-right\"></p>%n",
							"The String: " + request.getParameter("search") + " Does not exist", getDate());
			outputQueue.add(formatString);
		} else {
			searches = 0;
			outputQueue.clear();
			for (ThreadSafeInvertedIndex.Result result : results) {
				formatString = String.format(
						"<a href=\"%s\">%s</a>"
								+ "					<p class=\"has-text-grey is-size-7 has-text-right\">%s</p>%n",
								result.getLocation(), result.getLocation(), getDate());
				searches++;
				outputQueue.add(formatString);
			}
		}

		seconds =  (System.nanoTime() - st) / 1000000;
		response.setStatus(HttpServletResponse.SC_OK);
		response.sendRedirect(request.getServletPath());
		response.flushBuffer();
	}

	/**
	 * Returns the date and time in a long format. For example: "12:00 am on
	 * Saturday, January 01 2000".
	 *
	 * @return current date and time
	 */
	private static String getDate() {
		String format = "hh:mm a 'on' EEEE, MMMM dd yyyy";
		DateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(new Date());
	}
}