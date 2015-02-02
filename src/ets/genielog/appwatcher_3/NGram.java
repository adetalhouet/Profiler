package ets.genielog.appwatcher_3;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class NGram implements Comparable<NGram> {

	/**
	 * Number of time this n-gram is in the trace
	 */
	private int occurrence;

	/**
	 * Store the n-grams key grams separately and regrouped incrementally,
	 * mapped with their redundancy. Ex : n-grams key = "2 3 2" --> 2 - 2 | 3 -
	 * 1 | 2 3 - 1 | 2 3 2 - 1 | Each n-gram has this map
	 */
	private Map<String, Integer> decomposition;

	/**
	 * Constructor
	 */
	public NGram() {
		this.occurrence = 1;
		this.decomposition = new HashMap<String, Integer>();
	}

	/**
	 * Constructor
	 * 
	 * @param occurence
	 *            - occurrence of the given gram
	 * @param decomposition
	 *            - the map containing the n-grams decomposition
	 */
	public NGram(int occurence, Map<String, Integer> decomposition) {
		this.occurrence = occurence;
		this.decomposition = decomposition;
	}

	/**
	 * Increment the occurrence counter.
	 */
	public void incrementOccurrence() {
		this.occurrence++;
	}

	/**
	 * Map the specified gram with the specified occurrence.
	 * 
	 * @param nGrams
	 *            - the gram
	 * @param occ
	 *            - the occurrence of this n-grams
	 */
	public void put(String nGrams, int occ) {
		this.decomposition.put(nGrams, occ);
	}

	/**
	 * Sort the map in descending order
	 */
	public void sort() {
		this.decomposition = sortByValue(decomposition);
	}

	/**
	 * Return whether this Map contains the specified gram.
	 * 
	 * @param key
	 *            - the key to search for
	 * @return true if the map contains the specified key, false otherwise
	 */
	public boolean containsKey(String key) {
		return this.decomposition.containsKey(key);
	}

	/**
	 * Get the occurrence of the given gram
	 * 
	 * @param key
	 *            - the key
	 * @return the value of the mapping with the specified key
	 */
	public int get(String key) {
		return this.decomposition.get(key);
	}

	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("occurrence : " + occurrence + "\n");
		b.append("decomposition : \n");

		Iterator<Entry<String, Integer>> entries = decomposition.entrySet()
				.iterator();
		while (entries.hasNext()) {
			Entry<String, Integer> entry = entries.next();
			b.append("Key = " + entry.getKey() + ", Value = "
					+ entry.getValue().toString() + " \n");
		}
		return b.toString();
	}

	/**
	 * @return the occurrence
	 */
	public int getOccurrence() {
		return occurrence;
	}

	/**
	 * @return the decomposition
	 */
	public Map<String, Integer> getDecomposition() {
		return decomposition;
	}

	@Override
	public int compareTo(NGram another) {
		if (this.getOccurrence() > another.getOccurrence())
			return -1;
		else if (this.getOccurrence() == another.getOccurrence())
			return 0;
		else
			return 1;
	}

	/**
	 * Sort the map in descending order
	 * 
	 * @param map
	 *            - the map to sort
	 * @return - the sorted map
	 */
	public static <K, V extends Comparable<? super Integer>> Map<String, Integer> sortByValue(
			Map<String, Integer> map) {
		List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(
				map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1,
					Map.Entry<String, Integer> o2) {
				if (o1.getValue() >= o2.getValue())
					return -1;
				else
					return 1;
			}
		});

		Map<String, Integer> result = new LinkedHashMap<String, Integer>();
		for (Map.Entry<String, Integer> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

}
