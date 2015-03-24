package org.crowdsourcedinterlinking.expblogic;

import com.google.common.io.Files;
import org.crowdsourcedinterlinking.model.Dataset;
import org.crowdsourcedinterlinking.model.Interlink;
import org.crowdsourcedinterlinking.model.Interlinking;
import org.crowdsourcedinterlinking.mpublication.CwdfService;
import org.crowdsourcedinterlinking.rcollection.InterlinkingResultProcessor;
import org.crowdsourcedinterlinking.rcollection.InterlinkingResultReader;
import org.crowdsourcedinterlinking.util.ConfigurationManager;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ResultManager {

	// private Set<Microtask> setOfMicrotasksToAnalyse;

	private InterlinkingResultReader resultReader;
	private InterlinkingResultProcessor resultProcessor;

	public ResultManager(InterlinkingResultReader reader, InterlinkingResultProcessor processor) {
		// this.setOfMicrotasksToAnalyse=setMicrotasks;
		this.resultReader = reader;
		this.resultProcessor = processor;

	}

	public void analyseResultsOfTheCrowd() {
		// Ontologies files jobs, readResponses of one Job
		Set<Interlink> setOfMappings = new HashSet<Interlink>();

		CwdfService s = new CwdfService();
		try {

			File directory = new File(ConfigurationManager.getInstance()
					.getTrackDirectory());
			File files[] = directory.listFiles();
			for (File crowdFile : files) // each file represents an alignment
											// between two ontologies
			{
				String filePath = crowdFile.getName();
				if (filePath.startsWith("jobsToAnalyse")
						&& filePath.endsWith(".txt")) {

					List<String> lines = Files.readLines(crowdFile,
							Charset.defaultCharset());

					// Info about ontology 1
					String line0 = lines.get(0);
					String[] ontologyO1Attributes = line0.split(",");
					//TODO check with onto
					Dataset d1 = new Dataset(ontologyO1Attributes[0],
							null, null, null, null, null);

					// Info about ontology 2
					String line1 = lines.get(1);
					String[] ontologyO2Attributes = line1.split(",");
					Dataset d2 = new Dataset(ontologyO2Attributes[0],
							null, null, null, null, null);

					// Info about the generated microtasks for this pair of
					// ontologies
					for (int i = 2; i < lines.size(); i++) // for each microtask
															// created for the
															// alignment of the
															// two ontologies O1
															// and O2
					{
						String lineJobI = lines.get(i);
						String[] jobInfo = lineJobI.split(",");
						String microtaskId = jobInfo[0];
						String microtaskType = jobInfo[1];

						Set<Interlink> microtaskMappings = resultReader
								.readResponsesOfMicrotask(microtaskId,
										microtaskType, s);

						setOfMappings.addAll(microtaskMappings);

						resultReader.readResponsesZip(microtaskId, s);

					}
					Interlinking alignment = new Interlinking(d1, d2, setOfMappings);

					resultProcessor.serialiseInterlinkingToNTriples(alignment);

					/* Eval
					File fCrowd = new File(ConfigurationManager.getInstance()
							.getCrowdAlignmentsDirectory()
							+ ConfigurationManager.getInstance()
									.getCrowdBaseFileName()
							+ d1.getTitle()
							+ d2.getTitle() + ".rdf");
					File fRef = new File(ConfigurationManager.getInstance()
							.getReferenceAlignmentsDirectory()
							+ ConfigurationManager.getInstance()
									.getReferenceBaseFileName()
							+ d1.getTitle()
							+ d2.getTitle() + ".rdf");

					ResultEvaluatorImpl ev = new ResultEvaluatorImpl(fCrowd,
							fRef);
					ev.evaluateResultsFromCrowdPR();
					ev.printResultsInConsole();
					ev.printResultsInFile();*/

				}

			}

			// read results
			// process result

			// save the zip file?
			/*
			 * for (Microtask m: this.setOfMicrotasksToAnalyse) { Set<Response>
			 * setOfResponses = this.resultReader.readResponsesZip(m, s);
			 * System.out.println("responses of job: "); }
			 */

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
