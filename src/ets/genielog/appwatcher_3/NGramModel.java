package ets.genielog.appwatcher_3;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ets.genielog.appwatcher_3.profiling.util.Config;

public class NGramModel extends Model {

	/** Serial version uid */
	private static final long serialVersionUID = -6577825193077133389L;

	/** Map containing the n-grams and their occurrence */
	private Map<String, NGram> cl = new HashMap<String, NGram>();

	/** The n-grams depth */
	private int granularity;

	/**
	 * Constructor
	 * 
	 * @param appname
	 * @param sizengram
	 * @param tHRESHOLD_MODEL
	 */
	public NGramModel(String appname, int sizengram, double tHRESHOLD_MODEL) {
		super(appname, ModelManager.TREE_MODEL, sizengram, 0.1, tHRESHOLD_MODEL);

		this.granularity = sizengram;
	}

	/**
	 * Constructor
	 * 
	 * @param appname2
	 * @param sizengram
	 * @param nGRAM_THRESHOLD
	 * @param tHRESHOLD_MODEL
	 */
	public NGramModel(String appname2, int sizengram, double nGRAM_THRESHOLD,
			double tHRESHOLD_MODEL) {
		super(appname2, sizengram, nGRAM_THRESHOLD, tHRESHOLD_MODEL);

		this.granularity = sizengram;
	}

	@Override
	public void makeModel(ArrayList<Trace> traces) {

		// Define the loop iterator
		int traceNumber = 0;

		// Loop on all traces
		while (traceNumber < traces.size()) {

			// Take only 70% of the given trace
			// (70 * traces.get(traceNumber).sizeID()) / 100;

			// Take all traces
			int traceToConsume = traces.get(traceNumber).sizeID();

			// Loop on all traces' elements
			for (int traceElement = 0; traceElement < traceToConsume
					- granularity + 1; traceElement++) {

				NGram nGrams = new NGram();

				// To build the n-grams key
				StringBuilder nGram_keyBuilder = new StringBuilder();
				// Temporary list to save the grams value, per n-grams
				ArrayList<Integer> tp = new ArrayList<Integer>();

				// get the n-grams depending on the granularity
				for (int offset = 0; offset < granularity; offset++) {

					// Retrieve the gram value from the trace
					int gram_value = traces.get(traceNumber).getID(
							traceElement + offset);
					// Add the gram value to build the n-grams array
					nGram_keyBuilder.append(gram_value);
					// Separate each gram with a comma
					if (offset != granularity - 1)
						nGram_keyBuilder.append(",");

					// Add the gram value to temporary list
					tp.add(gram_value);

					// Either add or increment occurrence of the gram
					processGram(nGrams, Integer.toString(gram_value));

					// The first gram has already been added above
					if (offset != 0) {
						/*
						 * To build the array containing incremental gram value
						 * Ex : n-gram key = "2 3 2" --> 2 | 3 | 2 3 | 2 3 2
						 */
						StringBuilder b = new StringBuilder();
						for (int l = 0; l < tp.size(); l++) {
							b.append(tp.get(l));
							if (l != tp.size() - 1)
								b.append(",");
						}
						// Either add or increment occurrence of the gram
						processGram(nGrams, b.toString());
					}

					// Sort the array
					// nGrams.sort();
				}

				// Contain values of grams depending on the granularity
				// Example : if granularity = 2, n-grams key =
				// "grams[0] grams[1]"
				String nGram_key = nGram_keyBuilder.toString();

				// check if the n-grams key already exist in the map
				if (!cl.containsKey(nGram_key))
					// if not, add it
					cl.put(nGram_key, nGrams);
				else
					// if already present, just increment its associated value
					cl.get(nGram_key).incrementOccurrence();
			}
			// Increment loop counter
			traceNumber++;
		}

		// Descending sort
		// cl = sortByValue(cl);

		exportModel();
	}

	/**
	 * This method either add the gram to the n-grams list, or increments the
	 * gram occurrence, if already in the list.
	 * 
	 * @param nGrams
	 *            - the n-grams object
	 * @param gram
	 *            - the gram to process
	 */
	private void processGram(NGram nGrams, String gram) {
		// Check if the gram is in the n-grams decomposition
		// list
		if (!nGrams.containsKey(gram))
			// if not, add the value
			nGrams.put(gram, 1);
		else
			// increment the occurrence
			nGrams.put(gram, nGrams.get(gram) + 1);
	}

	private void exportModel() {
		try {
			String path = Config.PROCESS_PATH + "model.csv";
			File file = new File(path);
			BufferedWriter out;

			out = new BufferedWriter(new FileWriter(file, true));

			// print result
			Iterator<Entry<String, NGram>> entries = cl.entrySet().iterator();
			while (entries.hasNext()) {
				Entry<String, NGram> entry = entries.next();

				out.write(entry.getKey() + " "
						+ entry.getValue().getOccurrence() + "\n");

				for (Map.Entry<String, Integer> entry_decomposition : entry
						.getValue().getDecomposition().entrySet()) {

					out.write(" " + " " + entry_decomposition.getKey() + " "
							+ entry_decomposition.getValue() + "\n");

				}

			}

			out.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String getModelData() {
		StringBuilder b = new StringBuilder();
		// print result
		Iterator<Entry<String, NGram>> entries = cl.entrySet().iterator();
		while (entries.hasNext()) {
			Entry<String, NGram> entry = entries.next();
			b.append(entry.getValue().toString());
		}
		return b.toString();
	}

	@Override
	public Boolean scanTraces(ArrayList<Trace> traces, int nGRAM_SIZE,
			double nGRAM_THRESHOLD, double tHRESHOLD_MODEL) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean scanTracesB(ArrayList<Trace> traces, int nGRAM_SIZE,
			double nGRAM_THRESHOLD, double tHRESHOLD_MODEL) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Sort the map in descending order.
	 * 
	 * @param map
	 *            - the map to sort
	 * @return - the sorted map
	 */
	public static <K, V extends Comparable<? super NGram>> Map<String, NGram> sortByValue(
			Map<String, NGram> map) {
		List<Map.Entry<String, NGram>> list = new LinkedList<Map.Entry<String, NGram>>(
				map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, NGram>>() {

			public int compare(Map.Entry<String, NGram> o1,
					Map.Entry<String, NGram> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}

		});

		Map<String, NGram> result = new LinkedHashMap<String, NGram>();
		for (Map.Entry<String, NGram> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
}
