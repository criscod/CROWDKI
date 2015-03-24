package org.crowdsourcedinterlinking.rcollection;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.crowdsourcedinterlinking.model.Dataset;
import org.crowdsourcedinterlinking.model.Interlink;
import org.crowdsourcedinterlinking.model.Response;
import org.crowdsourcedinterlinking.mpublication.CwdfService;
import org.crowdsourcedinterlinking.mpublication.Service;
import org.crowdsourcedinterlinking.util.ConfigurationManager;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

//import com.hp.hpl.jena.query.ResultSet;

public class CwdfInterlinkingResultReaderImpl implements InterlinkingResultReader {

	private Dataset o1 = null;
	private Dataset o2 = null;

	public CwdfInterlinkingResultReaderImpl(Dataset o1, Dataset o2) {
		this.o1 = o1;
		this.o2 = o2;
	}

	public Set<Interlink> readResponsesOfMicrotask(String microtaskId,
			String type, Service service) {
		// depending on the type of InterlinkGoal the attributes to read should be
		// different

		Set<Interlink> InterlinksResult = new HashSet<Interlink>();

		try {
			// it will be "main" no matter it is InterlinkValidation... or
			// InterlinkIdentification -- the label will be different
			String agg = ConfigurationManager.getInstance()
					.getNameFormElementToWatch();
			/*
			 * if
			 * (type.equals("InterlinkIdentificationWithFullContextJobMicrotaskImpl"
			 * )||type.equals("InterlinkIdentificationJobMicrotaskImpl")) { agg =
			 * "Do you see any connection between Concept A and Concept B"; }
			 * else { if
			 * (type.equals("InterlinkValidationWithFullContextJobMicrotaskImpl"
			 * )||type.equals("InterlinkValidationJobMicrotaskImpl")) { //add
			 * validation agg=""; } }
			 */

			//
			CwdfService cwdf = (CwdfService) service;

			// the job should be checked to guarantee it is finalized!!

			/*
			 * HttpGet get = new
			 * HttpGet(cwdf.getReadUnitsOfJobURL(microtaskId));
			 * get.addHeader("Accept", cwdf.getReadUnitsOfJobAccept());
			 * 
			 * HttpClient client = new DefaultHttpClient();
			 * 
			 * //check when the unit is finalized HttpResponse response =
			 * client.execute(get);
			 * 
			 * Map<String,Map<String,String>> result = new HashMap<String,
			 * Map<String,String>>(); ObjectMapper mapper = new ObjectMapper();
			 * result = mapper.readValue(response.getEntity().getContent(), new
			 * TypeReference<Map<String,Map<String,String>>>() {});
			 * 
			 * for(Map.Entry<String,Map<String,String>> entry :
			 * result.entrySet()) { System.out.println("Unit: "+entry.getKey());
			 * for(Map.Entry<String,String> attr : entry.getValue().entrySet())
			 * { System.out.println(attr.getKey() + " = " + attr.getValue()); }
			 * System.out.println("============================="); }
			 */
			// cwdf.getReadUnitsOfJobURL(microtaskId)


			// Get info about the microtask
			HttpGet get = new HttpGet(cwdf.getReadUnitsOfJobURL(microtaskId));
			System.out.println(cwdf.getReadUnitsOfJobURL(microtaskId));
			// HttpGet get = new
			// HttpGet(cwdf.getReadUnitsOfJobURL(microtaskId));

			get.addHeader("Accept", cwdf.getReadUnitsOfJobAccept());

			HttpClient client = new DefaultHttpClient();

			// check when the unit is finalized
			HttpResponse response = client.execute(get);
			HttpEntity responseEntity = response.getEntity();

			if (responseEntity != null) {

				System.out.println("Started reading ...");
				Map<String, Map<String, Object>> result = new HashMap<String, Map<String, Object>>();
				ObjectMapper mapper = new ObjectMapper();
				result = mapper.readValue(responseEntity.getContent(),
						new TypeReference<Map<String, Map<String, Object>>>() {
						});

				for (Map.Entry<String, Map<String, Object>> entry : result
						.entrySet()) {

					String unitId = entry.getKey(); // only the Unit id is
													// needed

					// storeUnit(unitId, entry.getValue(), microtaskId);

					// System.out.println("Unit: " + unitId);
					boolean added = false;

					// Get info about the unit to check the agg response
					HttpGet get2 = new HttpGet(cwdf.getReadUnitURL(microtaskId,
							unitId));
					get2.addHeader("Accept", cwdf.getReadUnitAccept());
					HttpClient client2 = new DefaultHttpClient();
					HttpResponse response2 = client2.execute(get2);

					HttpEntity responseEntity2 = response2.getEntity();
					if (responseEntity2 != null) {
						Map<String, Object> unitData = new HashMap<String, Object>();

						ObjectMapper mapper2 = new ObjectMapper();
						unitData = mapper2.readValue(
								responseEntity2.getContent(),
								new TypeReference<Map<String, Object>>() {
								});

						String state = "none";
						double confidence = 0.0;
						String aggResponse = null;
						Map<String, Object> results;
						Map<String, Object> aggItem;
						Map<String, String> dataItem;
						String uria = null;
						String urib = null;
						String validationRadioLabel = null;
						double agreement = 0.0;

						for (Map.Entry<String, Object> entry2 : unitData
								.entrySet()) {

							// UNDO COMMENT - JUST NOW
							// storeJudgmentOfUnit(entry2, unitId);

							if (entry2.getKey().equals("results")
									&& entry2.getValue() instanceof Map) {
								results = (Map<String, Object>) entry2
										.getValue();
								// System.out.println("checking results ");
								// System.out.println("================= ");
								for (Map.Entry<String, Object> entry3 : results
										.entrySet()) {
									// depending on the type of microtask I
									// should ask for one or another title of
									// agg answer
									// validation vs identification no?
									//
									// Please select only one of the following
									// possible answers

									if (entry3.getValue() instanceof Map
											&& entry3.getKey().equals(agg)) {
										aggItem = (Map<String, Object>) entry3
												.getValue();

										for (Map.Entry<String, Object> entry4 : aggItem
												.entrySet()) {
											if (entry4.getKey().equals("agg")
													&& entry4.getValue() instanceof String) {
												aggResponse = (String) entry4
														.getValue();
												// System.out.println("aggResponse: "+
												// aggResponse);
											} else if (entry4.getKey().equals(
													"confidence")
													&& entry4.getValue() instanceof Double) {

												confidence = (Double) entry4
														.getValue();
												// System.out.println("confidence: "+
												// confidence);
											}
										}
									}
								}
							} else if (entry2.getValue() instanceof String
									&& entry2.getKey().equals("state")) {
								state = entry2.getValue().toString();
								// System.out.println("state:" + state);
							} else if (entry2.getKey().equals("data")
									&& entry2.getValue() instanceof Map) {
								dataItem = (Map<String, String>) entry2
										.getValue();
								for (Map.Entry<String, String> entry5 : dataItem
										.entrySet()) {
									if (entry5.getKey().equals("uria")) {
										uria = entry5.getValue();
										// System.out.println("uria: " + uria);
									} else if (entry5.getKey().equals("urib")) {
										urib = entry5.getValue();
										// System.out.println("urib: " + urib);
									} else if (entry5.getKey().equals(
											"validationradiolabel")) {
										validationRadioLabel = entry5
												.getValue();
									}

									// for testing: the cases where the job
									// units did not include uria and urib

									/*
									 * if(entry5.getKey().equals("a")) {
									 * 
									 * uria =this.o1.getUri()+"#"+
									 * entry5.getValue(); }else if
									 * (entry5.getKey().equals("b")) {
									 * urib=this.o2.getUri()+"#"+
									 * entry5.getValue(); }
									 */

									added = false;
								}
							} else if (entry2.getKey().equals("agreement")
									&& entry2.getValue() instanceof Double) {
								agreement = (Double) entry2.getValue();
								

							}

							// ask about the state and the confidence
							// confidence >= 0.5
							if (!added && state != null
									& state.equals("finalized")
									&& confidence > 0.5 && uria != null
									&& urib != null)

							{
								Model model = ModelFactory.createDefaultModel();
								Resource uriaResource = model
										.createResource(uria);
								Resource uribResource = model
										.createResource(urib);
                                Property owlsameas = model.createProperty("http://www.w3.org/2002/07/owl#sameAs");
								Interlink map = null;

								// Change the case of yes//similar yes//general
								// yes//specific
								if (type.equals("InterlinkIdentificationWithFullContextJobMicrotaskImpl")
										|| type.equals("InterlinkIdentificationJobMicrotaskImpl")) {
									if (aggResponse.equals(ConfigurationManager
											.getInstance()
											.getResponseValidation())
											|| aggResponse
													.equals(ConfigurationManager
                                                            .getInstance()
                                                            .getResponseIdentificationABSame())) {
										map = new Interlink(uriaResource,
												uribResource, owlsameas, agreement);
									} /*else if (aggResponse
											.equals(ConfigurationManager
                                                    .getInstance()
                                                    .getResponseIdentificationAGeneral())) {
										map = new Interlink(uriaResource,
												uribResource, Relation.GENERAL, agreement);
									} else if (aggResponse
											.equals(ConfigurationManager
                                                    .getInstance()
                                                    .getResponseIdentificationASpecific())) {
										map = new Interlink(uriaResource,
												uribResource, Relation.SPECIFIC, agreement);
									}*/

								} else if (type
										.equals("InterlinkValidationWithFullContextJobMicrotaskImpl")
										|| type.equals("InterlinkValidationJobMicrotaskImpl")) {
									// if ?? and aggResponse yes then similar,
									// if ?? aggResponse yes then general if ??
									// and aggresponse no then specific
									/*if (aggResponse.equals(ConfigurationManager
											.getInstance()
											.getResponseValidation())
											&& validationRadioLabel
													.equals(ConfigurationManager
															.getInstance()
															.getCmlValidationRadioLabelSame())) {
										map = new Interlink(uriaResource,
												uribResource, Relation.SIMILAR, agreement);
									} else if (aggResponse
											.equals(ConfigurationManager
													.getInstance()
													.getResponseValidation())
											&& validationRadioLabel
													.equals(ConfigurationManager
															.getInstance()
															.getCmlValidationRadioLabelGeneral())) {
										map = new Interlink(uriaResource,
												uribResource, Relation.GENERAL, agreement);
									} else if (aggResponse
											.equals(ConfigurationManager
													.getInstance()
													.getResponseValidation())
											&& validationRadioLabel
													.equals(ConfigurationManager
															.getInstance()
															.getCmlValidationRadioLabelSpecific())) {
										map = new Interlink(uriaResource,
												uribResource, Relation.SPECIFIC, agreement);
									}*/
									/*if (aggResponse.equals(ConfigurationManager
											.getInstance()
											.getResponseValidation())
											&& validationRadioLabel
													.equals("yes, they are closely related")) {
										map = new Interlink(uriaResource,
												uribResource, Relation.SIMILAR, agreement);
									} else if (aggResponse
											.equals(ConfigurationManager
													.getInstance()
													.getResponseValidation())
											&& validationRadioLabel
													.equals("yes, they are related")) {
										map = new Interlink(uriaResource,
												uribResource, Relation.SPECIFIC, agreement);
									}*/
								}

								if (map != null) {
									System.out.println("Selected!: " + uria
											+ " " + urib + " " + "=");
									InterlinksResult.add(map);
									added = true;
								}
							}
						}

					}

				}
			}

			// UNDO COMMENT - JUST NOW
			// storeJob(microtaskId, type);

		} catch (Exception e) {
			e.printStackTrace();
		}
		/*
		 * for (Interlink m : InterlinksResult) {
		 * System.out.println(m.getElementA().getURI() + "," +
		 * m.getElementB().getURI() + "," + m.getRelation()); }
		 */
		return InterlinksResult;
	}

