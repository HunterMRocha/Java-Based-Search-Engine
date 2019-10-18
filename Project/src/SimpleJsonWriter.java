import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Outputs several simple data structures in "pretty" JSON format where newlines
 * are used to separate elements and nested elements are indented.
 *
 * Warning: This class is not thread-safe. If multiple threads access this class
 * concurrently, access must be synchronized externally.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2019
 */
public class SimpleJsonWriter {

	/**
	 * Writes the elements as a pretty JSON array.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException
	 *
	 */
	public static void asArray(Collection<Integer> elements, Writer writer, int level) throws IOException{
		writer.write("[\n");

		Iterator<Integer> integers = elements.iterator();

		if (integers.hasNext()) {
			indent(writer, level +1);
			writer.write(integers.next().toString());
		}

		while (integers.hasNext()) {
			writer.write(",\n");
			indent(writer, level +1);
			writer.write(integers.next().toString());
		}

		writer.write("\n");
		indent(writer, level);
		writer.write("]");
	}

	/**
	 * Writes the elements as a pretty JSON array to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException
	 *
	 * @see #asArray(Collection, Writer, int)
	 */
	public static void asArray(Collection<Integer> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asArray(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON array.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asArray(Collection, Writer, int)
	 */
	public static String asArray(Collection<Integer> elements) {
		try {
			StringWriter writer = new StringWriter();
			asArray(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the elements as a pretty JSON object.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException
	 */
	public static void asObject(Map<String, Integer> elements, Writer writer, int level) throws IOException{
		writer.write("{\n");

		Iterator<Map.Entry<String, Integer>> entries = elements.entrySet().iterator();

		if (entries.hasNext()) {
			Map.Entry<String, Integer> entry = entries.next();
			String key = entry.getKey();
			String value = entry.getValue().toString();

			indent(writer, level + 1);
			quote(key, writer);
			writer.write(": " + value);
		}

		while (entries.hasNext()) {
			Map.Entry<String, Integer> entry = entries.next();
			String key = entry.getKey();
			String value = entry.getValue().toString();

			writer.write(",\n");

			indent(writer, level + 1);
			quote(key, writer);
			writer.write(": " + value);
		}

		writer.write("\n}\n");
	}

	/**
	 * Writes the elements as a pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException
	 *
	 * @see #asObject(Map, Writer, int)
	 */
	public static void asObject(Map<String, Integer> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asObject(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON object.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asObject(Map, Writer, int)
	 */
	public static String asObject(Map<String, Integer> elements) {
		try {
			StringWriter writer = new StringWriter();
			asObject(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the elements as a nested pretty JSON object. The generic notation used
	 * allows this method to be used for any type of map with any type of nested
	 * collection of integer objects.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException
	 */
	public static void asNestedObject(Map<String, ? extends Collection<Integer>> elements, Writer writer, int level)
			throws IOException {
		writer.write("{\n");
		Iterator<String> keys = elements.keySet().iterator();

		if (keys.hasNext()) {
			String next = keys.next();
			indent(writer, level + 1);
			quote(next, writer);
			writer.write(": ");
			asArray(elements.get(next), writer, level + 1);
		}

		while (keys.hasNext()) {
			String next = keys.next();
			writer.write(",\n");
			indent(writer, level + 1);
			quote(next, writer);
			writer.write(": ");
			asArray(elements.get(next), writer, level + 1);
		}

		writer.write("\n}");
	}

	/**
	 * Writes the elements as a nested pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException
	 *
	 * @see #asNestedObject(Map, Writer, int)
	 */
	public static void asNestedObject(Map<String, ? extends Collection<Integer>> elements, Path path)
			throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asNestedObject(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a nested pretty JSON object.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asNestedObject(Map, Writer, int)
	 */
	public static String asNestedObject(Map<String, ? extends Collection<Integer>> elements) {
		try {
			StringWriter writer = new StringWriter();
			asNestedObject(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the invertedIndex provided with a writer.
	 * @param invertedIndex
	 * @param writer
	 * @param level
	 * @throws IOException
	 */
	public static void asInvertedIndex(TreeMap<String, TreeMap<String, TreeSet<Integer>>> invertedIndex, Writer writer,
			int level) throws IOException {
		Iterator<String> iterator = invertedIndex.keySet().iterator();
		writer.write("{");

		if (iterator.hasNext()) {
			String key = iterator.next();
			writer.write("\n");
			indent(writer, level + 1);
			quote(key, writer);
			writer.write(": ");

			asNestedObject(invertedIndex.get(key), writer, level + 1);
		}

		while (iterator.hasNext()) {
			String key = iterator.next();
			writer.write(",\n");
			indent(writer, level + 1);
			quote(key, writer);
			writer.write(": ");
			asNestedObject(invertedIndex.get(key), writer, level + 1);
		}

		indent("\n}", writer, level);
	}

	/**
	 * Calls the other invertedIndex method
	 * @param invertedIndex
	 * @param path
	 * @throws IOException
	 */
	public static void asInvertedIndex(TreeMap<String, TreeMap<String, TreeSet<Integer>>> invertedIndex, Path path)
			throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asInvertedIndex(invertedIndex, writer, 0);
		}
	}

	/**
	 * Writes the {@code \t} tab symbol by the number of times specified.
	 *
	 * @param writer the writer to use
	 * @param times  the number of times to write a tab symbol
	 * @throws IOException
	 */
	public static void indent(Writer writer, int times) throws IOException {
		for (int i = 0; i < times; i++) {
			writer.write('\t');
		}
	}

	/**
	 * Indents and then writes the element.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * @throws IOException
	 *
	 * @see #indent(String, Writer, int)
	 * @see #indent(Writer, int)
	 */
	public static void indent(Integer element, Writer writer, int times) throws IOException {
		indent(element.toString(), writer, times);
	}

	/**
	 * Indents and then writes the element.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * @throws IOException
	 *
	 * @see #indent(Writer, int)
	 */
	public static void indent(String element, Writer writer, int times) throws IOException {
		indent(writer, times);
		writer.write(element);
	}

	/**
	 * Writes the element surrounded by {@code " "} quotation marks.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @throws IOException
	 */
	public static void quote(String element, Writer writer) throws IOException {
		writer.write('"');
		writer.write(element);
		writer.write('"');
	}

	/**
	 * Indents and then writes the element surrounded by {@code " "} quotation
	 * marks.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * @throws IOException
	 *
	 * @see #indent(Writer, int)
	 * @see #quote(String, Writer)
	 */
	public static void quote(String element, Writer writer, int times) throws IOException {
		indent(writer, times);
		quote(element, writer);
	}

	/**
	 * This beast will write the Query-Result pairs to a file.
	 *
	 * @param map Set of queries that will be written.
	 * @param path The path where it will be written.
	 * @param writer The writer to use.
	 * @param level How many initial indentations.
	 * @throws IOException Could happen!
	 */
	public static void asQuery(Map<String, List<InvertedIndex.Result>> map, Path path, Writer writer, int level) throws IOException {
		Map<String, List<InvertedIndex.Result>> temp = new TreeMap<>();

		for (String q : map.keySet()) {
			ArrayList<InvertedIndex.Result> innerTemp = new ArrayList<>();
			innerTemp.addAll(map.get(q));
			temp.put(q, innerTemp);
		}

		map = temp;



		writer.write("{\n");
		var iterator = map.keySet().iterator();

		if (iterator.hasNext()) {
			String nextQuery = iterator.next();
			indent(writer, level +1);

			writer.write("\"" + nextQuery.toString() + "\": [");

			indent(writer, level + 1);


			var innerIterator = map.get(nextQuery).iterator();

			if (innerIterator.hasNext()) {
				withExtraTab(innerIterator, writer, level);

			}

			while (innerIterator.hasNext()) {
				withoutTab(innerIterator, writer, level);

			}
			writer.write("\n");
			indent(writer, level + 1);
			writer.write("]");
			indent(writer, level);

		}

		while (iterator.hasNext()) {
			String nextQuery = iterator.next();
			writer.write(",\n");
			indent(writer, level +1);

			writer.write("\"" + nextQuery.toString() + "\": [");

			indent(writer, level + 1);


			var innerIterator = map.get(nextQuery).iterator();

			boolean bob = true;
			if (innerIterator.hasNext()) {
				withExtraTab(innerIterator, writer, level);
			}

			while (innerIterator.hasNext()) {
				withoutTab(innerIterator, writer, level);
			}

			if(!bob) {indent(writer, level +1);}
			writer.write("\n\t]");
			indent(writer, level);
		}
		writer.write("\n}");

	}

	/**
	 * A helper method for writing as Query.
	 *
	 * @param innerIterator
	 * @param writer
	 * @param level
	 * @throws IOException
	 */
	private static void withoutTab(Iterator<InvertedIndex.Result> innerIterator, Writer writer, int level) throws IOException {
		writer.write(",\n");
		indent(writer, level + 2);
		writer.write("{\n");
		indent(writer, level + 3);
		var nexto = innerIterator.next();
		writer.write(nexto.getWhereString() + "\n");
		indent(writer, level + 3);
		writer.write(nexto.getCountString() + "\n");
		indent(writer, level + 3);
		writer.write(nexto.getScoreString() + "\n");
		indent(writer, level + 2);

		writer.write("}");
		indent(writer, level);

	}

	/**
	 * A helper method for writing as Query.
	 *
	 * @param innerIterator
	 * @param writer
	 * @param level
	 * @throws IOException
	 */
	private static void withExtraTab(Iterator<InvertedIndex.Result> innerIterator, Writer writer, int level) throws IOException {
		writer.write("\n");
		indent(writer, level + 1);
		writer.write("\t{\n");
		indent(writer, level + 3);
		var nexto = innerIterator.next();
		writer.write(nexto.getWhereString() + "\n");
		indent(writer, level + 3);
		writer.write(nexto.getCountString() + "\n");
		indent(writer, level + 3);
		writer.write(nexto.getScoreString() + "\n");
		indent(writer, level + 2);

		writer.write("}");
		indent(writer, level );

	}

	/**
	 * Overloads asQuery
	 *
	 * @param map
	 * @param path
	 * @throws IOException
	 */
	public static void asQuery(Map<String, List<InvertedIndex.Result>> map, Path path)
			throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asQuery(map, path, writer, 0);
		}
	}


}