	/*
	 * public Alignment createAlignmentFromCrowdFlowerReport(Ontology oA,
	 * Ontology oB, Set<String> filePaths, TypeOfInterlinkGoal typeOfGoal, boolean
	 * context) { Set<Interlink> setOfInterlinks = new HashSet<Interlink>(); Alignment
	 * alignment = new Alignment(oA, oB, setOfInterlinks);
	 * 
	 * // Here I read everything at the same time
	 * 
	 * try {
	 * 
	 * for (String path : filePaths) { File cwdfReport = new File(path);
	 * String[] aggPerUnit;
	 * 
	 * List<String> lines = Files.readLines(cwdfReport,
	 * Charset.defaultCharset());
	 * 
	 * // adhoc for the case- change code if it can be done generally // in
	 * order to put away the line with title lines.remove(0); Model model =
	 * ModelFactory.createDefaultModel();
	 * 
	 * for (String s : lines) { System.out.println("line: " + s); aggPerUnit =
	 * s.split(","); for (int i = 0; i < aggPerUnit.length; i++) { if
	 * (typeOfGoal .equals(TypeOfInterlinkGoal.IDENTIFICATIONB) && context) { //
	 * the comparison with configurationmanager!!
	 * 
	 * // golden & answer.confidence
	 * 
	 * System.out .println("value for the confidence read: " + aggPerUnit[6]);
	 * // "1.0"
	 * 
	 * // double confidence = // Double.parseDouble(aggPerUnit[6].substring(1,
	 * // aggPerUnit[6].length()-2)); double confidence = Double
	 * .parseDouble(aggPerUnit[6]); // String golden =
	 * aggPerUnit[1].substring(1, // aggPerUnit[1].length()-2); String golden =
	 * aggPerUnit[1]; if (golden.equals(false) && confidence >= 0.5) // relevant
	 * // answer { // String elemA = aggPerUnit[7].substring(1, //
	 * aggPerUnit[7].length()-2); String elemA = aggPerUnit[7]; // CHANGE: //
	 * this.crowdAlignment.getOntology1().getNamespace // including the
	 * namespace in the constructor of // Ontology
	 * 
	 * // elemA is a label I have to check for the URI // OR if there is no
	 * label, in the local name of // the URI - I shoul d
	 * 
	 * // String elemB = aggPerUnit[8].substring(1, //
	 * aggPerUnit[8].length()-2); String elemB = aggPerUnit[8];
	 * 
	 * Resource uriElemA = null; Resource uriElemB = null;
	 * 
	 * // String answer = aggPerUnit[5].substring(1, //
	 * aggPerUnit[7].length()-2); String answer = aggPerUnit[5]; if (answer
	 * .equals("Concept A is similar to Concept B")) { // query about elemA real
	 * URI and elemB real // URI String queryString =
	 * "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT ?uria ?urib WHERE {?uria rdfs:label "
	 * + elemA + " . ?urib rdfs:label " + elemB + "}";
	 * 
	 * QueryExecution qe = QueryExecutionFactory .create(queryString, model);
	 * ResultSet results = qe.execSelect(); QuerySolution qs = null;
	 * 
	 * while (results.hasNext()) { qs = results.nextSolution();
	 * 
	 * uriElemA = qs.getResource("uria"); uriElemB = qs.getResource("urib");
	 * 
	 * }// end while results
	 * 
	 * Interlink map = new Interlink(uriElemA, uriElemB, Relation.SIMILAR);
	 * setOfInterlinks.add(map); } // Else --> there is no Interlink to add } } }
	 * 
	 * }
	 * 
	 * }
	 * 
	 * } catch (Exception e) { e.printStackTrace(); } return alignment; }
	 */

	// Method that downloads the CSV info in a zip - for storage purposes

	public Set<Response> readResponsesZip(String microtaskId, Service service) {

		// CHANGE

		// If job is completed and unit state is finalized - even if it will be
		// executed one week after and I will analyse things
		// read list of jobs in the file - check the typeofjob is not null or ""

		Set<Response> setOfResponses = new HashSet<Response>();

		try {
			HttpClient client = new DefaultHttpClient();

			CwdfService cwdf = (CwdfService) service;

			// for testing
			// jobId="94847"; // must be microtaskId

			HttpGet getJob = new HttpGet(
					cwdf.getJudgmentsOfAJobURL(microtaskId));

			// without Accept it also worked
			// getJob.setHeader("Accept", cwdf.getJudgmentsOfAJobAccept());

			HttpResponse response = client.execute(getJob);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				byte[] bytes = EntityUtils.toByteArray(response.getEntity());
				ZipInputStream zip = new ZipInputStream(
						new ByteArrayInputStream(bytes));

				ZipEntry entry = zip.getNextEntry();
				while (entry != null) {
					String readed;
					InputStreamReader reader = new InputStreamReader(zip);
					BufferedReader in = new BufferedReader(reader);

					// reading the CSV file that is downloaded in the ZIP
					File judgmentsFile = new File(ConfigurationManager
							.getInstance()
							.getResultsJudgmentsCsvFileDirectory()
							+ ConfigurationManager.getInstance()
									.getResultsJudgmentsCsvBaseFileName()
							+ microtaskId + ".csv");

					while ((readed = in.readLine()) != null) {
						// it comes in CSV not in JSON
						System.out.println("judgment" + readed);
						Files.append(readed, judgmentsFile,
								Charset.defaultCharset());
						String ls = System.getProperty("line.separator");
						Files.append(ls, judgmentsFile,
								Charset.defaultCharset());

					}
					entry = zip.getNextEntry();
				}
			} else {
				throw new Exception(
						"CrowdFlower did not success in retrieveing all the judgments related to the requested Job "
								+ response.getStatusLine().getReasonPhrase());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return setOfResponses;
	}

	public void controlJobJudgments(String idJob) {
		// for all the jobs with 4 options
		// check whether it is completed - only the ones of 5 will come here
		// if no agreement on 3 answers
		// then extend max to 9 - also configurable!!
	}
}
